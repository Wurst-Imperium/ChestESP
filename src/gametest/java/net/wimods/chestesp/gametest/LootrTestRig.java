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
import net.minecraft.world.level.block.Blocks;
import net.wimods.chestesp.ChestEspStyle;

public enum LootrTestRig
{
	;
	
	public static void build(ClientGameTestContext context,
		TestSingleplayerContext spContext)
	{
		TestClientWorldContext world = spContext.getClientWorld();
		TestServerContext server = spContext.getServer();
		
		// Remove previous test rig and reset config
		runCommand(server, "kill @e[type=!player]");
		runCommand(server, "fill ~-12 ~-3 ~1 ~12 ~9 ~9 air");
		ChestESPTest.resetConfig(context);
		context.waitFor(mc -> mc.level
			.getBlockState(mc.player.blockPosition().offset(-4, 0, 6))
			.getBlock() == Blocks.AIR);
		
		// Top row: lootr chests
		runCommand(server, "setblock ~4 ~2 ~7 lootr:lootr_chest");
		runCommand(server, "setblock ~2 ~2 ~7 lootr:lootr_chest[type=right]");
		runCommand(server, "setblock ~1 ~2 ~7 lootr:lootr_chest[type=left]");
		runCommand(server, "setblock ~-1 ~2 ~7 lootr:lootr_trapped_chest");
		runCommand(server,
			"setblock ~-3 ~2 ~7 lootr:lootr_trapped_chest[type=right]");
		runCommand(server,
			"setblock ~-4 ~2 ~7 lootr:lootr_trapped_chest[type=left]");
		runCommand(server,
			"fill ~4 ~2 ~6 ~-4 ~2 ~6 smooth_stone_slab[type=top]");
		
		// Second row: other lootr containers
		runCommand(server, "setblock ~4 ~ ~7 lootr:lootr_barrel");
		runCommand(server, "setblock ~2 ~ ~7 lootr:lootr_shulker");
		runCommand(server, "setblock ~ ~ ~7 lootr:lootr_inventory");
		runCommand(server,
			"setblock ~-2 ~ ~7 lootr:lootr_inventory[type=right]");
		runCommand(server,
			"setblock ~-3 ~ ~7 lootr:lootr_inventory[type=left]");
		runCommand(server, "fill ~4 ~ ~6 ~-4 ~ ~6 smooth_stone_slab");
		
		// Wait for the blocks to appear
		context.waitFor(mc -> mc.level
			.getBlockState(mc.player.blockPosition().offset(-4, 0, 6))
			.getBlock() == Blocks.SMOOTH_STONE_SLAB);
		context.waitTick();
		world.waitForChunksRender(false);
	}
	
	public static void test(ClientGameTestContext context)
	{
		// For some reason the first screenshot you take in 1.21.1 looks
		// completely wrong, but every screenshot after that is fine.
		context.takeScreenshot("weird_workaround_lootr");
		
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
		assertScreenshotEquals(context, "ChestESP_lootr_boxes",
			"https://i.imgur.com/lraf97p.png");
		
		ChestESPTest.LOGGER.info("Changing style to lines for Lootr test");
		ChestESPTest.withConfig(context, config -> {
			config.style = ChestEspStyle.LINES;
		});
		assertScreenshotEquals(context, "ChestESP_lootr_lines",
			"https://i.imgur.com/xj1MHgq.png");
		
		ChestESPTest.LOGGER
			.info("Changing style to lines and boxes for Lootr test");
		ChestESPTest.withConfig(context, config -> {
			config.style = ChestEspStyle.LINES_AND_BOXES;
		});
		assertScreenshotEquals(context, "ChestESP_lootr_lines_and_boxes",
			"https://i.imgur.com/SaK6Y9Z.png");
		
		ChestESPTest.LOGGER.info("Changing all color settings for Lootr test");
		ChestESPTest.setRainbowColors(context);
		assertScreenshotEquals(context, "ChestESP_lootr_custom_colors",
			"https://i.imgur.com/acr8fZI.png");
	}
}
