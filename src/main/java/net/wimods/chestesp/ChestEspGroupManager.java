/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
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
	public final ChestEspBlockGroup pots;
	public final ChestEspBlockGroup shulkerBoxes;
	public final ChestEspBlockGroup hoppers;
	public final ChestEspEntityGroup hopperCarts;
	public final ChestEspBlockGroup droppers;
	public final ChestEspBlockGroup dispensers;
	public final ChestEspBlockGroup crafters;
	public final ChestEspBlockGroup furnaces;
	
	public final List<ChestEspGroup> allGroups;
	public final List<ChestEspEntityGroup> entityGroups;
	
	public ChestEspGroupManager(ConfigHolder<ChestEspConfig> ch)
	{
		basicChests = new ChestEspBlockGroup(ch, "chest", c -> c.chest_color,
			c -> c.include_basic_chests);
		trapChests = new ChestEspBlockGroup(ch, "trap_chest",
			c -> c.trap_chest_color, c -> c.include_trap_chests);
		enderChests = new ChestEspBlockGroup(ch, "ender_chest",
			c -> c.ender_chest_color, c -> c.include_ender_chests);
		chestCarts = new ChestEspEntityGroup(ch, "chest_cart",
			c -> c.chest_cart_color, c -> c.include_chest_carts);
		chestBoats = new ChestEspEntityGroup(ch, "chest_boat",
			c -> c.chest_boat_color, c -> c.include_chest_boats);
		barrels = new ChestEspBlockGroup(ch, "barrel", c -> c.barrel_color,
			c -> c.include_barrels);
		pots = new ChestEspBlockGroup(ch, "pot", c -> c.pot_color,
			c -> c.include_pots);
		shulkerBoxes = new ChestEspBlockGroup(ch, "shulker_box",
			c -> c.shulker_box_color, c -> c.include_shulker_boxes);
		hoppers = new ChestEspBlockGroup(ch, "hopper", c -> c.hopper_color,
			c -> c.include_hoppers);
		hopperCarts = new ChestEspEntityGroup(ch, "hopper_cart",
			c -> c.hopper_cart_color, c -> c.include_hopper_carts);
		droppers = new ChestEspBlockGroup(ch, "dropper", c -> c.dropper_color,
			c -> c.include_droppers);
		dispensers = new ChestEspBlockGroup(ch, "dispenser",
			c -> c.dispenser_color, c -> c.include_dispensers);
		crafters = new ChestEspBlockGroup(ch, "crafter", c -> c.crafter_color,
			c -> c.include_crafters);
		furnaces = new ChestEspBlockGroup(ch, "furnace", c -> c.furnace_color,
			c -> c.include_furnaces);
		
		allGroups = Arrays.asList(basicChests, trapChests, enderChests,
			chestCarts, chestBoats, barrels, pots, shulkerBoxes, hoppers,
			hopperCarts, droppers, dispensers, crafters, furnaces);
		entityGroups = Arrays.asList(chestCarts, chestBoats, hopperCarts);
	}
}
