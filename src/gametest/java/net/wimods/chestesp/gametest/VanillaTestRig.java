/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.gametest;

import static net.wimods.chestesp.gametest.WiModsTestHelper.*;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestClientWorldContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.wimods.chestesp.ChestEspStyle;

public enum VanillaTestRig
{
	;
	
	public static void build(ClientGameTestContext context,
		TestSingleplayerContext spContext)
	{
		TestClientWorldContext world = spContext.getClientWorld();
		TestServerContext server = spContext.getServer();
		
		// Top row: normal chests
		runCommand(server, "setblock ^5 ^4 ^7 chest");
		runCommand(server, "setblock ^3 ^4 ^7 chest[type=right]");
		runCommand(server, "setblock ^2 ^4 ^7 chest[type=left]");
		runCommand(server, "setblock ^ ^4 ^7 ender_chest");
		runCommand(server, "setblock ^-2 ^4 ^7 trapped_chest");
		runCommand(server, "setblock ^-4 ^4 ^7 trapped_chest[type=right]");
		runCommand(server, "setblock ^-5 ^4 ^7 trapped_chest[type=left]");
		runCommand(server,
			"fill ^5 ^4 ^6 ^-5 ^4 ^6 smooth_stone_slab[type=top]");
		
		// Second row: other containers
		runCommand(server, "setblock ^5 ^2 ^7 barrel");
		runCommand(server, "setblock ^3 ^2 ^7 shulker_box");
		runCommand(server, "setblock ^1 ^2 ^7 decorated_pot");
		runCommand(server, "setblock ^-1 ^2 ^7 furnace");
		runCommand(server, "setblock ^-3 ^2 ^7 blast_furnace");
		runCommand(server, "setblock ^-5 ^2 ^7 smoker");
		runCommand(server,
			"fill ^5 ^2 ^6 ^-5 ^2 ^6 smooth_stone_slab[type=top]");
		
		// Third row: redstone things
		runCommand(server, "setblock ^5 ^ ^7 dispenser");
		runCommand(server, "setblock ^3 ^ ^7 dropper");
		runCommand(server, "setblock ^1 ^ ^7 hopper");
		runCommand(server, "setblock ^-1 ^ ^7 crafter");
		runCommand(server, "fill ^5 ^ ^6 ^-5 ^ ^6 smooth_stone_slab");
		
		// Fourth row: vehicles
		runCommand(server,
			"summon chest_minecart ^5 ^-2 ^7 {Rotation:[90f,0f],NoGravity:1b}");
		runCommand(server,
			"summon hopper_minecart ^3 ^-2 ^7 {Rotation:[90f,0f],NoGravity:1b}");
		runCommand(server,
			"summon oak_chest_boat ^1 ^-2 ^7 {Rotation:[180f,0f],NoGravity:1b}");
		runCommand(server,
			"summon bamboo_chest_raft ^-1 ^-2 ^7 {Rotation:[180f,0f],NoGravity:1b}");
		runCommand(server, "fill ^5 ^-2 ^6 ^-5 ^-2 ^6 smooth_stone_slab");
		
		// TODO: Copper chests!
		
		// Wait for the blocks to appear
		context.waitTicks(2);
		world.waitForChunksRender();
	}
	
	public static void test(ClientGameTestContext context)
	{
		assertScreenshotEquals(context, "ChestESP_default_settings",
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
