/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp;

import java.util.ArrayList;

import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.world.entity.Entity;
import net.wimods.chestesp.util.EntityUtils;

public abstract class ChestEspEntityGroup extends ChestEspGroup
{
	private final ArrayList<Entity> entities = new ArrayList<>();
	
	public ChestEspEntityGroup(ConfigHolder<ChestEspConfig> configHolder,
		String name)
	{
		super(configHolder, name);
	}
	
	protected abstract boolean matches(Entity e);
	
	public final void addIfMatches(Entity e)
	{
		if(!matches(e))
			return;
		
		entities.add(e);
	}
	
	@Override
	public void clear()
	{
		entities.clear();
		super.clear();
	}
	
	public void updateBoxes(float partialTicks)
	{
		boxes.clear();
		
		for(Entity e : entities)
			boxes.add(EntityUtils.getLerpedBox(e, partialTicks));
	}
}
