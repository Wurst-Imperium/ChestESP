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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.wimods.chestesp.ChestEspStyle;

public final class CopperChestsTest extends SingleplayerTest
{
	// Note: Copper chests are only separate from vanilla ones
	// because there are so many of them. It's not a mod.
	
	public CopperChestsTest(ClientGameTestContext context,
		TestSingleplayerContext spContext)
	{
		super(context, spContext);
	}
	
	@Override
	protected void runImpl()
	{
		logger.info("Testing copper chests");
		buildTestRig();
		
		logger.info("Enabling all ChestESP groups for copper test");
		ChestESPTest.withConfig(context, config -> {
			config.include_pots = true;
			config.include_hoppers = true;
			config.include_hopper_carts = true;
			config.include_droppers = true;
			config.include_dispensers = true;
			config.include_crafters = true;
			config.include_furnaces = true;
		});
		waitForScreenshotMatch("ChestESP_copper_boxes",
			"https://i.imgur.com/OKmAwt9.png");
		
		logger.info("Changing style to lines for copper test");
		ChestESPTest.withConfig(context, config -> {
			config.style = ChestEspStyle.LINES;
		});
		assertScreenshotEquals("ChestESP_copper_lines",
			"https://i.imgur.com/yZuL3EO.png");
		
		logger.info("Changing style to lines and boxes for copper test");
		ChestESPTest.withConfig(context, config -> {
			config.style = ChestEspStyle.LINES_AND_BOXES;
		});
		assertScreenshotEquals("ChestESP_copper_lines_and_boxes",
			"https://i.imgur.com/qksVwWN.png");
		
		logger.info("Changing all color settings for copper test");
		ChestESPTest.setRainbowColors(context);
		assertScreenshotEquals("ChestESP_copper_custom_colors",
			"https://i.imgur.com/r2NafDt.png");
		
		logger.info("Cleaning up copper chest test");
		ChestESPTest.resetConfig(context);
		setBlocksAndWait(
			blocks -> blocks.fill(-4, -59, 6, 4, -53, 7, Blocks.AIR));
		context.waitTick();// to trigger ChestEspMod.onUpdate()
	}
	
	private void buildTestRig()
	{
		BlockState topSlab = Blocks.SMOOTH_STONE_SLAB.defaultBlockState()
			.setValue(SlabBlock.TYPE, SlabType.TOP);
		setBlocksAndWait(blocks -> {
			// Top row: new copper chests
			blocks.set(4, -53, 7, Blocks.COPPER_CHEST);
			blocks.set(2, -53, 7,
				chestState(Blocks.COPPER_CHEST, ChestType.RIGHT));
			blocks.set(1, -53, 7,
				chestState(Blocks.COPPER_CHEST, ChestType.LEFT));
			blocks.set(-1, -53, 7, Blocks.WAXED_COPPER_CHEST);
			blocks.set(-3, -53, 7,
				chestState(Blocks.WAXED_COPPER_CHEST, ChestType.RIGHT));
			blocks.set(-4, -53, 7,
				chestState(Blocks.WAXED_COPPER_CHEST, ChestType.LEFT));
			blocks.fill(4, -53, 6, -4, -53, 6, topSlab);
			
			// Second row: exposed copper chests
			blocks.set(4, -55, 7, Blocks.EXPOSED_COPPER_CHEST);
			blocks.set(2, -55, 7,
				chestState(Blocks.EXPOSED_COPPER_CHEST, ChestType.RIGHT));
			blocks.set(1, -55, 7,
				chestState(Blocks.EXPOSED_COPPER_CHEST, ChestType.LEFT));
			blocks.set(-1, -55, 7, Blocks.WAXED_EXPOSED_COPPER_CHEST);
			blocks.set(-3, -55, 7,
				chestState(Blocks.WAXED_EXPOSED_COPPER_CHEST, ChestType.RIGHT));
			blocks.set(-4, -55, 7,
				chestState(Blocks.WAXED_EXPOSED_COPPER_CHEST, ChestType.LEFT));
			blocks.fill(4, -55, 6, -4, -55, 6, topSlab);
			
			// Third row: weathered copper chests
			blocks.set(4, -57, 7, Blocks.WEATHERED_COPPER_CHEST);
			blocks.set(2, -57, 7,
				chestState(Blocks.WEATHERED_COPPER_CHEST, ChestType.RIGHT));
			blocks.set(1, -57, 7,
				chestState(Blocks.WEATHERED_COPPER_CHEST, ChestType.LEFT));
			blocks.set(-1, -57, 7, Blocks.WAXED_WEATHERED_COPPER_CHEST);
			blocks.set(-3, -57, 7, chestState(
				Blocks.WAXED_WEATHERED_COPPER_CHEST, ChestType.RIGHT));
			blocks.set(-4, -57, 7, chestState(
				Blocks.WAXED_WEATHERED_COPPER_CHEST, ChestType.LEFT));
			blocks.fill(4, -57, 6, -4, -57, 6, Blocks.SMOOTH_STONE_SLAB);
			
			// Fourth row: oxidized copper chests
			blocks.set(4, -59, 7, Blocks.OXIDIZED_COPPER_CHEST);
			blocks.set(2, -59, 7,
				chestState(Blocks.OXIDIZED_COPPER_CHEST, ChestType.RIGHT));
			blocks.set(1, -59, 7,
				chestState(Blocks.OXIDIZED_COPPER_CHEST, ChestType.LEFT));
			blocks.set(-1, -59, 7, Blocks.WAXED_OXIDIZED_COPPER_CHEST);
			blocks.set(-3, -59, 7, chestState(
				Blocks.WAXED_OXIDIZED_COPPER_CHEST, ChestType.RIGHT));
			blocks.set(-4, -59, 7,
				chestState(Blocks.WAXED_OXIDIZED_COPPER_CHEST, ChestType.LEFT));
			blocks.fill(4, -59, 6, -4, -59, 6, Blocks.SMOOTH_STONE_SLAB);
		});
		context.waitTick();// to trigger ChestEspMod.onUpdate()
	}
}
