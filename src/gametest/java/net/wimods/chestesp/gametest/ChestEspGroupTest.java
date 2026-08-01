/*
 * Copyright (c) 2023-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.gametest;

import java.util.Objects;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.wimods.chestesp.ChestEspBlockGroup;
import net.wimods.chestesp.ChestEspGroupManager;
import net.wimods.chestesp.ChestEspMod;

public final class ChestEspGroupTest extends SingleplayerTest
{
	private static final BlockPos TEST_POS = new BlockPos(0, -56, 7);
	
	public ChestEspGroupTest(ClientGameTestContext context,
		TestSingleplayerContext spContext)
	{
		super(context, spContext);
	}
	
	@Override
	protected void runImpl()
	{
		logger.info("Testing ChestESP group matching");
		ChestEspGroupManager gm = new ChestEspGroupManager(
			ChestEspMod.getInstance().getConfigHolder());
		
		assertMatchesOnly(gm, Blocks.CHEST, gm.normalChests);
		assertMatchesOnly(gm, Blocks.COPPER_CHEST, gm.normalChests);
		assertMatchesOnly(gm, Blocks.WAXED_COPPER_CHEST, gm.normalChests);
		assertMatchesOnly(gm, Blocks.EXPOSED_COPPER_CHEST, gm.normalChests);
		assertMatchesOnly(gm, Blocks.WAXED_EXPOSED_COPPER_CHEST,
			gm.normalChests);
		assertMatchesOnly(gm, Blocks.WEATHERED_COPPER_CHEST, gm.normalChests);
		assertMatchesOnly(gm, Blocks.WAXED_WEATHERED_COPPER_CHEST,
			gm.normalChests);
		assertMatchesOnly(gm, Blocks.OXIDIZED_COPPER_CHEST, gm.normalChests);
		assertMatchesOnly(gm, Blocks.WAXED_OXIDIZED_COPPER_CHEST,
			gm.normalChests);
		assertMatchesOnly(gm, Blocks.TRAPPED_CHEST, gm.trapChests);
		assertMatchesOnly(gm, Blocks.ENDER_CHEST, gm.enderChests);
		assertMatchesOnly(gm, Blocks.BARREL, gm.barrels);
		assertMatchesOnly(gm, Blocks.DECORATED_POT, gm.pots);
		assertMatchesOnly(gm, Blocks.SHULKER_BOX, gm.shulkerBoxes);
		assertMatchesOnly(gm, Blocks.HOPPER, gm.hoppers);
		assertMatchesOnly(gm, Blocks.DROPPER, gm.droppers);
		assertMatchesOnly(gm, Blocks.DISPENSER, gm.dispensers);
		assertMatchesOnly(gm, Blocks.CRAFTER, gm.crafters);
		assertMatchesOnly(gm, Blocks.FURNACE, gm.furnaces);
		
		if(ChestESPTest.IS_LOOTR_INSTALLED)
		{
			assertMatchesOnly(gm, getLootrBlock("chest"), gm.normalChests);
			assertMatchesOnly(gm, getLootrBlock("trapped_chest"),
				gm.trapChests);
			assertMatchesOnly(gm, getLootrBlock("barrel"), gm.barrels);
			assertMatchesOnly(gm, getLootrBlock("shulker_box"),
				gm.shulkerBoxes);
		}
		
		// Clean up
		setBlocksAndWait(blocks -> blocks.set(TEST_POS, Blocks.AIR));
		context.waitTick();// to trigger ChestEspMod.onUpdate()
	}
	
	private void assertMatchesOnly(ChestEspGroupManager gm, Block block,
		ChestEspBlockGroup expectedGroup)
	{
		setBlocksAndWait(blocks -> blocks.set(TEST_POS, block));
		BlockEntity blockEntity = Objects.requireNonNull(
			context.computeOnClient(mc -> mc.level.getBlockEntity(TEST_POS)),
			"Missing block entity for " + block);
		
		if(!expectedGroup.matches(blockEntity))
			throw new AssertionError(blockEntity.getClass().getName()
				+ " did not match expected group " + expectedGroup.getName());
		
		for(ChestEspBlockGroup group : gm.blockGroups)
			if(group != expectedGroup && group.matches(blockEntity))
				throw new AssertionError(blockEntity.getClass().getName()
					+ " unexpectedly matched group " + group.getName());
	}
	
	private Block getLootrBlock(String path)
	{
		Identifier id = Identifier.fromNamespaceAndPath("lootr", path);
		return BuiltInRegistries.BLOCK.getOptional(id).orElseThrow(
			() -> new IllegalStateException("Missing block " + id));
	}
}
