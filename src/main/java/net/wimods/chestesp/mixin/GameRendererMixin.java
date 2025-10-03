/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.GameRenderer;
import net.wimods.chestesp.ChestEspMod;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements AutoCloseable
{
	@WrapOperation(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/GameRenderer;bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V",
		ordinal = 0),
		method = "renderLevel(Lnet/minecraft/client/DeltaTracker;)V")
	private void onBobView(GameRenderer instance, PoseStack matrices,
		float tickDelta, Operation<Void> original)
	{
		ChestEspMod chestEsp = ChestEspMod.getInstance();
		
		if(chestEsp == null || !chestEsp.shouldCancelViewBobbing())
			original.call(instance, matrices, tickDelta);
	}
}
