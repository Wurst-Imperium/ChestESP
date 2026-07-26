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

public enum VanillaTestRig
{
	;
	
	public static void build(ClientGameTestContext context,
		TestSingleplayerContext spContext)
	{
		TestClientLevelContext world = spContext.getClientLevel();
		TestServerContext server = spContext.getServer();
		
		// Top row: normal chests
		runCommand(server, "setblock 5 -53 7 chest");
		runCommand(server, "setblock 3 -53 7 chest[type=right]");
		runCommand(server, "setblock 2 -53 7 chest[type=left]");
		runCommand(server, "setblock 0 -53 7 ender_chest");
		runCommand(server, "setblock -2 -53 7 trapped_chest");
		runCommand(server, "setblock -4 -53 7 trapped_chest[type=right]");
		runCommand(server, "setblock -5 -53 7 trapped_chest[type=left]");
		runCommand(server, "fill 5 -53 6 -5 -53 6 smooth_stone_slab[type=top]");
		
		// Second row: other containers
		runCommand(server, "setblock 5 -55 7 barrel");
		runCommand(server, "setblock 3 -55 7 shulker_box");
		runCommand(server, "setblock 1 -55 7 decorated_pot");
		runCommand(server, "setblock -1 -55 7 furnace");
		runCommand(server, "setblock -3 -55 7 blast_furnace");
		runCommand(server, "setblock -5 -55 7 smoker");
		runCommand(server, "fill 5 -55 6 -5 -55 6 smooth_stone_slab[type=top]");
		
		// Third row: redstone things
		runCommand(server, "setblock 5 -57 7 dispenser");
		runCommand(server, "setblock 3 -57 7 dropper");
		runCommand(server, "setblock 1 -57 7 hopper");
		runCommand(server, "setblock -1 -57 7 crafter");
		runCommand(server, "fill 5 -57 6 -5 -57 6 smooth_stone_slab");
		
		// Fourth row: vehicles
		runCommand(server,
			"summon chest_minecart 5 -59 7 {Rotation:[90f,0f],NoGravity:1b}");
		runCommand(server,
			"summon hopper_minecart 3 -59 7 {Rotation:[90f,0f],NoGravity:1b}");
		runCommand(server,
			"summon oak_chest_boat 1 -59 7 {Rotation:[180f,0f],NoGravity:1b}");
		runCommand(server,
			"summon bamboo_chest_raft -1 -59 7 {Rotation:[180f,0f],NoGravity:1b}");
		runCommand(server, "fill 5 -59 6 -5 -59 6 smooth_stone_slab");
		
		// Wait for the blocks to appear
		BlockPos lastSlabPos = new BlockPos(-5, -59, 6);
		context.waitFor(mc -> mc.level.getBlockState(lastSlabPos)
			.getBlock() == Blocks.SMOOTH_STONE_SLAB);
		context.waitTick();
		world.waitForChunksRender();
	}
	
	public static void test(ClientGameTestContext context)
	{
		waitForScreenshotMatch(context, "ChestESP_default_settings",
			"https://i.imgur.com/5SS5W2T.png");
		
		ChestESPTest.LOGGER.info("Enabling all ChestESP groups");
		ChestESPTest.withConfig(context, config -> {
			config.include_pots = true;
			config.include_hoppers = true;
			config.include_hopper_carts = true;
			config.include_droppers = true;
			config.include_dispensers = true;
			config.include_crafters = true;
			config.include_furnaces = true;
		});
		assertScreenshotEquals(context, "ChestESP_boxes",
			"https://i.imgur.com/lRMaLRU.png");
		
		ChestESPTest.LOGGER.info("Changing style to lines");
		ChestESPTest.withConfig(context, config -> {
			config.style = ChestEspStyle.LINES;
		});
		assertScreenshotEquals(context, "ChestESP_lines",
			"https://i.imgur.com/jhVL1Ne.png");
		
		ChestESPTest.LOGGER.info("Changing style to lines and boxes");
		ChestESPTest.withConfig(context, config -> {
			config.style = ChestEspStyle.LINES_AND_BOXES;
		});
		assertScreenshotEquals(context, "ChestESP_lines_and_boxes",
			"https://i.imgur.com/XiFiGvh.png");
		
		ChestESPTest.LOGGER.info("Changing all color settings");
		ChestESPTest.setRainbowColors(context);
		assertScreenshotEquals(context, "ChestESP_custom_colors",
			"https://i.imgur.com/TBsz8Eq.png");
	}
}
