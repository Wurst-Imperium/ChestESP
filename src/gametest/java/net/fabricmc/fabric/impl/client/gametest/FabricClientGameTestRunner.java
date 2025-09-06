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

package net.fabricmc.fabric.impl.client.gametest;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.impl.client.gametest.context.ClientGameTestContextImpl;
import net.fabricmc.fabric.impl.client.gametest.threading.ThreadingImpl;
import net.fabricmc.fabric.impl.client.gametest.util.WindowHooks;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

public class FabricClientGameTestRunner
{
	private static final String ENTRYPOINT_KEY = "fabric-client-gametest";
	
	public static EntrypointContainer<FabricClientGameTest> currentlyRunningGameTest =
		null;
	
	public static void start()
	{
		// make the game think the window is focused
		MinecraftClient.getInstance().onWindowFocusChanged(true);
		
		List<EntrypointContainer<FabricClientGameTest>> gameTests =
			FabricLoader.getInstance().getEntrypointContainers(ENTRYPOINT_KEY,
				FabricClientGameTest.class);
		
		ThreadingImpl.runTestThread(() -> {
			ClientGameTestContextImpl context = new ClientGameTestContextImpl();
			
			for(EntrypointContainer<FabricClientGameTest> gameTest : gameTests)
			{
				currentlyRunningGameTest = gameTest;
				
				try
				{
					setupInitialGameTestState(context);
					gameTest.getEntrypoint().runTest(context);
					setupAndCheckFinalGameTestState(context);
				}finally
				{
					currentlyRunningGameTest = null;
				}
			}
		});
	}
	
	private static void setupInitialGameTestState(ClientGameTestContext context)
	{
		context.restoreDefaultGameOptions();
	}
	
	private static void setupAndCheckFinalGameTestState(
		ClientGameTestContextImpl context)
	{
		context.getInput().clearKeysDown();
		context.runOnClient(client -> ((WindowHooks)(Object)client.getWindow())
			.fabric_resetSize());
		context.getInput().setCursorPos(
			context.computeOnClient(client -> client.getWindow().getWidth())
				* 0.5,
			context.computeOnClient(client -> client.getWindow().getHeight())
				* 0.5);
		
		if(ThreadingImpl.isServerRunning)
		{
			throw new AssertionError(
				"Client gametest %s finished while a server is still running"
					.formatted(currentlyRunningGameTest.getDefinition()));
		}
		
		context.runOnClient(client -> {
			if(client.world != null)
			{
				throw new AssertionError(
					"Client gametest %s finished while still connected to a server"
						.formatted(currentlyRunningGameTest.getDefinition()));
			}
			
			if(!(client.currentScreen instanceof TitleScreen))
			{
				throw new AssertionError(
					"Client gametest %s did not finish on the title screen"
						.formatted(currentlyRunningGameTest.getDefinition()));
			}
		});
	}
}
