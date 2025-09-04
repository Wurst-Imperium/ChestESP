/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.gametest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.joml.Vector2i;
import org.lwjgl.system.MemoryUtil;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonAlgorithm;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonAlgorithm.RawImage;
import net.fabricmc.fabric.impl.client.gametest.screenshot.TestScreenshotComparisonAlgorithms.RawImageImpl;
import net.fabricmc.fabric.impl.client.gametest.threading.ThreadingImpl;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.commands.CommandSourceStack;

public enum WiModsTestHelper
{
	;
	
	/**
	 * Takes a screenshot, matches it against the template image, and throws if
	 * it doesn't match. This method allows the template image to have
	 * an alpha channel and ignores any pixels that are >50% transparent. This
	 * way, you can precisely control which parts of the screenshot to assert
	 * against the template and which parts to ignore.
	 */
	public static void assertScreenshotEquals(ClientGameTestContext context,
		String fileName, String templateUrl)
	{
		ThreadingImpl.checkOnGametestThread("assertScreenshotEquals");
		
		NativeImage nativeTemplateImage = downloadImage(templateUrl);
		boolean[][] mask = alphaChannelToMask(nativeTemplateImage);
		RawImage<int[]> rawTemplateImage =
			RawImageImpl.fromColorNativeImage(nativeTemplateImage);
		RawImage<int[]> maskedTemplateImage = applyMask(rawTemplateImage, mask);
		
		Path screenshotPath = context.takeScreenshot(fileName);
		RawImage<int[]> rawScreenshotImage =
			RawImageImpl.fromColorNativeImage(loadImageFile(screenshotPath));
		RawImage<int[]> maskedScreenshotImage =
			applyMask(rawScreenshotImage, mask);
		
		if(maskedScreenshotImage.width() != maskedTemplateImage.width()
			|| maskedScreenshotImage.height() != maskedTemplateImage.height())
			throw new AssertionError(
				"Screenshot and template dimensions do not match");
		
		TestScreenshotComparisonAlgorithm algo =
			TestScreenshotComparisonAlgorithm.meanSquaredDifference(5e-6F);
		
		Vector2i result =
			algo.findColor(maskedScreenshotImage, maskedTemplateImage);
		if(result == null)
			throw new AssertionError("Screenshot '" + fileName
				+ "' does not match template '" + templateUrl + "'");
	}
	
	private static boolean[][] alphaChannelToMask(NativeImage template)
	{
		if(!template.format().hasAlpha())
		{
			int width = template.getWidth();
			int height = template.getHeight();
			boolean[][] mask = new boolean[width][height];
			for(int y = 0; y < height; y++)
				for(int x = 0; x < width; x++)
					mask[x][y] = false;
			return mask;
		}
		
		int width = template.getWidth();
		int height = template.getHeight();
		boolean[][] mask = new boolean[width][height];
		
		int size = width * height;
		int alphaOffset = template.format().alphaOffset() / 8;
		int channelCount = template.format().components();
		
		for(int i = 0; i < size; i++)
		{
			int x = i % width;
			int y = i / width;
			int alpha = MemoryUtil.memGetByte(
				template.getPointer() + i * channelCount + alphaOffset) & 0xff;
			mask[x][y] = alpha > 127;
		}
		
		return mask;
	}
	
	private static RawImage<int[]> applyMask(RawImage<int[]> image,
		boolean[][] mask)
	{
		int width = image.width();
		int height = image.height();
		int[] inData = image.data();
		int[] outData = new int[width * height];
		
		for(int y = 0; y < height; y++)
			for(int x = 0; x < width; x++)
				outData[y * width + x] = mask[x][y] ? inData[y * width + x] : 0;
			
		return new RawImageImpl<>(width, height, outData);
	}
	
	public static NativeImage loadImageFile(Path path)
	{
		try(InputStream inputStream = Files.newInputStream(path))
		{
			return NativeImage.read(inputStream);
			
		}catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static NativeImage downloadImage(String url)
	{
		try(InputStream inputStream = URI.create(url).toURL().openStream())
		{
			return NativeImage.read(inputStream);
			
		}catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static void hideSplashTexts(ClientGameTestContext context)
	{
		context.runOnClient(mc -> {
			mc.options.hideSplashTexts().set(true);
		});
	}
	
	/**
	 * Waits for the fading animation of the title screen to finish, or fails
	 * after 10 seconds.
	 */
	public static void waitForTitleScreenFade(ClientGameTestContext context)
	{
		context.waitFor(mc -> {
			if(!(mc.screen instanceof TitleScreen titleScreen))
				return false;
			
			return !titleScreen.fading;
		});
	}
	
	public static void runCommand(TestServerContext server, String command)
	{
		String commandWithPlayer = "execute as @p at @s run " + command;
		server.runOnServer(mc -> {
			ParseResults<CommandSourceStack> results =
				mc.getCommands().getDispatcher().parse(commandWithPlayer,
					mc.createCommandSourceStack());
			
			if(!results.getExceptions().isEmpty())
			{
				StringBuilder errors =
					new StringBuilder("Invalid command: /" + commandWithPlayer);
				for(CommandSyntaxException e : results.getExceptions().values())
					errors.append("\n").append(e.getMessage());
				
				throw new RuntimeException(errors.toString());
			}
			
			mc.getCommands().performCommand(results, commandWithPlayer);
		});
	}
}
