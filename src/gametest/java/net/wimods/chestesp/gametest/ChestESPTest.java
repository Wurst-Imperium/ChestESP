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

public final class ChestESPTest implements FabricClientGameTest
{
	public static final Logger LOGGER =
		LoggerFactory.getLogger("ChestESP Test");
	
	public static final boolean IS_LOOTR_TEST =
		System.getProperty("chestesp.withLootr") != null;
	
	@Override
	public void runTest(ClientGameTestContext context)
	{
		LOGGER.info("Starting ChestESP Client GameTest");
		hideSplashTexts(context);
		waitForTitleScreenFade(context);
		
		LOGGER.info("Reached title screen");
		assertScreenshotEquals(context, "title_screen",
			"https://i.imgur.com/egw0IEv.png");
		
		// Check config values that aren't visible in screenshots
		withConfig(context, config -> {
			if(!config.plausible)
				throw new AssertionError(
					"Plausible should be enabled by default");
		});
		
		LOGGER.info("Clicking mods button");
		context.clickScreenButton("modmenu.title");
		if(IS_LOOTR_TEST)
			assertScreenshotEquals(context, "mod_menu",
				"https://i.imgur.com/Q1IyYQG.png");
		else
			assertScreenshotEquals(context, "mod_menu",
				"https://i.imgur.com/PU7EsPS.png");
		
		LOGGER.info("Clicking configure button");
		TestInput input = context.getInput();
		input.pressKey(GLFW.GLFW_KEY_TAB);
		input.pressKey(GLFW.GLFW_KEY_TAB);
		input.pressKey(GLFW.GLFW_KEY_TAB);
		input.pressKey(GLFW.GLFW_KEY_ENTER);
		assertScreenshotEquals(context, "cloth_config",
			"https://i.imgur.com/MXdxdap.png");
		
		LOGGER.info("Returning to title screen");
		context.clickScreenButton("gui.cancel");
		context.clickScreenButton("gui.done");
		
		LOGGER.info("Creating test world");
		TestWorldBuilder worldBuilder = context.worldBuilder();
		worldBuilder.adjustSettings(creator -> {
			String mcVersion = SharedConstants.getGameVersion().name();
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
		world.waitForChunksRender();
		
		LOGGER.info("Reached singleplayer world");
		assertScreenshotEquals(context, "in_game",
			"https://i.imgur.com/XrGpZZV.png");
		
		LOGGER.info("Recording debug menu");
		input.pressKey(GLFW.GLFW_KEY_F3);
		context.takeScreenshot("debug_menu");
		input.pressKey(GLFW.GLFW_KEY_F3);
		
		LOGGER.info("Opening inventory");
		input.pressKey(GLFW.GLFW_KEY_E);
		assertScreenshotEquals(context, "inventory",
			"https://i.imgur.com/9barXvh.png");
		input.pressKey(GLFW.GLFW_KEY_ESCAPE);
		
		LOGGER.info("Opening game menu");
		input.pressKey(GLFW.GLFW_KEY_ESCAPE);
		assertScreenshotEquals(context, "game_menu",
			"https://i.imgur.com/rNf37jd.png");
		input.pressKey(GLFW.GLFW_KEY_ESCAPE);
		
		LOGGER.info("Building vanilla test rig");
		runCommand(server, "tp ^ ^3 ^");
		runCommand(server, "fill ^ ^-3 ^ ^ ^-1 ^ smooth_stone");
		VanillaTestRig.build(context, spContext);
		VanillaTestRig.test(context);
		
		LOGGER.info("Checking for broken mixins");
		MixinEnvironment.getCurrentEnvironment().audit();
	}
	
	public static void withConfig(ClientGameTestContext context,
		Consumer<ChestEspConfig> configUpdater)
	{
		context.runOnClient(mc -> {
			configUpdater.accept(
				ChestEspMod.getInstance().getConfigHolder().getConfig());
		});
	}
	
	public static void setRainbowColors(ClientGameTestContext context)
	{
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
	}
	
	private static int rainbowColor(int index, int total)
	{
		return Color.HSBtoRGB((float)index / total, 0.8F, 1) & 0xFFFFFF;
	}
}
