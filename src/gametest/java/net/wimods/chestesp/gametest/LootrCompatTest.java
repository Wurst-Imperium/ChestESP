/*
 * Copyright (c) 2023-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.wimods.chestesp.ChestEspStyle;

public final class LootrCompatTest extends SingleplayerTest
{
	public LootrCompatTest(ClientGameTestContext context,
		TestSingleplayerContext spContext)
	{
		super(context, spContext);
	}
	
	@Override
	protected void runImpl()
	{
		logger.info("Testing Lootr compatibility");
		buildTestRig();
		
		logger.info("Enabling all ChestESP groups for Lootr test");
		ChestESPTest.withConfig(context, config -> {
			config.include_pots = true;
			config.include_hoppers = true;
			config.include_hopper_carts = true;
			config.include_droppers = true;
			config.include_dispensers = true;
			config.include_crafters = true;
			config.include_furnaces = true;
		});
		waitForScreenshotMatch("ChestESP_lootr_boxes",
			"https://i.imgur.com/g5gbEAa.png");
		
		logger.info("Changing style to lines for Lootr test");
		ChestESPTest.withConfig(context, config -> {
			config.style = ChestEspStyle.LINES;
		});
		assertScreenshotEquals("ChestESP_lootr_lines",
			"https://i.imgur.com/LImNhH3.png");
		
		logger.info("Changing style to lines and boxes for Lootr test");
		ChestESPTest.withConfig(context, config -> {
			config.style = ChestEspStyle.LINES_AND_BOXES;
		});
		assertScreenshotEquals("ChestESP_lootr_lines_and_boxes",
			"https://i.imgur.com/arRfGL2.png");
		
		logger.info("Changing all color settings for Lootr test");
		ChestESPTest.setRainbowColors(context);
		assertScreenshotEquals("ChestESP_lootr_custom_colors",
			"https://i.imgur.com/PqiNVf0.png");
		
		logger.info("Cleaning up Lootr compatibility test");
		ChestESPTest.resetConfig(context);
		setBlocksAndWait(
			blocks -> blocks.fill(-4, -59, 6, 4, -53, 7, Blocks.AIR));
		context.waitTick();// to trigger ChestEspMod.onUpdate()
	}
	
	private void buildTestRig()
	{
		Block lootrChest = getLootrBlock("lootr_chest");
		Block lootrTrappedChest = getLootrBlock("lootr_trapped_chest");
		Block lootrBarrel = getLootrBlock("lootr_barrel");
		Block lootrShulker = getLootrBlock("lootr_shulker");
		Block lootrInventory = getLootrBlock("lootr_inventory");
		BlockState topSlab = Blocks.SMOOTH_STONE_SLAB.defaultBlockState()
			.setValue(SlabBlock.TYPE, SlabType.TOP);
		
		setBlocksAndWait(blocks -> {
			// Top row: Lootr chests
			blocks.set(4, -55, 7, lootrChest);
			blocks.set(2, -55, 7, chestState(lootrChest, ChestType.RIGHT));
			blocks.set(1, -55, 7, chestState(lootrChest, ChestType.LEFT));
			blocks.set(-1, -55, 7, lootrTrappedChest);
			blocks.set(-3, -55, 7,
				chestState(lootrTrappedChest, ChestType.RIGHT));
			blocks.set(-4, -55, 7,
				chestState(lootrTrappedChest, ChestType.LEFT));
			blocks.fill(4, -55, 6, -4, -55, 6, topSlab);
			
			// Second row: other Lootr containers
			blocks.set(4, -57, 7, lootrBarrel);
			blocks.set(2, -57, 7, lootrShulker);
			blocks.set(0, -57, 7, lootrInventory);
			blocks.set(-2, -57, 7, chestState(lootrInventory, ChestType.RIGHT));
			blocks.set(-3, -57, 7, chestState(lootrInventory, ChestType.LEFT));
			blocks.fill(4, -57, 6, -4, -57, 6, Blocks.SMOOTH_STONE_SLAB);
		});
		context.waitTick();// to trigger ChestEspMod.onUpdate()
	}
	
	private Block getLootrBlock(String path)
	{
		Identifier id = Identifier.fromNamespaceAndPath("lootr", path);
		return BuiltInRegistries.BLOCK.getOptional(id).orElseThrow(
			() -> new IllegalStateException("Missing block " + id));
	}
}
