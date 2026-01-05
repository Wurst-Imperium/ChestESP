/*
 * Copyright (c) 2023-2026 Wurst-Imperium and contributors.
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
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.wimods.chestesp.ChestEspMod;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayer
{
	public ClientPlayerEntityMixin(ChestEspMod chestEspMod, ClientLevel world,
		GameProfile profile)
	{
		super(world, profile);
	}
	
	@Inject(at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/player/AbstractClientPlayer;tick()V",
		ordinal = 0), method = "tick()V")
	private void onTick(CallbackInfo ci)
	{
		ChestEspMod chestEsp = ChestEspMod.getInstance();
		if(chestEsp == null)
			return;
		
		chestEsp.onUpdate();
	}
}
