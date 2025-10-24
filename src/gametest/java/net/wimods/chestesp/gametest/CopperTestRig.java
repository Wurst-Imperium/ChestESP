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

public enum CopperTestRig
{
	;
	
	// Note: Copper chests are only separate from vanilla ones
	// because there are so many of them. It's not a mod.
	
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
		
		// Top row: new copper chests
		runCommand(server, "setblock ~4 ~4 ~7 copper_chest");
		runCommand(server, "setblock ~2 ~4 ~7 copper_chest[type=right]");
		runCommand(server, "setblock ~1 ~4 ~7 copper_chest[type=left]");
		runCommand(server, "setblock ~-1 ~4 ~7 waxed_copper_chest");
		runCommand(server, "setblock ~-3 ~4 ~7 waxed_copper_chest[type=right]");
		runCommand(server, "setblock ~-4 ~4 ~7 waxed_copper_chest[type=left]");
		runCommand(server,
			"fill ~4 ~4 ~6 ~-4 ~4 ~6 smooth_stone_slab[type=top]");
		
		// Second row: exposed copper chests
		runCommand(server, "setblock ~4 ~2 ~7 exposed_copper_chest");
		runCommand(server,
			"setblock ~2 ~2 ~7 exposed_copper_chest[type=right]");
		runCommand(server, "setblock ~1 ~2 ~7 exposed_copper_chest[type=left]");
		runCommand(server, "setblock ~-1 ~2 ~7 waxed_exposed_copper_chest");
		runCommand(server,
			"setblock ~-3 ~2 ~7 waxed_exposed_copper_chest[type=right]");
		runCommand(server,
			"setblock ~-4 ~2 ~7 waxed_exposed_copper_chest[type=left]");
		runCommand(server,
			"fill ~4 ~2 ~6 ~-4 ~2 ~6 smooth_stone_slab[type=top]");
		
		// Third row: weathered copper chests
		runCommand(server, "setblock ~4 ~ ~7 weathered_copper_chest");
		runCommand(server,
			"setblock ~2 ~ ~7 weathered_copper_chest[type=right]");
		runCommand(server,
			"setblock ~1 ~ ~7 weathered_copper_chest[type=left]");
		runCommand(server, "setblock ~-1 ~ ~7 waxed_weathered_copper_chest");
		runCommand(server,
			"setblock ~-3 ~ ~7 waxed_weathered_copper_chest[type=right]");
		runCommand(server,
			"setblock ~-4 ~ ~7 waxed_weathered_copper_chest[type=left]");
		runCommand(server, "fill ~4 ~ ~6 ~-4 ~ ~6 smooth_stone_slab");
		
		// Fourth row: oxidized copper chests
		runCommand(server, "setblock ~4 ~-2 ~7 oxidized_copper_chest");
		runCommand(server,
			"setblock ~2 ~-2 ~7 oxidized_copper_chest[type=right]");
		runCommand(server,
			"setblock ~1 ~-2 ~7 oxidized_copper_chest[type=left]");
		runCommand(server, "setblock ~-1 ~-2 ~7 waxed_oxidized_copper_chest");
		runCommand(server,
			"setblock ~-3 ~-2 ~7 waxed_oxidized_copper_chest[type=right]");
		runCommand(server,
			"setblock ~-4 ~-2 ~7 waxed_oxidized_copper_chest[type=left]");
		runCommand(server, "fill ~4 ~-2 ~6 ~-4 ~-2 ~6 smooth_stone_slab");
		
		// Wait for the blocks to appear
		context.waitFor(mc -> mc.level
			.getBlockState(mc.player.blockPosition().offset(-4, 0, 6))
			.getBlock() == Blocks.SMOOTH_STONE_SLAB);
		context.waitTick();
		world.waitForChunksRender();
	}
	
	public static void test(ClientGameTestContext context)
	{
		ChestESPTest.LOGGER
			.info("Enabling all ChestESP groups for copper test");
		ChestESPTest.withConfig(context, config -> {
			config.include_pots = true;
			config.include_hoppers = true;
			config.include_hopper_carts = true;
			config.include_droppers = true;
			config.include_dispensers = true;
			config.include_crafters = true;
			config.include_furnaces = true;
		});
		assertScreenshotEquals(context, "ChestESP_copper_boxes",
			"https://i.imgur.com/OKmAwt9.png");
		
		ChestESPTest.LOGGER.info("Changing style to lines for Lootr test");
		ChestESPTest.withConfig(context, config -> {
			config.style = ChestEspStyle.LINES;
		});
		assertScreenshotEquals(context, "ChestESP_copper_lines",
			"https://i.imgur.com/yZuL3EO.png");
		
		ChestESPTest.LOGGER
			.info("Changing style to lines and boxes for Lootr test");
		ChestESPTest.withConfig(context, config -> {
			config.style = ChestEspStyle.LINES_AND_BOXES;
		});
		assertScreenshotEquals(context, "ChestESP_copper_lines_and_boxes",
			"https://i.imgur.com/qksVwWN.png");
		
		ChestESPTest.LOGGER.info("Changing all color settings for Lootr test");
		ChestESPTest.setRainbowColors(context);
		assertScreenshotEquals(context, "ChestESP_copper_custom_colors",
			"https://i.imgur.com/r2NafDt.png");
	}
}
