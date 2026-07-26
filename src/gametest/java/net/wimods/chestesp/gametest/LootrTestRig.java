/*
 * Copyright (c) 2023-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.gametest;

import static net.wimods.chestesp.gametest.WiModsTestHelper.*;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestClientLevelContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.wimods.chestesp.ChestEspStyle;

public enum LootrTestRig
{
	;
	
	public static void build(ClientGameTestContext context,
		TestSingleplayerContext spContext)
	{
		TestClientLevelContext world = spContext.getClientLevel();
		TestServerContext server = spContext.getServer();
		
		// Remove previous test rig and reset config
		runCommand(server, "kill @e[type=!player]");
		runCommand(server, "fill -12 -60 1 12 -48 9 air");
		ChestESPTest.resetConfig(context);
		BlockPos lastSlabPos = new BlockPos(-4, -57, 6);
		context.waitFor(
			mc -> mc.level.getBlockState(lastSlabPos).getBlock() == Blocks.AIR);
		
		// Top row: lootr chests
		runCommand(server, "setblock 4 -55 7 lootr:chest");
		runCommand(server, "setblock 2 -55 7 lootr:chest[type=right]");
		runCommand(server, "setblock 1 -55 7 lootr:chest[type=left]");
		runCommand(server, "setblock -1 -55 7 lootr:trapped_chest");
		runCommand(server, "setblock -3 -55 7 lootr:trapped_chest[type=right]");
		runCommand(server, "setblock -4 -55 7 lootr:trapped_chest[type=left]");
		runCommand(server, "fill 4 -55 6 -4 -55 6 smooth_stone_slab[type=top]");
		
		// Second row: other lootr containers
		runCommand(server, "setblock 4 -57 7 lootr:barrel");
		runCommand(server, "setblock 2 -57 7 lootr:shulker_box");
		runCommand(server, "fill 4 -57 6 -4 -57 6 smooth_stone_slab");
		
		// Wait for the blocks to appear
		context.waitFor(mc -> mc.level.getBlockState(lastSlabPos)
			.getBlock() == Blocks.SMOOTH_STONE_SLAB);
		context.waitTick();
		world.waitForChunksRender();
	}
	
	public static void test(ClientGameTestContext context)
	{
		ChestESPTest.LOGGER.info("Enabling all ChestESP groups for Lootr test");
		ChestESPTest.withConfig(context, config -> {
			config.include_pots = true;
			config.include_hoppers = true;
			config.include_hopper_carts = true;
			config.include_droppers = true;
			config.include_dispensers = true;
			config.include_crafters = true;
			config.include_furnaces = true;
		});
		waitForScreenshotMatch(context, "ChestESP_lootr_boxes",
			"https://i.imgur.com/2rPHCHV.png");
		
		ChestESPTest.LOGGER.info("Changing style to lines for Lootr test");
		ChestESPTest.withConfig(context, config -> {
			config.style = ChestEspStyle.LINES;
		});
		assertScreenshotEquals(context, "ChestESP_lootr_lines",
			"https://i.imgur.com/Rr8vkkh.png");
		
		ChestESPTest.LOGGER
			.info("Changing style to lines and boxes for Lootr test");
		ChestESPTest.withConfig(context, config -> {
			config.style = ChestEspStyle.LINES_AND_BOXES;
		});
		assertScreenshotEquals(context, "ChestESP_lootr_lines_and_boxes",
			"https://i.imgur.com/cqzWrnm.png");
		
		ChestESPTest.LOGGER.info("Changing all color settings for Lootr test");
		ChestESPTest.setRainbowColors(context);
		assertScreenshotEquals(context, "ChestESP_lootr_custom_colors",
			"https://i.imgur.com/cypSFCl.png");
	}
}
