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

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.tutorial.TutorialSteps;
import net.wimods.chestesp.ChestEspConfig;
import net.wimods.chestesp.ChestEspMod;

public enum ChestESPClientTestHelper
{
	;
	
	public static void clickPosition(int x, int y)
	{
		waitFor("Click position " + x + ", " + y, mc -> {
			mc.screen.mouseClicked(x, y, 0);
			mc.screen.mouseReleased(x, y, 0);
			return true;
		});
	}
	
	public static void clickScreenButton(int x, int y)
	{
		waitFor("Click button at " + x + ", " + y, mc -> {
			Screen screen = mc.screen;
			if(screen == null)
				return false;
			
			for(Renderable drawable : screen.renderables)
			{
				if(!(drawable instanceof LayoutElement widget))
					continue;
				
				System.out.println(
					"Found widget at " + widget.getX() + ", " + widget.getY()
						+ " of type " + widget.getClass().getName());
				
				if(widget instanceof AbstractButton pressable
					&& pressMatchingButton(pressable, x, y))
					return true;
				
				widget.visitWidgets(clickableWidget -> pressMatchingButton(
					clickableWidget, x, y));
			}
			
			return false;
		});
	}
	
	private static boolean pressMatchingButton(AbstractWidget widget, int x,
		int y)
	{
		if(widget instanceof AbstractButton button && button.getX() == x
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
			Screen screen = mc.screen;
			if(screen == null)
				return false;
			
			int currentIndex = 0;
			for(Renderable drawable : screen.renderables)
			{
				if(!(drawable instanceof EditBox textField))
					continue;
				
				if(currentIndex == index)
				{
					textField.setValue(text);
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
			ClientPacketListener netHandler = mc.getConnection();
			
			// Validate command using client-side command dispatcher
			ParseResults<?> results = netHandler.getCommands().parse(command,
				netHandler.getSuggestionsProvider());
			
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
			netHandler.sendCommand(command);
			return null;
		});
	}
	
	public static void clearChat()
	{
		submitAndWait(mc -> {
			mc.gui.getChat().clearMessages(true);
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
			mc.getTutorial().setStep(TutorialSteps.NONE);
			return null;
		});
	}
}
