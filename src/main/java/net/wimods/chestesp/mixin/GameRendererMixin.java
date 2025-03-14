/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
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
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
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
		target = "Lnet/minecraft/client/renderer/GameRenderer;bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V",
		ordinal = 0),
		method = "renderLevel(Lnet/minecraft/client/DeltaTracker;)V")
	private void onRenderWorldViewBobbing(DeltaTracker tickCounter,
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
		method = "bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V",
		cancellable = true)
	private void onBobView(PoseStack matrices, float tickDelta, CallbackInfo ci)
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
		method = "renderItemInHand(Lnet/minecraft/client/Camera;FLorg/joml/Matrix4f;)V")
	private void onRenderHand(Camera camera, float tickDelta, Matrix4f matrix4f,
		CallbackInfo ci)
	{
		cancelNextBobView = false;
	}
	
	@Inject(
		at = @At(value = "FIELD",
			target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand:Z",
			opcode = Opcodes.GETFIELD,
			ordinal = 0),
		method = "renderLevel(Lnet/minecraft/client/DeltaTracker;)V")
	private void onRenderWorldHandRendering(DeltaTracker tickCounter,
		CallbackInfo ci, @Local(ordinal = 2) Matrix4f matrix4f3,
		@Local(ordinal = 1) float tickDelta)
	{
		PoseStack matrixStack = new PoseStack();
		matrixStack.mulPose(matrix4f3);
		ChestEspMod chestEsp = ChestEspMod.getInstance();
		
		if(chestEsp != null && chestEsp.isEnabled())
			chestEsp.onRender(matrixStack, tickDelta);
	}
}
