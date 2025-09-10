/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.client.gametest.input;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import net.fabricmc.fabric.impl.client.gametest.util.WindowHooks;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MinecraftClientMixin
{
	@Shadow
	@Final
	private Window window;
	
	@ModifyExpressionValue(method = "runTick",
		at = @At(value = "INVOKE",
			target = "Lcom/mojang/blaze3d/platform/Window;isMinimized()Z"))
	private boolean hasZeroRealWidthOrHeight(boolean original)
	{
		WindowHooks windowHooks = (WindowHooks)(Object)window;
		return windowHooks.fabric_getRealFramebufferWidth() == 0
			|| windowHooks.fabric_getRealFramebufferHeight() == 0;
	}
}
