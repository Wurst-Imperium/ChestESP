/*
 * Copyright (c) 2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp;

import java.util.Arrays;
import java.util.List;

import me.shedaniel.autoconfig.ConfigHolder;

public final class ChestEspGroupManager
{
	public final ChestEspBlockGroup basicChests;
	public final ChestEspBlockGroup trapChests;
	public final ChestEspBlockGroup enderChests;
	public final ChestEspEntityGroup chestCarts;
	public final ChestEspEntityGroup chestBoats;
	public final ChestEspBlockGroup barrels;
	public final ChestEspBlockGroup shulkerBoxes;
	public final ChestEspBlockGroup hoppers;
	public final ChestEspEntityGroup hopperCarts;
	public final ChestEspBlockGroup droppers;
	public final ChestEspBlockGroup dispensers;
	public final ChestEspBlockGroup furnaces;
	
	public final List<ChestEspGroup> allGroups;
	public final List<ChestEspEntityGroup> entityGroups;
	
	public ChestEspGroupManager(ConfigHolder<ChestEspConfig> ch)
	{
		basicChests = new ChestEspBlockGroup(ch, c -> c.chest_color, null);
		trapChests = new ChestEspBlockGroup(ch, c -> c.trap_chest_color,
			c -> c.include_trap_chests);
		enderChests = new ChestEspBlockGroup(ch, c -> c.ender_chest_color,
			c -> c.include_ender_chests);
		chestCarts = new ChestEspEntityGroup(ch, c -> c.chest_cart_color,
			c -> c.include_chest_carts);
		chestBoats = new ChestEspEntityGroup(ch, c -> c.chest_boat_color,
			c -> c.include_chest_boats);
		barrels = new ChestEspBlockGroup(ch, c -> c.barrel_color,
			c -> c.include_barrels);
		shulkerBoxes = new ChestEspBlockGroup(ch, c -> c.shulker_box_color,
			c -> c.include_shulker_boxes);
		hoppers = new ChestEspBlockGroup(ch, c -> c.hopper_color,
			c -> c.include_hoppers);
		hopperCarts = new ChestEspEntityGroup(ch, c -> c.hopper_cart_color,
			c -> c.include_hopper_carts);
		droppers = new ChestEspBlockGroup(ch, c -> c.dropper_color,
			c -> c.include_droppers);
		dispensers = new ChestEspBlockGroup(ch, c -> c.dispenser_color,
			c -> c.include_dispensers);
		furnaces = new ChestEspBlockGroup(ch, c -> c.furnace_color,
			c -> c.include_furnaces);
		
		allGroups = Arrays.asList(basicChests, trapChests, enderChests,
			chestCarts, chestBoats, barrels, shulkerBoxes, hoppers, hopperCarts,
			droppers, dispensers, furnaces);
		entityGroups = Arrays.asList(chestCarts, chestBoats, hopperCarts);
	}
}
