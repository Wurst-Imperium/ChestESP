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
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.fabric.impl.client.gametest.util.WindowHooks;
import net.minecraft.client.Minecraft;

@Mixin(RenderTarget.class)
public class FramebufferMixin
{
	@ModifyVariable(method = {"blitToScreen", "_blitToScreen"},
		at = @At("HEAD"),
		ordinal = 0,
		argsOnly = true)
	private int modifyWidth(int width)
	{
		Window window = Minecraft.getInstance().getWindow();
		
		if((Object)this == Minecraft.getInstance().getMainRenderTarget()
			&& width == window.getWidth())
		{
			return ((WindowHooks)(Object)window)
				.fabric_getRealFramebufferWidth();
		}
		
		return width;
	}
	
	@ModifyVariable(method = {"blitToScreen", "_blitToScreen"},
		at = @At("HEAD"),
		ordinal = 1,
		argsOnly = true)
	private int modifyHeight(int height)
	{
		Window window = Minecraft.getInstance().getWindow();
		
		if((Object)this == Minecraft.getInstance().getMainRenderTarget()
			&& height == window.getHeight())
		{
			return ((WindowHooks)(Object)window)
				.fabric_getRealFramebufferHeight();
		}
		
		return height;
	}
}
