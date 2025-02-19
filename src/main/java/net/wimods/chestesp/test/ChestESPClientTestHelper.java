/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.test;

import static net.wimods.chestesp.test.fabric.FabricClientTestHelper.*;

import java.util.function.Consumer;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.tutorial.TutorialStep;
import net.wimods.chestesp.ChestEspConfig;
import net.wimods.chestesp.ChestEspMod;

public enum ChestESPClientTestHelper
{
	;
	
	public static void clickScreenButton(int x, int y)
	{
		waitFor("Click button at " + x + ", " + y, mc -> {
			Screen screen = mc.currentScreen;
			if(screen == null)
				return false;
			
			for(Drawable drawable : screen.drawables)
			{
				if(!(drawable instanceof Widget widget))
					continue;
				
				System.out.println(
					"Found widget at " + widget.getX() + ", " + widget.getY()
						+ " of type " + widget.getClass().getName());
				
				if(widget instanceof PressableWidget pressable
					&& pressMatchingButton(pressable, x, y))
					return true;
				
				widget.forEachChild(clickableWidget -> pressMatchingButton(
					clickableWidget, x, y));
			}
			
			return false;
		});
	}
	
	private static boolean pressMatchingButton(ClickableWidget widget, int x,
		int y)
	{
		if(widget instanceof PressableWidget button && button.getX() == x
			&& button.getY() == y)
		{
			button.onPress();
			return true;
		}
		
		return false;
	}
	
	public static void setTextfieldText(int index, String text)
	{
		waitFor("Set textfield " + index + " to " + text, mc -> {
			Screen screen = mc.currentScreen;
			if(screen == null)
				return false;
			
			int currentIndex = 0;
			for(Drawable drawable : screen.drawables)
			{
				if(!(drawable instanceof TextFieldWidget textField))
					continue;
				
				if(currentIndex == index)
				{
					textField.setText(text);
					return true;
				}
				
				currentIndex++;
			}
			
			return false;
		});
	}
	
	public static void runChatCommand(String command)
	{
		submitAndWait(mc -> {
			ClientPlayNetworkHandler netHandler = mc.getNetworkHandler();
			
			// Validate command using client-side command dispatcher
			ParseResults<?> results = netHandler.getCommandDispatcher()
				.parse(command, netHandler.getCommandSource());
			
			// Command is invalid, fail the test
			if(!results.getExceptions().isEmpty())
			{
				StringBuilder errors =
					new StringBuilder("Invalid command: " + command);
				for(CommandSyntaxException e : results.getExceptions().values())
					errors.append("\n").append(e.getMessage());
				
				throw new RuntimeException(errors.toString());
			}
			
			// Command is valid, send it
			netHandler.sendChatCommand(command);
			return null;
		});
	}
	
	public static void clearChat()
	{
		submitAndWait(mc -> {
			mc.inGameHud.getChatHud().clear(true);
			return null;
		});
	}
	
	public static void updateConfig(Consumer<ChestEspConfig> configUpdater)
	{
		submitAndWait(mc -> {
			configUpdater.accept(
				ChestEspMod.getInstance().getConfigHolder().getConfig());
			return null;
		});
	}
	
	public static void dismissTutorialToasts()
	{
		submitAndWait(mc -> {
			mc.getTutorialManager().setStep(TutorialStep.NONE);
			return null;
		});
	}
}
