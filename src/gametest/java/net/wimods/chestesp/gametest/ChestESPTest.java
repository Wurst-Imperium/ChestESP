/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.gametest;

import static net.wimods.chestesp.gametest.WiModsTestHelper.*;

import java.awt.Color;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.MixinEnvironment;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.TestInput;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestClientWorldContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.world.TestWorldBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.world.GameRules;
import net.wimods.chestesp.ChestEspConfig;
import net.wimods.chestesp.ChestEspMod;
import net.wimods.chestesp.ChestEspStyle;

public final class ChestESPTest implements FabricClientGameTest
{
	public static final Logger LOGGER =
		LoggerFactory.getLogger("ChestESP Test");
	
	@Override
	public void runTest(ClientGameTestContext context)
	{
		LOGGER.info("Starting ChestESP Client GameTest");
		hideSplashTexts(context);
		waitForTitleScreenFade(context);
		
		LOGGER.info("Reached title screen");
		assertScreenshotEquals(context, "title_screen",
			"https://i.imgur.com/jOlaDQ8.png");
		
		// Check config values that aren't visible in screenshots
		withConfig(context, config -> {
			if(!config.plausible)
				throw new AssertionError(
					"Plausible should be enabled by default");
		});
		
		LOGGER.info("Clicking mods button");
		context.clickScreenButton("modmenu.title");
		context.waitTick();
		assertScreenshotEquals(context, "mod_menu",
			"https://i.imgur.com/QZpYfaj.png");
		
		LOGGER.info("Clicking configure button");
		TestInput input = context.getInput();
		input.pressKey(GLFW.GLFW_KEY_TAB);
		input.pressKey(GLFW.GLFW_KEY_TAB);
		input.pressKey(GLFW.GLFW_KEY_TAB);
		input.pressKey(GLFW.GLFW_KEY_ENTER);
		assertScreenshotEquals(context, "cloth_config",
			"https://i.imgur.com/ABKhiOT.png");
		
		LOGGER.info("Returning to title screen");
		context.clickScreenButton("gui.cancel");
		context.clickScreenButton("gui.done");
		
		LOGGER.info("Creating test world");
		TestWorldBuilder worldBuilder = context.worldBuilder();
		worldBuilder.adjustSettings(creator -> {
			String mcVersion = SharedConstants.getGameVersion().getName();
			creator.setWorldName("E2E Test " + mcVersion);
			creator.setGameMode(WorldCreator.Mode.CREATIVE);
			creator.getGameRules().get(GameRules.SEND_COMMAND_FEEDBACK)
				.set(false, null);
		});
		
		try(TestSingleplayerContext spContext = worldBuilder.create())
		{
			testInWorld(context, spContext);
			LOGGER.info("Exiting test world");
		}
		
		LOGGER.info("Test complete");
	}
	
	private void testInWorld(ClientGameTestContext context,
		TestSingleplayerContext spContext)
	{
		TestInput input = context.getInput();
		TestClientWorldContext world = spContext.getClientWorld();
		TestServerContext server = spContext.getServer();
		
		LOGGER.info("Teleporting player to world origin");
		runCommand(server, "tp 0 -60 0");
		
		LOGGER.info("Loading chunks");
		context.waitFor(mc -> mc.worldRenderer.getCompletedChunkCount() >= 50);
		world.waitForChunksRender(false);
		
		LOGGER.info("Reached singleplayer world");
		assertScreenshotEquals(context, "in_game",
			"https://i.imgur.com/LpspNgF.png");
		
		LOGGER.info("Recording debug menu");
		input.pressKey(GLFW.GLFW_KEY_F3);
		context.takeScreenshot("debug_menu");
		input.pressKey(GLFW.GLFW_KEY_F3);
		
		LOGGER.info("Opening inventory");
		input.pressKey(GLFW.GLFW_KEY_E);
		assertScreenshotEquals(context, "inventory",
			"https://i.imgur.com/Ra86k3g.png");
		input.pressKey(GLFW.GLFW_KEY_ESCAPE);
		
		LOGGER.info("Opening game menu");
		input.pressKey(GLFW.GLFW_KEY_ESCAPE);
		assertScreenshotEquals(context, "game_menu",
			"https://i.imgur.com/78AXYwv.png");
		input.pressKey(GLFW.GLFW_KEY_ESCAPE);
		
		LOGGER.info("Building test rig");
		buildTestRig(context, spContext);
		assertScreenshotEquals(context, "ChestESP_default_settings",
			"https://i.imgur.com/HZ14uy3.png");
		
		LOGGER.info("Enabling all ChestESP groups");
		withConfig(context, config -> {
			config.include_pots = true;
			config.include_hoppers = true;
			config.include_hopper_carts = true;
			config.include_droppers = true;
			config.include_dispensers = true;
			config.include_crafters = true;
			config.include_furnaces = true;
		});
		assertScreenshotEquals(context, "ChestESP_boxes",
			"https://i.imgur.com/KnEWPg1.png");
		
		LOGGER.info("Changing style to lines");
		withConfig(context, config -> {
			config.style = ChestEspStyle.LINES;
		});
		assertScreenshotEquals(context, "ChestESP_lines",
			"https://i.imgur.com/GoBPMmg.png");
		
		LOGGER.info("Changing style to lines and boxes");
		withConfig(context, config -> {
			config.style = ChestEspStyle.LINES_AND_BOXES;
		});
		assertScreenshotEquals(context, "ChestESP_lines_and_boxes",
			"https://i.imgur.com/w97POFW.png");
		
		LOGGER.info("Changing all color settings");
		withConfig(context, config -> {
			int total = 14;
			config.chest_color = rainbowColor(0, total);
			config.trap_chest_color = rainbowColor(1, total);
			config.ender_chest_color = rainbowColor(2, total);
			config.chest_cart_color = rainbowColor(3, total);
			config.chest_boat_color = rainbowColor(4, total);
			config.barrel_color = rainbowColor(5, total);
			config.pot_color = rainbowColor(6, total);
			config.shulker_box_color = rainbowColor(7, total);
			config.hopper_color = rainbowColor(8, total);
			config.hopper_cart_color = rainbowColor(9, total);
			config.dropper_color = rainbowColor(10, total);
			config.dispenser_color = rainbowColor(11, total);
			config.crafter_color = rainbowColor(12, total);
			config.furnace_color = rainbowColor(13, total);
		});
		assertScreenshotEquals(context, "ChestESP_custom_colors",
			"https://i.imgur.com/cKzmv0s.png");
		
		LOGGER.info("Checking for broken mixins");
		MixinEnvironment.getCurrentEnvironment().audit();
	}
	
	private void buildTestRig(ClientGameTestContext context,
		TestSingleplayerContext spContext)
	{
		TestClientWorldContext world = spContext.getClientWorld();
		TestServerContext server = spContext.getServer();
		
		// Set up background
		runCommand(server, "fill ^-12 ^-1 ^ ^12 ^-1 ^10 stone");
		runCommand(server, "fill ^-12 ^ ^10 ^12 ^12 ^10 stone");
		runCommand(server, "tp ^ ^3 ^");
		runCommand(server, "fill ^ ^-3 ^ ^ ^-1 ^ stone");
		
		// Top row: normal chests
		runCommand(server, "setblock ^5 ^4 ^7 chest");
		runCommand(server, "setblock ^3 ^4 ^7 chest[type=right]");
		runCommand(server, "setblock ^2 ^4 ^7 chest[type=left]");
		runCommand(server, "setblock ^ ^4 ^7 ender_chest");
		runCommand(server, "setblock ^-2 ^4 ^7 trapped_chest");
		runCommand(server, "setblock ^-4 ^4 ^7 trapped_chest[type=right]");
		runCommand(server, "setblock ^-5 ^4 ^7 trapped_chest[type=left]");
		
		// Second row: other containers
		runCommand(server, "setblock ^5 ^2 ^7 barrel");
		runCommand(server, "setblock ^3 ^2 ^7 shulker_box");
		runCommand(server, "setblock ^1 ^2 ^7 decorated_pot");
		runCommand(server, "setblock ^-1 ^2 ^7 furnace");
		runCommand(server, "setblock ^-3 ^2 ^7 blast_furnace");
		runCommand(server, "setblock ^-5 ^2 ^7 smoker");
		
		// Third row: redstone things
		runCommand(server, "setblock ^5 ^ ^7 dispenser");
		runCommand(server, "setblock ^3 ^ ^7 dropper");
		runCommand(server, "setblock ^1 ^ ^7 hopper");
		runCommand(server, "setblock ^-1 ^ ^7 crafter");
		
		// Fourth row: vehicles
		runCommand(server,
			"summon chest_minecart ^5 ^-2 ^7 {Rotation:[90f,0f],NoGravity:1b}");
		runCommand(server,
			"summon hopper_minecart ^3 ^-2 ^7 {Rotation:[90f,0f],NoGravity:1b}");
		runCommand(server,
			"summon chest_boat ^1 ^-2 ^7 {Rotation:[180f,0f],NoGravity:1b}");
		runCommand(server,
			"summon chest_boat ^-1 ^-2 ^7 {Type:\"bamboo\",Rotation:[180f,0f],NoGravity:1b}");
		
		// TODO: Copper chests!
		
		// Wait for the blocks to appear
		context.waitTicks(2);
		world.waitForChunksRender(false);
	}
	
	public static void withConfig(ClientGameTestContext context,
		Consumer<ChestEspConfig> configUpdater)
	{
		context.runOnClient(mc -> {
			configUpdater.accept(
				ChestEspMod.getInstance().getConfigHolder().getConfig());
		});
	}
	
	private static int rainbowColor(int index, int total)
	{
		return Color.HSBtoRGB((float)index / total, 0.8F, 1) & 0xFFFFFF;
	}
}
