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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Window;

import net.fabricmc.fabric.impl.client.gametest.util.WindowHooks;

@Mixin(Framebuffer.class)
public class FramebufferMixin
{
	@ModifyVariable(method = {"draw", "drawInternal"},
		at = @At("HEAD"),
		ordinal = 0,
		argsOnly = true)
	private int modifyWidth(int width)
	{
		Window window = MinecraftClient.getInstance().getWindow();
		
		if((Object)this == MinecraftClient.getInstance().getFramebuffer()
			&& width == window.getFramebufferWidth())
		{
			return ((WindowHooks)(Object)window)
				.fabric_getRealFramebufferWidth();
		}
		
		return width;
	}
	
	@ModifyVariable(method = {"draw", "drawInternal"},
		at = @At("HEAD"),
		ordinal = 1,
		argsOnly = true)
	private int modifyHeight(int height)
	{
		Window window = MinecraftClient.getInstance().getWindow();
		
		if((Object)this == MinecraftClient.getInstance().getFramebuffer()
			&& height == window.getFramebufferHeight())
		{
			return ((WindowHooks)(Object)window)
				.fabric_getRealFramebufferHeight();
		}
		
		return height;
	}
}
