/*
 * Copyright (c) 2023-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.gametest;

import java.util.function.Consumer;

import org.slf4j.Logger;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestClientWorldContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.wimods.chestesp.gametest.BlockTestHelper.BlockBatch;

public abstract class SingleplayerTest
{
	protected final ClientGameTestContext context;
	protected final TestSingleplayerContext spContext;
	protected final TestClientWorldContext world;
	protected final TestServerContext server;
	protected final Logger logger = ChestESPTest.LOGGER;
	
	public SingleplayerTest(ClientGameTestContext context,
		TestSingleplayerContext spContext)
	{
		this.context = context;
		this.spContext = spContext;
		this.world = spContext.getClientWorld();
		this.server = spContext.getServer();
	}
	
	/**
	 * Runs the test and verifies cleanup afterward.
	 */
	public final void run()
	{
		runImpl();
		
		String testName = getClass().getSimpleName();
		int retries =
			waitForScreenshotMatch(testName.toLowerCase() + "_cleanup",
				"https://i.imgur.com/XF1SILt.png");
		
		if(retries > 0)
			logger.warn(testName + " needed " + retries
				+ " retries to get a valid cleanup screenshot. First view ALL"
				+ " screenshots from " + testName + " to understand what"
				+ " happened, then optionally retest. If this keeps happening,"
				+ " your timings are probably wrong. Otherwise it's likely a"
				+ " fluke, especially if you didn't change any gametest code.");
	}
	
	/**
	 * Implement the actual test logic here. The test is responsible for
	 * cleaning up after itself (removing blocks/entities, resetting config,
	 * etc.).
	 */
	protected abstract void runImpl();
	
	protected final void runCommand(String command)
	{
		WiModsTestHelper.runCommand(server, command);
	}
	
	protected final void setBlocksAndWait(Consumer<BlockBatch> batchBuilder)
	{
		BlockTestHelper.setBlocksAndWait(context, spContext, batchBuilder);
	}
	
	protected final BlockState chestState(Block block, ChestType type)
	{
		return block.defaultBlockState().setValue(ChestBlock.TYPE, type);
	}
	
	protected final void assertScreenshotEquals(String fileName,
		String templateUrl)
	{
		WiModsTestHelper.assertScreenshotEquals(context, fileName, templateUrl);
	}
	
	protected final int waitForScreenshotMatch(String fileName,
		String templateUrl)
	{
		return WiModsTestHelper.waitForScreenshotMatch(context, fileName,
			templateUrl);
	}
}
