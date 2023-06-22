/*
 * Copyright (c) 2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.SynchronousResourceReloader;
import net.wimods.chestesp.ChestEspMod;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
	implements AutoCloseable, SynchronousResourceReloader
{
	private boolean cancelNextBobView;
	
	@Inject(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V",
		ordinal = 0),
		method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V")
	private void onRenderWorldViewBobbing(float tickDelta, long limitTime,
		MatrixStack matrices, CallbackInfo ci)
	{
		ChestEspMod chestEsp = ChestEspMod.getInstance();
		
		if(chestEsp != null && chestEsp.isEnabled())
			cancelNextBobView = chestEsp.shouldCancelViewBobbing();
	}
	
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
	
	@Inject(at = @At("HEAD"),
		method = "renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V")
	private void renderHand(MatrixStack matrices, Camera camera,
		float tickDelta, CallbackInfo ci)
	{
		cancelNextBobView = false;
	}
	
	@Inject(
		at = @At(value = "FIELD",
			target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
			opcode = Opcodes.GETFIELD,
			ordinal = 0),
		method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V")
	private void onRenderWorld(float partialTicks, long finishTimeNano,
		MatrixStack matrixStack, CallbackInfo ci)
	{
		ChestEspMod chestEsp = ChestEspMod.getInstance();
		
		if(chestEsp != null && chestEsp.isEnabled())
			chestEsp.onRender(matrixStack, partialTicks);
	}
}
