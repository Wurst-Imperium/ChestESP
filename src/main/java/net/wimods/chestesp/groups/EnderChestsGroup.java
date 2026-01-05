/*
 * Copyright (c) 2023-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.groups;

import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.wimods.chestesp.ChestEspBlockGroup;
import net.wimods.chestesp.ChestEspConfig;

public final class EnderChestsGroup extends ChestEspBlockGroup
{
	public EnderChestsGroup(ConfigHolder<ChestEspConfig> ch)
	{
		super(ch, "ender_chest");
	}
	
	@Override
	protected boolean isEnabled(ChestEspConfig c)
	{
		return c.include_ender_chests;
	}
	
	@Override
	protected int getColor(ChestEspConfig c)
	{
		return c.ender_chest_color;
	}
	
	@Override
	protected boolean matches(BlockEntity be)
	{
		return be instanceof EnderChestBlockEntity;
	}
}
