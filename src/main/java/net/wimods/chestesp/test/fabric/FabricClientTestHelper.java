/*
 * Copied from https://github.com/FabricMC/fabric/blob/
 * f17fc976e9d0d6fe6fc7303fb25ca7b24d122c98/fabric-api-base/src/testmodClient/
 * java/net/fabricmc/fabric/test/base/client/FabricClientTestHelper.java,
 * formatted to match our code style, and then ported to NeoForge.
 *
 * TODO: Replace this with a purpose-built WI Mods test helper, since porting a
 * Fabric utility to NeoForge is kind of cursed...
 */
/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wimods.chestesp.test.fabric;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;

// Provides thread safe utils for interacting with a running game.
public final class FabricClientTestHelper
{
	// Added a counter so that screenshots show up in the correct order
	private static final AtomicInteger screenshotCount = new AtomicInteger(0);
	
	public static void waitForLoadingComplete()
	{
		waitFor("Loading to complete", client -> client.getOverlay() == null,
			Duration.ofMinutes(5));
	}
	
	public static void waitForScreen(Class<? extends Screen> screenClass)
	{
		waitFor("Screen %s".formatted(screenClass.getName()),
			client -> client.screen != null
				&& client.screen.getClass() == screenClass);
	}
	
	public static void openGameMenu()
	{
		setScreen(client -> new PauseScreen(true));
		waitForScreen(PauseScreen.class);
	}
	
	public static void openInventory()
	{
		setScreen(client -> new InventoryScreen(
			Objects.requireNonNull(client.player)));
		
		boolean creative = submitAndWait(
			client -> Objects.requireNonNull(client.player).isCreative());
		waitForScreen(creative ? CreativeModeInventoryScreen.class
			: InventoryScreen.class);
	}
	
	public static void closeScreen()
	{
		setScreen(client -> null);
	}
	
	private static void setScreen(Function<Minecraft, Screen> screenSupplier)
	{
		submit(client -> {
			client.setScreen(screenSupplier.apply(client));
			return null;
		});
	}
	
	public static void takeScreenshot(String name)
	{
		takeScreenshot(name, Duration.ofMillis(50));
	}
	
	public static void takeScreenshot(String name, Duration delay)
	{
		// Allow time for any screens to open
		waitFor(delay);
		
		submitAndWait(client -> {
			// Added a counter so that screenshots show up in the correct order
			String count =
				String.format("%02d", screenshotCount.incrementAndGet());
			String filename = count + "_" + name + ".png";
			Screenshot.grab(client.gameDirectory, filename,
				client.getMainRenderTarget(), message -> {});
			return null;
		});
	}
	
	public static void clickScreenButton(String translationKey)
	{
		final String buttonText =
			Component.translatable(translationKey).getString();
		
		waitFor("Click button" + buttonText, client -> {
			final Screen screen = client.screen;
			
			if(screen == null)
				return false;
			
			// Replaced the accessor with an access widener
			for(Renderable drawable : screen.renderables)
			{
				if(drawable instanceof AbstractButton pressableWidget
					&& pressMatchingButton(pressableWidget, buttonText))
					return true;
				
				if(drawable instanceof LayoutElement widget)
					widget.visitWidgets(clickableWidget -> pressMatchingButton(
						clickableWidget, buttonText));
			}
			
			// Was unable to find the button to press
			return false;
		});
	}
	
	private static boolean pressMatchingButton(AbstractWidget widget,
		String text)
	{
		if(widget instanceof Button buttonWidget
			&& text.equals(buttonWidget.getMessage().getString()))
		{
			buttonWidget.onPress();
			return true;
		}
		
		// Replaced the accessor with an access widener
		if(widget instanceof CycleButton<?> buttonWidget
			&& text.equals(buttonWidget.name.getString()))
		{
			buttonWidget.onPress();
			return true;
		}
		
		return false;
	}
	
	public static void waitForWorldTicks(long ticks)
	{
		// Wait for the world to be loaded and get the start ticks
		waitFor("World load",
			client -> client.level != null
				&& !(client.screen instanceof LevelLoadingScreen),
			Duration.ofMinutes(30));
		final long startTicks =
			submitAndWait(client -> client.level.getGameTime());
		waitFor("World load", client -> Objects.requireNonNull(client.level)
			.getGameTime() > startTicks + ticks, Duration.ofMinutes(10));
	}
	
	public static void enableDebugHud()
	{
		submitAndWait(client -> {
			client.gui.getDebugOverlay().toggleOverlay();
			return null;
		});
	}
	
	public static void setPerspective(CameraType perspective)
	{
		submitAndWait(client -> {
			client.options.setCameraType(perspective);
			return null;
		});
	}
	
	// Removed connectToServer()
	
	public static void waitForTitleScreenFade()
	{
		waitFor("Title screen fade", client -> {
			if(!(client.screen instanceof TitleScreen titleScreen))
				return false;
			
			// Replaced the accessor with an access widener
			return !titleScreen.fading;
		});
	}
	
	public static void waitFor(String what, Predicate<Minecraft> predicate)
	{
		waitFor(what, predicate, Duration.ofSeconds(10));
	}
	
	public static void waitFor(String what, Predicate<Minecraft> predicate,
		Duration timeout)
	{
		final LocalDateTime end = LocalDateTime.now().plus(timeout);
		
		while(true)
		{
			boolean result = submitAndWait(predicate::test);
			
			if(result)
				break;
			
			if(LocalDateTime.now().isAfter(end))
				throw new RuntimeException("Timed out waiting for " + what);
			
			waitFor(Duration.ofMillis(50));
		}
	}
	
	private static void waitFor(Duration duration)
	{
		try
		{
			Thread.sleep(duration.toMillis());
			
		}catch(InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("resource")
	private static <T> CompletableFuture<T> submit(
		Function<Minecraft, T> function)
	{
		return Minecraft.getInstance()
			.submit(() -> function.apply(Minecraft.getInstance()));
	}
	
	public static <T> T submitAndWait(Function<Minecraft, T> function)
	{
		return submit(function).join();
	}
}
