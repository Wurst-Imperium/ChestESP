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

package net.fabricmc.fabric.mixin.client.gametest.lifecycle;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.main.Main;

@Mixin(Main.class)
public class MainMixin
{
	/**
	 * Prevents the Main class from catching exceptions when running
	 * gametests, so that test failures can properly crash the game and fail
	 * the build.
	 */
	@WrapOperation(method = "main",
		at = @At(value = "INVOKE",
			target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Throwable;)V",
			ordinal = 0))
	private static void rethrowGameException(Logger logger, String message,
		Throwable throwable, Operation<Void> original) throws Throwable
	{
		throw throwable;
	}
}
