/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforgespi.language.IModInfo;
import net.wimods.chestesp.ChestEspConfig;
import net.wimods.chestesp.ChestEspGroup;
import net.wimods.chestesp.ChestEspGroupManager;

/**
 * An implementation of the Plausible Events API for privacy-friendly
 * analytics in Minecraft mods, without collecting any personal information.
 *
 * <p>
 * See {@link https://plausible.io/docs/events-api} for technical details and
 * {@link https://plausible.io/privacy-focused-web-analytics} for a
 * non-technical overview of how Plausible works.
 */
public final class PlausibleAnalytics
{
	private static final Gson GSON = new Gson();
	private static final Logger LOGGER = LoggerFactory.getLogger("Plausible");
	
	private static final String MOD_ID = "chestesp";
	private static final URI API_ENDPOINT =
		URI.create("https://plausible.wurstimperium.net/api/event");
	
	private final HttpClient httpClient =
		HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
	private final LinkedBlockingQueue<PlausibleEvent> eventQueue =
		new LinkedBlockingQueue<>();
	private final JsonObject sessionProps = new JsonObject();
	private final ConfigHolder<ChestEspConfig> configHolder;
	private final ChestEspGroupManager groups;
	private final KeyMapping toggleKey;
	
	/**
	 * Creates a new PlausibleAnalytics instance and starts a background thread
	 * for sending events.
	 */
	public PlausibleAnalytics(ConfigHolder<ChestEspConfig> configHolder,
		ChestEspGroupManager groups, KeyMapping toggleKey)
	{
		this.configHolder = Objects.requireNonNull(configHolder);
		this.groups = Objects.requireNonNull(groups);
		this.toggleKey = Objects.requireNonNull(toggleKey);
		
		sessionProp("version", getVersion("chestesp"));
		sessionProp("short_version", getShortVersion("chestesp"));
		sessionProp("mc_version", getVersion("minecraft"));
		sessionProp("mod_loader", "neoforge");
		sessionProp("neoforge_version", getVersion("neoforge"));
		sessionProp("cloth_config_version", getVersion("cloth-config"));
		sessionProp("sodium_version", getVersion("sodium"));
		sessionProp("sinytra_connector_version", getVersion("connector"));
		
		Thread.ofPlatform().daemon().name("Plausible")
			.start(this::runBackgroundLoop);
		
		NeoForge.EVENT_BUS.addListener(this::onWorldChange);
	}
	
	private String getVersion(String modId)
	{
		return ModList.get().getModContainerById(modId)
			.map(ModContainer::getModInfo).map(IModInfo::getVersion)
			.map(ArtifactVersion::toString).orElse(null);
	}
	
	private String getShortVersion(String modId)
	{
		String version = getVersion(modId);
		if(version != null && version.contains("-MC"))
			return version.substring(0, version.indexOf("-MC"));
		
		return version;
	}
	
	@SubscribeEvent
	public void onWorldChange(ClientPlayerNetworkEvent.LoggingIn event)
	{
		Minecraft client = Minecraft.getInstance();
		String path = getPathForServer(client.getCurrentServer());
		LinkedHashMap<String, String> props = new LinkedHashMap<>();
		props.put("enabled", "" + configHolder.get().enable);
		props.put("style", configHolder.get().style.name());
		if(!toggleKey.isUnbound())
			props.put("toggle_key", toggleKey.saveString());
		for(ChestEspGroup group : groups.allGroups)
		{
			String key = group.getName() + "_color";
			String value = group.isEnabled() ? group.getColorHex() : "off";
			props.put(key, value);
		}
		pageview(path, props);
	}
	
	private String getPathForServer(ServerData server)
	{
		if(server == null)
			return "/singleplayer";
		if(server.isLan())
			return "/lan";
		if(server.isRealm())
			return "/realms";
		return "/multiplayer";
	}
	
	private boolean isEnabled()
	{
		return configHolder.get().plausible;
	}
	
	private boolean isDebugMode()
	{
		return !FMLLoader.isProduction();
	}
	
	private void runBackgroundLoop()
	{
		while(!Thread.currentThread().isInterrupted())
		{
			try
			{
				sendEvent(eventQueue.take());
				Thread.sleep(50);
				
			}catch(InterruptedException e)
			{
				Thread.currentThread().interrupt();
				break;
				
			}catch(Exception e)
			{
				LOGGER.error("Plausible error", e);
			}
		}
	}
	
	private void sendEvent(PlausibleEvent event)
	{
		String body = createRequestBody(event);
		if(isDebugMode())
		{
			LOGGER.info("Event ({} props): {}", event.props().size(), body);
			return;
		}
		
		HttpRequest request = HttpRequest.newBuilder().uri(API_ENDPOINT)
			.header("User-Agent", getUserAgent())
			.header("Content-Type", "application/json")
			.POST(HttpRequest.BodyPublishers.ofString(body))
			.timeout(Duration.ofSeconds(5)).build();
		
		httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
			.exceptionally(ex -> null);
	}
	
	private String getUserAgent()
	{
		// Same as the "Operating System" entry in Minecraft crash reports.
		return System.getProperty("os.name") + " ("
			+ System.getProperty("os.arch") + ") version "
			+ System.getProperty("os.version");
	}
	
	private String createRequestBody(PlausibleEvent event)
	{
		JsonObject body = new JsonObject();
		body.addProperty("name", event.name());
		body.addProperty("url", event.url());
		body.addProperty("domain", MOD_ID);
		if(event.props() != null && !event.props().isEmpty())
			body.add("props", event.props());
		
		return GSON.toJson(body);
	}
	
	/**
	 * Sends a pageview event with the given path.
	 *
	 * <p>
	 * Any session properties set with {@link #sessionProp(String, String)}
	 * will also be included.
	 *
	 * <p>
	 * If Plausible is disabled at the time of this method call, no event will
	 * be sent.
	 */
	public void pageview(String path)
	{
		event("pageview", path, null);
	}
	
	/**
	 * Sends a pageview event with the given path and properties.
	 *
	 * <p>
	 * Any session properties set with {@link #sessionProp(String, String)}
	 * will also be included.
	 *
	 * <p>
	 * The total number of properties is limited to 30. Any additional
	 * properties will be ignored. The length of each property name is limited
	 * to 300 characters and the length of each property value is limited to
	 * 2000 characters. Longer names and values will be truncated.
	 *
	 * <p>
	 * Properties MUST NOT contain any personal information. This includes
	 * usernames, emails, IP addresses and any persistent user IDs, even if
	 * they are randomly generated and/or hashed.
	 *
	 * <p>
	 * If Plausible is disabled at the time of this method call, no event will
	 * be sent.
	 */
	public void pageview(String path, Map<String, String> props)
	{
		event("pageview", path, props);
	}
	
	/**
	 * Sends an event with the given name and path.
	 *
	 * <p>
	 * Any session properties set with {@link #sessionProp(String, String)}
	 * will also be included.
	 *
	 * <p>
	 * If Plausible is disabled at the time of this method call, no event will
	 * be sent.
	 */
	public void event(String name, String path)
	{
		event(name, path, null);
	}
	
	/**
	 * Sends an event with the given name, path and properties.
	 *
	 * <p>
	 * Any session properties set with {@link #sessionProp(String, String)}
	 * will also be included.
	 *
	 * <p>
	 * The total number of properties is limited to 30. Any additional
	 * properties will be ignored. The length of each property name is limited
	 * to 300 characters and the length of each property value is limited to
	 * 2000 characters. Longer names and values will be truncated.
	 *
	 * <p>
	 * Properties MUST NOT contain any personal information. This includes
	 * usernames, emails, IP addresses and any persistent user IDs, even if
	 * they are randomly generated and/or hashed.
	 *
	 * <p>
	 * If Plausible is disabled at the time of this method call, no event will
	 * be sent.
	 */
	public void event(String name, String path, Map<String, String> props)
	{
		if(!isEnabled() || name == null || path == null)
			return;
		
		String url = buildURL(path);
		JsonObject jsonProps = buildJsonProps(props);
		eventQueue.offer(new PlausibleEvent(name, url, jsonProps));
	}
	
	private String buildURL(String path)
	{
		String adjustedPath = path.startsWith("/") ? path : "/" + path;
		return "mod://" + MOD_ID + adjustedPath;
	}
	
	private JsonObject buildJsonProps(Map<String, String> props)
	{
		JsonObject jsonProps = sessionProps.deepCopy();
		if(props == null || props.isEmpty())
			return jsonProps;
		
		for(Map.Entry<String, String> entry : props.entrySet())
		{
			String key = entry.getKey();
			if(isDebugMode() && key.length() > 300)
				LOGGER.warn("Property key is too long ({} characters): {}",
					key.length(), key);
			
			String value = entry.getValue();
			if(isDebugMode() && value.length() > 2000)
				LOGGER.warn("Property value is too long ({} characters): {}",
					value.length(), value);
			
			if(key != null && value != null)
				jsonProps.addProperty(key, value);
		}
		
		if(isDebugMode() && jsonProps.size() > 30)
			LOGGER.warn("Too many properties ({})", jsonProps.size());
		
		return jsonProps;
	}
	
	/**
	 * Sets a session property, which will be included in all subsequent events.
	 *
	 * <p>
	 * The total number of properties is limited to 30. Any additional
	 * properties will be ignored. The length of each property name is limited
	 * to 300 characters and the length of each property value is limited to
	 * 2000 characters. Longer names and values will be truncated.
	 *
	 * <p>
	 * Properties MUST NOT contain any personal information. This includes
	 * usernames, emails, IP addresses and any persistent user IDs, even if
	 * they are randomly generated and/or hashed.
	 */
	public void sessionProp(String name, String value)
	{
		if(name != null && value != null)
			sessionProps.addProperty(name, value);
	}
	
	/**
	 * Removes a session property.
	 */
	public void removeSessionProp(String name)
	{
		if(name != null)
			sessionProps.remove(name);
	}
	
	private record PlausibleEvent(String name, String url, JsonObject props)
	{}
}
