/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.gametest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Colors;

@Mixin(TitleScreen.class)
public class TitleScreenMixin
{
	@Shadow
	private SplashTextRenderer splashText;
	
	/**
	 * Disables splash text rendering for consistent gametest screenshots.
	 */
	@Inject(method = "init", at = @At("RETURN"))
	private void disableSplashText(CallbackInfo ci)
	{
		this.splashText = null;
	}
	
	/**
	 * Replaces the panorama cube map with a gray background for consistent
	 * gametest screenshots.
	 */
	@Redirect(method = "render",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/RotatingCubeMapRenderer;render(FF)V"))
	private void replacePanoramaWithGrayBackground(
		RotatingCubeMapRenderer backgroundRenderer, float delta, float alpha,
		@Local(ordinal = 0) DrawContext context)
	{
		context.fill(0, 0, ((TitleScreen)(Object)this).width,
			((TitleScreen)(Object)this).height, Colors.GRAY);
	}
}
