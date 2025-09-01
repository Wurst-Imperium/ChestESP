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

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.impl.client.gametest.context.ClientGameTestContextImpl;
import net.fabricmc.fabric.impl.client.gametest.threading.ThreadingImpl;
import net.fabricmc.fabric.impl.client.gametest.util.WindowHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.wimods.chestesp.gametest.ChestESPTest;

public class FabricClientGameTestRunner
{
	public static FabricClientGameTest currentlyRunningGameTest = null;
	
	public static void start()
	{
		// make the game think the window is focused
		Minecraft.getInstance().setWindowActive(true);
		
		List<FabricClientGameTest> gameTests = getTestToRun();
		
		ThreadingImpl.runTestThread(() -> {
			ClientGameTestContextImpl context = new ClientGameTestContextImpl();
			
			for(FabricClientGameTest gameTest : gameTests)
			{
				currentlyRunningGameTest = gameTest;
				
				try
				{
					setupInitialGameTestState(context);
					gameTest.runTest(context);
					setupAndCheckFinalGameTestState(context);
				}finally
				{
					currentlyRunningGameTest = null;
				}
			}
		});
	}
	
	private static List<FabricClientGameTest> getTestToRun()
	{
		// There is no Fabric Loader in Neoforge,
		// so we just manually set tests here.
		return List.of(new ChestESPTest());
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
			context.computeOnClient(
				client -> client.getWindow().getScreenWidth()) * 0.5,
			context.computeOnClient(
				client -> client.getWindow().getScreenHeight()) * 0.5);
		
		if(ThreadingImpl.isServerRunning)
		{
			throw new AssertionError(
				"Client gametest %s finished while a server is still running"
					.formatted(currentlyRunningGameTest.getClass().getName()));
		}
		
		context.runOnClient(client -> {
			if(client.level != null)
			{
				throw new AssertionError(
					"Client gametest %s finished while still connected to a server"
						.formatted(
							currentlyRunningGameTest.getClass().getName()));
			}
			
			if(!(client.screen instanceof TitleScreen))
			{
				throw new AssertionError(
					"Client gametest %s did not finish on the title screen"
						.formatted(
							currentlyRunningGameTest.getClass().getName()));
			}
		});
	}
}
