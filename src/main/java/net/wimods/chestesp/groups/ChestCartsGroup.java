/*
 * Copyright (c) 2023-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.groups;

import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.wimods.chestesp.ChestEspConfig;
import net.wimods.chestesp.ChestEspEntityGroup;

public final class ChestCartsGroup extends ChestEspEntityGroup
{
	public ChestCartsGroup(ConfigHolder<ChestEspConfig> ch)
	{
		super(ch, "chest_cart");
	}
	
	@Override
	protected boolean isEnabled(ChestEspConfig c)
	{
		return c.include_chest_carts;
	}
	
	@Override
	protected int getColor(ChestEspConfig c)
	{
		return c.chest_cart_color;
	}
	
	@Override
	protected boolean matches(Entity e)
	{
		return e instanceof MinecartChest;
	}
}
