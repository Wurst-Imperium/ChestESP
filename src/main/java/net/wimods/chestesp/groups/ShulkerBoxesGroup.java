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
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.wimods.chestesp.ChestEspBlockGroup;
import net.wimods.chestesp.ChestEspConfig;
import net.wimods.chestesp.util.LootrModCompat;

public final class ShulkerBoxesGroup extends ChestEspBlockGroup
{
	public ShulkerBoxesGroup(ConfigHolder<ChestEspConfig> ch)
	{
		super(ch, "shulker_box");
	}
	
	@Override
	protected boolean isEnabled(ChestEspConfig c)
	{
		return c.include_shulker_boxes;
	}
	
	@Override
	protected int getColor(ChestEspConfig c)
	{
		return c.shulker_box_color;
	}
	
	@Override
	protected boolean matches(BlockEntity be)
	{
		return be instanceof ShulkerBoxBlockEntity
			|| LootrModCompat.isLootrShulkerBox(be);
	}
}
