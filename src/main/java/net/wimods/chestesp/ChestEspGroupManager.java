/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp;

import java.util.List;
import java.util.stream.Stream;

import me.shedaniel.autoconfig.ConfigHolder;
import net.wimods.chestesp.groups.*;

public final class ChestEspGroupManager
{
	public final NormalChestsGroup normalChests;
	public final TrapChestsGroup trapChests;
	public final EnderChestsGroup enderChests;
	public final ChestCartsGroup chestCarts;
	public final ChestBoatsGroup chestBoats;
	public final BarrelsGroup barrels;
	public final PotsGroup pots;
	public final ShulkerBoxesGroup shulkerBoxes;
	public final HoppersGroup hoppers;
	public final HopperCartsGroup hopperCarts;
	public final DroppersGroup droppers;
	public final DispensersGroup dispensers;
	public final CraftersGroup crafters;
	public final FurnacesGroup furnaces;
	
	public final List<ChestEspBlockGroup> blockGroups;
	public final List<ChestEspEntityGroup> entityGroups;
	public final List<ChestEspGroup> allGroups;
	
	public ChestEspGroupManager(ConfigHolder<ChestEspConfig> ch)
	{
		normalChests = new NormalChestsGroup(ch);
		trapChests = new TrapChestsGroup(ch);
		enderChests = new EnderChestsGroup(ch);
		chestCarts = new ChestCartsGroup(ch);
		chestBoats = new ChestBoatsGroup(ch);
		barrels = new BarrelsGroup(ch);
		pots = new PotsGroup(ch);
		shulkerBoxes = new ShulkerBoxesGroup(ch);
		hoppers = new HoppersGroup(ch);
		hopperCarts = new HopperCartsGroup(ch);
		droppers = new DroppersGroup(ch);
		dispensers = new DispensersGroup(ch);
		crafters = new CraftersGroup(ch);
		furnaces = new FurnacesGroup(ch);
		
		blockGroups = List.of(normalChests, trapChests, enderChests, barrels,
			pots, shulkerBoxes, hoppers, droppers, dispensers, crafters,
			furnaces);
		
		entityGroups = List.of(chestCarts, chestBoats, hopperCarts);
		
		allGroups =
			Stream.concat(blockGroups.stream(), entityGroups.stream()).toList();
	}
}
