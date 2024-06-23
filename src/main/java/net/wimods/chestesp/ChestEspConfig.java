/*
 * Copyright (c) 2023-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;

@Config(name = "chestesp")
public final class ChestEspConfig implements ConfigData
{
	@ConfigEntry.Gui.Tooltip
	public boolean enable = true;
	
	@ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
	@ConfigEntry.Gui.Tooltip
	public ChestEspStyle style = ChestEspStyle.BOXES;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int chest_color = 0x00FF00;
	
	public boolean include_trap_chests = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int trap_chest_color = 0xFF8000;
	
	public boolean include_ender_chests = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int ender_chest_color = 0x00FFFF;
	
	public boolean include_chest_carts = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int chest_cart_color = 0xFFFF00;
	
	public boolean include_chest_boats = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int chest_boat_color = 0xFFFF00;
	
	public boolean include_barrels = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int barrel_color = 0x00FF00;
	
	public boolean include_pots = false;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int pot_color = 0x00FF00;
	
	public boolean include_shulker_boxes = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int shulker_box_color = 0xFF00FF;
	
	public boolean include_hoppers = false;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int hopper_color = 0xFFFFFF;
	
	public boolean include_hopper_carts = false;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int hopper_cart_color = 0xFFFF00;
	
	public boolean include_droppers = false;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int dropper_color = 0xFFFFFF;
	
	public boolean include_dispensers = false;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int dispenser_color = 0xFF8000;
	
	public boolean include_crafters = false;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int crafter_color = 0xFFFFFF;
	
	public boolean include_furnaces = false;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int furnace_color = 0xFF0000;
}
