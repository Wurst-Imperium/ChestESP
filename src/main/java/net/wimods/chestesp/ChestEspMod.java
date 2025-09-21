/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.entity.vehicle.ChestRaft;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.wimods.chestesp.util.ChunkUtils;
import net.wimods.chestesp.util.LootrModCompat;
import net.wimods.chestesp.util.PlausibleAnalytics;
import net.wimods.chestesp.util.RenderUtils;

@Mod(ChestEspMod.MODID)
public final class ChestEspMod
{
	public static final String MODID = "chestesp";
	private static ChestEspMod instance;
	
	public static final Logger LOGGER = LoggerFactory.getLogger("ChestESP");
	
	private final ConfigHolder<ChestEspConfig> configHolder;
	private final PlausibleAnalytics plausible;
	private final ChestEspGroupManager groups;
	private final KeyMapping toggleKey;
	
	private boolean enabled;
	
	public ChestEspMod(IEventBus modBus, ModContainer container)
	{
		LOGGER.info("Starting ChestESP...");
		if(instance != null)
			throw new RuntimeException("ChestESP constructor ran twice!");
		
		instance = this;
		
		configHolder = AutoConfig.register(ChestEspConfig.class,
			GsonConfigSerializer::new);
		
		// Register the config screen
		container.registerExtensionPoint(IConfigScreenFactory.class,
			(mc, screen) -> {
				ChestEspMod.getInstance().getPlausible().pageview("/config");
				return AutoConfig.getConfigScreen(ChestEspConfig.class, screen)
					.get();
			});
		
		groups = new ChestEspGroupManager(configHolder);
		
		toggleKey = new KeyMapping("key.chestesp.toggle",
			InputConstants.UNKNOWN.getValue(), "ChestESP");
		
		plausible = new PlausibleAnalytics(configHolder, groups, toggleKey);
		plausible.pageview("/");
		
		// Register mod bus events
		modBus.addListener(this::onRegisterKeyMappings);
		
		// Register NeoForge bus events
		NeoForge.EVENT_BUS.addListener(this::onClientTick);
	}
	
	@SubscribeEvent
	private void onRegisterKeyMappings(RegisterKeyMappingsEvent event)
	{
		event.register(toggleKey);
	}
	
	@SubscribeEvent
	private void onClientTick(ClientTickEvent.Post event)
	{
		boolean enabled = configHolder.get().enable;
		while(toggleKey.consumeClick())
			setEnabled(!enabled);
	}
	
	public void setEnabled(boolean enabled)
	{
		if(this.enabled == enabled)
			return;
		
		LOGGER.info("{} ChestESP.", enabled ? "Enabling" : "Disabling");
		
		this.enabled = enabled;
		
		if(!enabled)
			groups.allGroups.forEach(ChestEspGroup::clear);
		
		if(configHolder.get().enable != enabled)
		{
			configHolder.get().enable = enabled;
			configHolder.save();
		}
	}
	
	public void onUpdate()
	{
		setEnabled(configHolder.get().enable);
		if(!isEnabled())
			return;
		
		groups.allGroups.forEach(ChestEspGroup::clear);
		
		ChunkUtils.getLoadedBlockEntities().forEach(blockEntity -> {
			if(blockEntity instanceof TrappedChestBlockEntity)
				groups.trapChests.add(blockEntity);
			else if(blockEntity instanceof ChestBlockEntity)
				groups.basicChests.add(blockEntity);
			else if(blockEntity instanceof EnderChestBlockEntity)
				groups.enderChests.add(blockEntity);
			else if(blockEntity instanceof ShulkerBoxBlockEntity
				|| LootrModCompat.isLootrShulkerBox(blockEntity))
				groups.shulkerBoxes.add(blockEntity);
			else if(blockEntity instanceof BarrelBlockEntity
				|| LootrModCompat.isLootrBarrel(blockEntity))
				groups.barrels.add(blockEntity);
			else if(blockEntity instanceof DecoratedPotBlockEntity)
				groups.pots.add(blockEntity);
			else if(blockEntity instanceof HopperBlockEntity)
				groups.hoppers.add(blockEntity);
			else if(blockEntity instanceof DropperBlockEntity)
				groups.droppers.add(blockEntity);
			else if(blockEntity instanceof DispenserBlockEntity)
				groups.dispensers.add(blockEntity);
			else if(blockEntity instanceof CrafterBlockEntity)
				groups.crafters.add(blockEntity);
			else if(blockEntity instanceof AbstractFurnaceBlockEntity)
				groups.furnaces.add(blockEntity);
		});
		
		for(Entity entity : Minecraft.getInstance().level
			.entitiesForRendering())
			if(entity instanceof MinecartChest)
				groups.chestCarts.add(entity);
			else if(entity instanceof MinecartHopper)
				groups.hopperCarts.add(entity);
			else if(entity instanceof ChestBoat || entity instanceof ChestRaft)
				groups.chestBoats.add(entity);
	}
	
	public boolean shouldCancelViewBobbing()
	{
		return configHolder.get().style.hasLines();
	}
	
	public void onRender(PoseStack matrixStack, float partialTicks)
	{
		groups.entityGroups.stream().filter(ChestEspGroup::isEnabled)
			.forEach(g -> g.updateBoxes(partialTicks));
		
		ChestEspStyle style = configHolder.get().style;
		if(style.hasBoxes())
			renderBoxes(matrixStack);
		
		if(style.hasLines())
			renderTracers(matrixStack, partialTicks);
	}
	
	private void renderBoxes(PoseStack matrixStack)
	{
		for(ChestEspGroup group : groups.allGroups)
		{
			if(!group.isEnabled())
				continue;
			
			List<AABB> boxes = group.getBoxes();
			int quadsColor = group.getColorI(0x40);
			int linesColor = group.getColorI(0x80);
			
			RenderUtils.drawSolidBoxes(matrixStack, boxes, quadsColor, false);
			RenderUtils.drawOutlinedBoxes(matrixStack, boxes, linesColor,
				false);
		}
	}
	
	private void renderTracers(PoseStack matrixStack, float partialTicks)
	{
		for(ChestEspGroup group : groups.allGroups)
		{
			if(!group.isEnabled())
				continue;
			
			List<AABB> boxes = group.getBoxes();
			List<Vec3> ends = boxes.stream().map(AABB::getCenter).toList();
			int color = group.getColorI(0x80);
			
			RenderUtils.drawTracers(matrixStack, partialTicks, ends, color,
				false);
		}
	}
	
	public static ChestEspMod getInstance()
	{
		return instance;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public ConfigHolder<ChestEspConfig> getConfigHolder()
	{
		return configHolder;
	}
	
	public PlausibleAnalytics getPlausible()
	{
		return plausible;
	}
}
