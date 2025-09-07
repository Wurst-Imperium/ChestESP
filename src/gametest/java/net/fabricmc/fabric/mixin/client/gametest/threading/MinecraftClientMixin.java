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

package net.fabricmc.fabric.mixin.client.gametest.threading;

import com.google.common.base.Preconditions;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.level.storage.LevelStorage;

import net.fabricmc.fabric.impl.client.gametest.TestSystemProperties;
import net.fabricmc.fabric.impl.client.gametest.threading.NetworkSynchronizer;
import net.fabricmc.fabric.impl.client.gametest.threading.ThreadingImpl;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
	@Unique
	private boolean inMergedRunTasksLoop = false;
	@Unique
	private Runnable deferredTask = null;
	
	@WrapMethod(method = "run")
	private void onRun(Operation<Void> original) throws Throwable
	{
		if(ThreadingImpl.isClientRunning)
		{
			throw new IllegalStateException("Client is already running");
		}
		
		ThreadingImpl.isClientRunning = true;
		ThreadingImpl.PHASER.register();
		
		try
		{
			original.call();
		}finally
		{
			deregisterClient();
			
			if(ThreadingImpl.testFailureException != null)
			{
				throw ThreadingImpl.testFailureException;
			}
		}
	}
	
	@Inject(method = "cleanUpAfterCrash", at = @At("HEAD"))
	private void deregisterAfterCrash(CallbackInfo ci)
	{
		// Deregister a bit earlier than normal to allow for the integrated
		// server to stop without waiting for the client
		ThreadingImpl.setGameCrashed();
		deregisterClient();
	}
	
	@ModifyExpressionValue(method = "render",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/RenderTickCounter;beginRenderTick(J)I"))
	private int captureTicksPerFrame(int capturedTicksPerFrame,
		@Share("ticksPerFrame") LocalIntRef ticksPerFrame)
	{
		// limit the number of ticks in a single frame to 1 (disable the
		// "catch-up" mechanism)
		if(capturedTicksPerFrame > 1)
		{
			capturedTicksPerFrame = 1;
		}
		
		ticksPerFrame.set(capturedTicksPerFrame);
		return capturedTicksPerFrame;
	}
	
	@Inject(method = "render",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/MinecraftClient;runTasks()V"))
	private void preRunTasksHook(CallbackInfo ci)
	{
		// "merge" multiple possible iterations of runTasks into one block from
		// the point of view of locking
		if(!inMergedRunTasksLoop)
		{
			inMergedRunTasksLoop = true;
			preRunTasks();
		}
		
		// we still allow runTasks() to go ahead even when ticksPerFrame is 0,
		// as the results of these tasks won't be
		// observable until the next tick or gametest thread unlock anyway
	}
	
	@Inject(method = "render",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/MinecraftClient;runTasks()V",
			shift = At.Shift.AFTER))
	private void postRunTasksHook(CallbackInfo ci,
		@Share("ticksPerFrame") LocalIntRef ticksPerFrame)
	{
		// end our "merged" runTasks block if there is going to be a tick this
		// frame
		if(ticksPerFrame.get() > 0)
		{
			NetworkSynchronizer.CLIENTBOUND
				.waitForPacketHandlers((ThreadExecutor<?>)(Object)this);
			postRunTasks();
			inMergedRunTasksLoop = false;
		}
	}
	
	@Inject(method = "startIntegratedServer",
		at = @At("HEAD"),
		cancellable = true)
	private void deferStartIntegratedServer(String levelName,
		LevelStorage.Session session, ResourcePackManager dataPackManager,
		SaveLoader saveLoader, boolean newWorld, CallbackInfo ci)
	{
		if(ThreadingImpl.taskToRun != null)
		{
			// don't start the integrated server (which busywaits) inside a task
			deferredTask =
				() -> MinecraftClient.getInstance().startIntegratedServer(
					levelName, session, dataPackManager, saveLoader, newWorld);
			ci.cancel();
		}
	}
	
	@Inject(method = "startIntegratedServer",
		at = @At(value = "INVOKE",
			target = "Ljava/lang/Thread;sleep(J)V",
			remap = false))
	private void onStartIntegratedServerBusyWait(CallbackInfo ci)
	{
		// give the server a chance to tick too
		preRunTasks();
		postRunTasks();
	}
	
	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",
		at = @At("HEAD"),
		cancellable = true)
	private void deferDisconnect(Screen disconnectionScreen, CallbackInfo ci)
	{
		if(MinecraftClient.getInstance().getServer() != null
			&& ThreadingImpl.taskToRun != null)
		{
			// don't disconnect (which busywaits) inside a task
			deferredTask = () -> MinecraftClient.getInstance()
				.disconnect(disconnectionScreen);
			ci.cancel();
		}
	}
	
	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/MinecraftClient;cancelTasks()V"))
	private void onDisconnectCancelTasks(CallbackInfo ci)
	{
		NetworkSynchronizer.CLIENTBOUND.reset();
	}
	
	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/MinecraftClient;render(Z)V",
			shift = At.Shift.AFTER))
	private void onDisconnectBusyWait(CallbackInfo ci)
	{
		// give the server a chance to tick too
		preRunTasks();
		postRunTasks();
	}
	
	@Unique
	private void preRunTasks()
	{
		if(ThreadingImpl.getCurrentPhase() == ThreadingImpl.PHASE_CLIENT_TASKS)
		{
			postRunTasks();
		}
		
		if(!TestSystemProperties.DISABLE_NETWORK_SYNCHRONIZER)
		{
			ThreadingImpl.enterPhase(ThreadingImpl.PHASE_SERVER_TASKS);
			// server tasks happen here
			ThreadingImpl.enterPhase(ThreadingImpl.PHASE_CLIENT_TASKS);
		}
	}
	
	@Unique
	private void postRunTasks()
	{
		ThreadingImpl.clientCanAcceptTasks = true;
		ThreadingImpl.enterPhase(ThreadingImpl.PHASE_TEST);
		
		if(ThreadingImpl.testThread != null)
		{
			while(true)
			{
				try
				{
					ThreadingImpl.CLIENT_SEMAPHORE.acquire();
				}catch(InterruptedException e)
				{
					throw new RuntimeException(e);
				}
				
				if(ThreadingImpl.taskToRun != null)
				{
					ThreadingImpl.taskToRun.run();
				}else
				{
					break;
				}
			}
		}
		
		ThreadingImpl.enterPhase(ThreadingImpl.PHASE_TICK);
		
		Runnable deferredTask = this.deferredTask;
		this.deferredTask = null;
		
		if(deferredTask != null)
		{
			deferredTask.run();
		}
	}
	
	@Inject(method = "getInstance", at = @At("HEAD"))
	private static void checkThreadOnGetInstance(
		CallbackInfoReturnable<MinecraftClient> cir)
	{
		Preconditions.checkState(
			Thread.currentThread() != ThreadingImpl.testThread,
			"MinecraftClient.getInstance() cannot be called from the gametest thread. Try using ClientGameTestContext.runOnClient or ClientGameTestContext.computeOnClient");
	}
	
	@Unique
	private static void deregisterClient()
	{
		if(ThreadingImpl.isClientRunning)
		{
			ThreadingImpl.clientCanAcceptTasks = false;
			ThreadingImpl.PHASER.arriveAndDeregister();
			ThreadingImpl.isClientRunning = false;
		}
	}
}
