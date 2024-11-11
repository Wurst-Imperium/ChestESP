/*
 * Copyright (c) 2023-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum BlockUtils
{
	;
	
	private static final Minecraft MC = Minecraft.getInstance();
	
	public static BlockState getState(BlockPos pos)
	{
		return MC.level.getBlockState(pos);
	}
	
	private static VoxelShape getOutlineShape(BlockPos pos)
	{
		return getState(pos).getShape(MC.level, pos);
	}
	
	public static AABB getBoundingBox(BlockPos pos)
	{
		return getOutlineShape(pos).bounds().move(pos);
	}
	
	public static boolean canBeClicked(BlockPos pos)
	{
		return getOutlineShape(pos) != Shapes.empty();
	}
}
