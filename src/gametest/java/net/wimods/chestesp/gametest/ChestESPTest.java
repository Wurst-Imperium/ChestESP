/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.gametest;

import static net.wimods.chestesp.gametest.WiModsTestHelper.*;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.MixinEnvironment;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.TestInput;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.wimods.chestesp.ChestEspConfig;
import net.wimods.chestesp.ChestEspMod;
import net.wimods.chestesp.ChestEspStyle;

public final class ChestESPTest implements FabricClientGameTest
{
	@Override
	public void runTest(ClientGameTestContext context)
	{
		System.out.println("Starting ChestESP Client GameTest");
		context
			.runOnClient(mc -> mc.options.getHideSplashTexts().setValue(true));
		waitForTitleScreenFade(context);
		
		System.out.println("Reached title screen");
		assertScreenshotEquals(context, "title_screen",
			"https://i.imgur.com/egw0IEv.png");
		
		System.out.println("Clicking mods button");
		context.clickScreenButton("modmenu.title");
		assertScreenshotEquals(context, "mod_menu",
			"https://i.imgur.com/PU7EsPS.png");
		
		System.out.println("Clicking configure button");
		TestInput input = context.getInput();
		input.pressKey(GLFW.GLFW_KEY_TAB);
		input.pressKey(GLFW.GLFW_KEY_TAB);
		input.pressKey(GLFW.GLFW_KEY_TAB);
		input.pressKey(GLFW.GLFW_KEY_ENTER);
		assertScreenshotEquals(context, "cloth_config",
			"https://i.imgur.com/MXdxdap.png");
		
		System.out.println("Returning to title screen");
		context.clickScreenButton("gui.cancel");
		context.clickScreenButton("gui.done");
		
		// System.out.println("Clicking singleplayer button");
		// clickButton("menu.singleplayer");
		
		// if(submitAndGet(mc ->
		// !mc.getLevelStorage().getLevelList().isEmpty()))
		// {
		// System.out.println("World list is not empty. Waiting for it");
		// waitForScreen(SelectWorldScreen.class);
		// System.out.println("Reached select world screen");
		// takeScreenshot("select_world_screen");
		// clickButton("selectWorld.create");
		// }
		
		// waitForScreen(CreateWorldScreen.class);
		// System.out.println("Reached create world screen");
		
		// // Set MC version as world name
		// setTextFieldText(0,
		// "E2E Test " + SharedConstants.getGameVersion().name());
		// // Select creative mode
		// clickButton("selectWorld.gameMode");
		// clickButton("selectWorld.gameMode");
		// takeScreenshot("create_world_screen");
		
		// System.out.println("Creating test world");
		// clickButton("selectWorld.create");
		
		// waitForWorldLoad();
		// dismissTutorialToasts();
		// waitForWorldTicks(200);
		// runChatCommand("seed");
		// System.out.println("Reached singleplayer world");
		// takeScreenshot("in_game", Duration.ZERO);
		
		// System.out.println("Building test rig");
		// buildTestRig();
		// waitForWorldTicks(20);
		// clearChat();
		// takeScreenshot("ChestESP_default_settings");
		
		// System.out.println("Enabling all ChestESP groups");
		// updateConfig(config -> {
		// config.include_pots = true;
		// config.include_hoppers = true;
		// config.include_hopper_carts = true;
		// config.include_droppers = true;
		// config.include_dispensers = true;
		// config.include_crafters = true;
		// config.include_furnaces = true;
		// });
		// takeScreenshot("ChestESP_all_boxes");
		
		// System.out.println("Changing style to lines");
		// updateConfig(config -> {
		// config.style = ChestEspStyle.LINES;
		// });
		// takeScreenshot("ChestESP_all_lines");
		
		// System.out.println("Changing style to lines and boxes");
		// updateConfig(config -> {
		// config.style = ChestEspStyle.LINES_AND_BOXES;
		// });
		// takeScreenshot("ChestESP_all_lines_and_boxes");
		
		// System.out.println("Changing all color settings to purple");
		// updateConfig(config -> {
		// config.chest_color = 0xFF00FF;
		// config.trap_chest_color = 0xFF00FF;
		// config.ender_chest_color = 0xFF00FF;
		// config.chest_cart_color = 0xFF00FF;
		// config.chest_boat_color = 0xFF00FF;
		// config.barrel_color = 0xFF00FF;
		// config.pot_color = 0xFF00FF;
		// config.shulker_box_color = 0xFF00FF;
		// config.hopper_color = 0xFF00FF;
		// config.hopper_cart_color = 0xFF00FF;
		// config.dropper_color = 0xFF00FF;
		// config.dispenser_color = 0xFF00FF;
		// config.crafter_color = 0xFF00FF;
		// config.furnace_color = 0xFF00FF;
		// });
		// takeScreenshot("ChestESP_all_purple");
		
		// System.out.println("Opening debug menu");
		// toggleDebugHud();
		// takeScreenshot("debug_menu");
		
		// System.out.println("Closing debug menu");
		// toggleDebugHud();
		
		// System.out.println("Checking for broken mixins");
		// MixinEnvironment.getCurrentEnvironment().audit();
		
		// System.out.println("Opening inventory");
		// openInventory();
		// takeScreenshot("inventory");
		
		// System.out.println("Closing inventory");
		// closeScreen();
		
		// System.out.println("Opening game menu");
		// openGameMenu();
		// takeScreenshot("game_menu");
		
		// System.out.println("Returning to title screen");
		// clickButton("menu.returnToMenu");
		// waitForScreen(TitleScreen.class);
		
		// System.out.println("Stopping the game");
		// clickButton("menu.quit");
	}
	
	private void buildTestRig()
	{
		// Clear area and create platform
		runChatCommand("fill ^-8 ^ ^ ^8 ^10 ^10 air");
		runChatCommand("fill ^-8 ^-1 ^ ^8 ^-1 ^10 stone");
		runChatCommand("fill ^-8 ^ ^10 ^8 ^10 ^10 stone");
		
		// simple storage blocks
		runChatCommand("setblock ^-5 ^ ^5 chest");
		runChatCommand("setblock ^-3 ^ ^5 trapped_chest");
		runChatCommand("setblock ^-1 ^ ^5 ender_chest");
		runChatCommand("setblock ^1 ^ ^5 barrel");
		runChatCommand("setblock ^3 ^ ^5 shulker_box");
		runChatCommand("setblock ^5 ^ ^5 decorated_pot");
		
		// vehicles
		runChatCommand("setblock ^3 ^ ^7 rail");
		runChatCommand("summon chest_minecart ^3 ^ ^7");
		runChatCommand("summon oak_chest_boat ^ ^ ^7");
		runChatCommand("setblock ^-3 ^ ^7 rail");
		runChatCommand("summon hopper_minecart ^-3 ^ ^7");
		
		// redstone and smelting
		runChatCommand("setblock ^-5 ^2 ^7 hopper");
		runChatCommand("setblock ^-3 ^2 ^7 dropper");
		runChatCommand("setblock ^-1 ^2 ^7 dispenser");
		runChatCommand("setblock ^1 ^2 ^7 furnace");
		runChatCommand("setblock ^3 ^2 ^7 blast_furnace");
		runChatCommand("setblock ^5 ^2 ^7 smoker");
		
		// double chests
		runChatCommand("setblock ^-2 ^4 ^7 chest[type=left]");
		runChatCommand("setblock ^-1 ^4 ^7 chest[type=right]");
		runChatCommand("setblock ^1 ^4 ^7 trapped_chest[type=left]");
		runChatCommand("setblock ^2 ^4 ^7 trapped_chest[type=right]");
	}
	
	public static void updateConfig(Consumer<ChestEspConfig> configUpdater)
	{
		submitAndWait(mc -> {
			configUpdater.accept(
				ChestEspMod.getInstance().getConfigHolder().getConfig());
		});
	}
}
