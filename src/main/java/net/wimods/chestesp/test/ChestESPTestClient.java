/*
 * Copyright (c) 2023-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.test;

import static net.wimods.chestesp.test.ChestESPClientTestHelper.*;
import static net.wimods.chestesp.test.ChestESPClientTestHelper.clickScreenButton;
import static net.wimods.chestesp.test.fabric.FabricClientTestHelper.*;
import static net.wimods.chestesp.test.fabric.FabricClientTestHelper.clickScreenButton;

import java.time.Duration;

import org.spongepowered.asm.mixin.MixinEnvironment;

import net.fabricmc.api.ModInitializer;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.wimods.chestesp.ChestEspStyle;

// It would be cleaner to have this test in src/test/java, but remapping that
// into a separate testmod is a whole can of worms.
public final class ChestESPTestClient implements ModInitializer
{
	@Override
	public void onInitialize()
	{
		if(System.getProperty("chestesp.e2eTest") == null)
			return;
		
		Thread.ofVirtual().name("ChestESP End-to-End Test")
			.uncaughtExceptionHandler((t, e) -> {
				e.printStackTrace();
				System.exit(1);
			}).start(this::runTest);
	}
	
	private void runTest()
	{
		System.out.println("Starting ChestESP End-to-End Test");
		waitForLoadingComplete();
		
		if(submitAndWait(mc -> mc.options.onboardAccessibility))
		{
			System.out.println("Onboarding is enabled. Waiting for it");
			waitForScreen(AccessibilityOnboardingScreen.class);
			System.out.println("Reached onboarding screen");
			clickScreenButton("gui.continue");
		}
		
		waitForScreen(TitleScreen.class);
		waitForTitleScreenFade();
		System.out.println("Reached title screen");
		takeScreenshot("title_screen", Duration.ZERO);
		
		System.out.println("Clicking mods button");
		clickScreenButton("modmenu.title");
		takeScreenshot("mod_menu");
		
		System.out.println("Clicking configure button");
		clickScreenButton(403, 48);
		takeScreenshot("cloth_config");
		
		System.out.println("Returning to title screen");
		clickScreenButton("gui.cancel");
		clickScreenButton("gui.done");
		
		System.out.println("Clicking singleplayer button");
		clickScreenButton("menu.singleplayer");
		
		if(submitAndWait(mc -> !mc.getLevelStorage().getLevelList().isEmpty()))
		{
			System.out.println("World list is not empty. Waiting for it");
			waitForScreen(SelectWorldScreen.class);
			System.out.println("Reached select world screen");
			takeScreenshot("select_world_screen");
			clickScreenButton("selectWorld.create");
		}
		
		waitForScreen(CreateWorldScreen.class);
		System.out.println("Reached create world screen");
		
		// Set MC version as world name
		setTextfieldText(0, SharedConstants.getGameVersion().getName());
		// Select creative mode
		clickScreenButton("selectWorld.gameMode");
		clickScreenButton("selectWorld.gameMode");
		takeScreenshot("create_world_screen");
		
		System.out.println("Creating test world");
		clickScreenButton("selectWorld.create");
		
		waitForWorldTicks(180);
		dismissTutorialToasts();
		runChatCommand("seed");
		waitForWorldTicks(20);
		System.out.println("Reached singleplayer world");
		takeScreenshot("in_game", Duration.ZERO);
		
		System.out.println("Building test rig");
		buildTestRig();
		waitForWorldTicks(20);
		clearChat();
		takeScreenshot("ChestESP_default_settings");
		
		System.out.println("Enabling all ChestESP groups");
		updateConfig(config -> {
			config.include_pots = true;
			config.include_hoppers = true;
			config.include_hopper_carts = true;
			config.include_droppers = true;
			config.include_dispensers = true;
			config.include_crafters = true;
			config.include_furnaces = true;
		});
		takeScreenshot("ChestESP_all_boxes");
		
		System.out.println("Changing style to lines");
		updateConfig(config -> {
			config.style = ChestEspStyle.LINES;
		});
		takeScreenshot("ChestESP_all_lines");
		
		System.out.println("Changing style to lines and boxes");
		updateConfig(config -> {
			config.style = ChestEspStyle.LINES_AND_BOXES;
		});
		takeScreenshot("ChestESP_all_lines_and_boxes");
		
		System.out.println("Changing all color settings to purple");
		updateConfig(config -> {
			config.chest_color = 0xFF00FF;
			config.trap_chest_color = 0xFF00FF;
			config.ender_chest_color = 0xFF00FF;
			config.chest_cart_color = 0xFF00FF;
			config.chest_boat_color = 0xFF00FF;
			config.barrel_color = 0xFF00FF;
			config.pot_color = 0xFF00FF;
			config.shulker_box_color = 0xFF00FF;
			config.hopper_color = 0xFF00FF;
			config.hopper_cart_color = 0xFF00FF;
			config.dropper_color = 0xFF00FF;
			config.dispenser_color = 0xFF00FF;
			config.crafter_color = 0xFF00FF;
			config.furnace_color = 0xFF00FF;
		});
		takeScreenshot("ChestESP_all_purple");
		
		System.out.println("Opening debug menu");
		enableDebugHud();
		takeScreenshot("debug_menu");
		
		System.out.println("Closing debug menu");
		enableDebugHud();// bad name, it actually toggles
		
		System.out.println("Checking for broken mixins");
		MixinEnvironment.getCurrentEnvironment().audit();
		
		System.out.println("Opening inventory");
		openInventory();
		takeScreenshot("inventory");
		
		System.out.println("Closing inventory");
		closeScreen();
		
		System.out.println("Opening game menu");
		openGameMenu();
		takeScreenshot("game_menu");
		
		System.out.println("Returning to title screen");
		clickScreenButton("menu.returnToMenu");
		waitForScreen(TitleScreen.class);
		
		System.out.println("Stopping the game");
		clickScreenButton("menu.quit");
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
		runChatCommand("summon chest_boat ^ ^ ^7");
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
}
