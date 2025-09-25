/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.groups;

import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.wimods.chestesp.ChestEspBlockGroup;
import net.wimods.chestesp.ChestEspConfig;

public final class DispensersGroup extends ChestEspBlockGroup
{
	public DispensersGroup(ConfigHolder<ChestEspConfig> ch)
	{
		super(ch, "dispenser");
	}
	
	@Override
	protected boolean isEnabled(ChestEspConfig c)
	{
		return c.include_dispensers;
	}
	
	@Override
	protected int getColor(ChestEspConfig c)
	{
		return c.dispenser_color;
	}
	
	@Override
	protected boolean matches(BlockEntity be)
	{
		return be instanceof DispenserBlockEntity
			&& !(be instanceof DropperBlockEntity);
	}
}
