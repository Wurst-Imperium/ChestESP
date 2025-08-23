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

import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.wimods.chestesp.ChestEspMod;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity
{
	public ClientPlayerEntityMixin(ChestEspMod chestEspMod, ClientWorld world,
		GameProfile profile)
	{
		super(world, profile);
	}
	
	@Inject(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
		ordinal = 0), method = "tick()V")
	private void onTick(CallbackInfo ci)
	{
		ChestEspMod chestEsp = ChestEspMod.getInstance();
		if(chestEsp == null)
			return;
		
		chestEsp.onUpdate();
	}
}
