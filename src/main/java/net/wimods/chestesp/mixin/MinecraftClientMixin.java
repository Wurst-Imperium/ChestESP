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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.wimods.chestesp.ChestEspMod;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
	extends ReentrantThreadExecutor<Runnable> implements WindowEventHandler
{
	private MinecraftClientMixin(ChestEspMod chestEsp, String name)
	{
		super(name);
	}
	
	/**
	 * Does the same thing as ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE
	 * but for older Minecraft versions where Fabric API >=0.108.0 is
	 * not available.
	 */
	@Inject(at = @At("TAIL"),
		method = "setWorld(Lnet/minecraft/client/world/ClientWorld;)V")
	private void onSetWorld(ClientWorld world, CallbackInfo ci)
	{
		if(world == null)
			return;
		
		MinecraftClient client = (MinecraftClient)(Object)this;
		ChestEspMod.getInstance().getPlausible().onWorldChange(client, world);
	}
}
