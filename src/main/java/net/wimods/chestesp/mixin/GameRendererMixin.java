/*
 * Copyright (c) 2023-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.mixin;

import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.wimods.chestesp.ChestEspMod;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements AutoCloseable
{
	@Unique
	private boolean cancelNextBobView;
	
	/**
	 * Fires the CameraTransformViewBobbingEvent event and records whether the
	 * next view-bobbing call should be cancelled.
	 */
	@Inject(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V",
		ordinal = 0), method = "renderWorld(FJ)V")
	private void onRenderWorldViewBobbing(float tickDelta, long limitTime,
		CallbackInfo ci)
	{
		ChestEspMod chestEsp = ChestEspMod.getInstance();
		
		if(chestEsp != null && chestEsp.isEnabled())
			cancelNextBobView = chestEsp.shouldCancelViewBobbing();
	}
	
	/**
	 * Cancels the view-bobbing call if requested by the last
	 * CameraTransformViewBobbingEvent.
	 */
	@Inject(at = @At("HEAD"),
		method = "bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V",
		cancellable = true)
	private void onBobView(MatrixStack matrices, float tickDelta,
		CallbackInfo ci)
	{
		if(!cancelNextBobView)
			return;
		
		ci.cancel();
		cancelNextBobView = false;
	}
	
	/**
	 * This mixin is injected into a random method call later in the
	 * renderWorld() method to ensure that cancelNextBobView is always reset
	 * after the view-bobbing call.
	 */
	@Inject(at = @At("HEAD"),
		method = "renderHand(Lnet/minecraft/client/render/Camera;FLorg/joml/Matrix4f;)V")
	private void onRenderHand(Camera camera, float tickDelta, Matrix4f matrix4f,
		CallbackInfo ci)
	{
		cancelNextBobView = false;
	}
	
	@Inject(
		at = @At(value = "FIELD",
			target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
			opcode = Opcodes.GETFIELD,
			ordinal = 0),
		method = "renderWorld(FJ)V")
	private void onRenderWorld(float partialTicks, long finishTimeNano,
		CallbackInfo ci, @Local(ordinal = 1) Matrix4f matrix4f2)
	{
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.multiplyPositionMatrix(matrix4f2);
		ChestEspMod chestEsp = ChestEspMod.getInstance();
		
		if(chestEsp != null && chestEsp.isEnabled())
			chestEsp.onRender(matrixStack, partialTicks);
	}
}
