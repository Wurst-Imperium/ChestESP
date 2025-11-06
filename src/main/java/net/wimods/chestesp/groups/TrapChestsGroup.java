/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.groups;

import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.wimods.chestesp.ChestEspBlockGroup;
import net.wimods.chestesp.ChestEspConfig;
import net.wimods.chestesp.util.LootrModCompat;

public final class TrapChestsGroup extends ChestEspBlockGroup
{
	public TrapChestsGroup(ConfigHolder<ChestEspConfig> ch)
	{
		super(ch, "trap_chest");
	}
	
	@Override
	protected boolean isEnabled(ChestEspConfig c)
	{
		return c.include_trap_chests;
	}
	
	@Override
	protected int getColor(ChestEspConfig c)
	{
		return c.trap_chest_color;
	}
	
	@Override
	protected boolean matches(BlockEntity be)
	{
		return be instanceof TrappedChestBlockEntity
			|| LootrModCompat.isLootrTrappedChest(be);
	}
}
