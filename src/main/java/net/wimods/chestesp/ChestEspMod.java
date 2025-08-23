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

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.ChestRaftEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.wimods.chestesp.util.ChunkUtils;
import net.wimods.chestesp.util.PlausibleAnalytics;
import net.wimods.chestesp.util.RenderUtils;

public final class ChestEspMod
{
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	public static final Logger LOGGER = LoggerFactory.getLogger("ChestESP");
	
	private final ConfigHolder<ChestEspConfig> configHolder;
	private final PlausibleAnalytics plausible;
	private final ChestEspGroupManager groups;
	private final KeyBinding toggleKey;
	
	private boolean enabled;
	
	public ChestEspMod()
	{
		LOGGER.info("Starting ChestESP...");
		
		configHolder = AutoConfig.register(ChestEspConfig.class,
			GsonConfigSerializer::new);
		
		groups = new ChestEspGroupManager(configHolder);
		
		toggleKey = KeyBindingHelper
			.registerKeyBinding(new KeyBinding("key.chestesp.toggle",
				InputUtil.UNKNOWN_KEY.getCode(), "ChestESP"));
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			boolean enabled = configHolder.get().enable;
			while(toggleKey.wasPressed())
				setEnabled(!enabled);
		});
		
		plausible = new PlausibleAnalytics(configHolder, groups, toggleKey);
		plausible.pageview("/");
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
			else if(blockEntity instanceof ShulkerBoxBlockEntity)
				groups.shulkerBoxes.add(blockEntity);
			else if(blockEntity instanceof BarrelBlockEntity)
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
		
		for(Entity entity : MC.world.getEntities())
			if(entity instanceof ChestMinecartEntity)
				groups.chestCarts.add(entity);
			else if(entity instanceof HopperMinecartEntity)
				groups.hopperCarts.add(entity);
			else if(entity instanceof ChestBoatEntity
				|| entity instanceof ChestRaftEntity)
				groups.chestBoats.add(entity);
	}
	
	public boolean shouldCancelViewBobbing()
	{
		return configHolder.get().style.hasLines();
	}
	
	public void onRender(MatrixStack matrixStack, float partialTicks)
	{
		groups.entityGroups.stream().filter(ChestEspGroup::isEnabled)
			.forEach(g -> g.updateBoxes(partialTicks));
		
		ChestEspStyle style = configHolder.get().style;
		if(style.hasBoxes())
			renderBoxes(matrixStack);
		
		if(style.hasLines())
			renderTracers(matrixStack, partialTicks);
	}
	
	private void renderBoxes(MatrixStack matrixStack)
	{
		for(ChestEspGroup group : groups.allGroups)
		{
			if(!group.isEnabled())
				continue;
			
			List<Box> boxes = group.getBoxes();
			int quadsColor = group.getColorI(0x40);
			int linesColor = group.getColorI(0x80);
			
			RenderUtils.drawSolidBoxes(matrixStack, boxes, quadsColor, false);
			RenderUtils.drawOutlinedBoxes(matrixStack, boxes, linesColor,
				false);
		}
	}
	
	private void renderTracers(MatrixStack matrixStack, float partialTicks)
	{
		for(ChestEspGroup group : groups.allGroups)
		{
			if(!group.isEnabled())
				continue;
			
			List<Box> boxes = group.getBoxes();
			List<Vec3d> ends = boxes.stream().map(Box::getCenter).toList();
			int color = group.getColorI(0x80);
			
			RenderUtils.drawTracers(matrixStack, partialTicks, ends, color,
				false);
		}
	}
	
	public static ChestEspMod getInstance()
	{
		return ChestEspModInitializer.getInstance();
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
