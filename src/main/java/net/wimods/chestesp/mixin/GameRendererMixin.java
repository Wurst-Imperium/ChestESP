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

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.wimods.chestesp.ChestEspMod;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements AutoCloseable
{
	@WrapOperation(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V",
		ordinal = 0),
		method = "renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V")
	private void onBobView(GameRenderer instance, MatrixStack matrices,
		float tickDelta, Operation<Void> original)
	{
		ChestEspMod chestEsp = ChestEspMod.getInstance();
		
		if(chestEsp == null || !chestEsp.shouldCancelViewBobbing())
			original.call(instance, matrices, tickDelta);
	}
}
