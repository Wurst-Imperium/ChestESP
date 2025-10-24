/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.groups;

import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.wimods.chestesp.ChestEspConfig;
import net.wimods.chestesp.ChestEspEntityGroup;

public final class ChestBoatsGroup extends ChestEspEntityGroup
{
	public ChestBoatsGroup(ConfigHolder<ChestEspConfig> ch)
	{
		super(ch, "chest_boat");
	}
	
	@Override
	protected boolean isEnabled(ChestEspConfig c)
	{
		return c.include_chest_boats;
	}
	
	@Override
	protected int getColor(ChestEspConfig c)
	{
		return c.chest_boat_color;
	}
	
	@Override
	protected boolean matches(Entity e)
	{
		return e instanceof ChestBoat;
	}
}
