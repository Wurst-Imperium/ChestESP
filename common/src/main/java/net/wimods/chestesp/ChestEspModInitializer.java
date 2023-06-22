/*
 * Copyright (c) 2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp;

public final class ChestEspModInitializer
{
	private static ChestEspMod instance;
	
	public static void init()
	{
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		
		if(instance != null)
			throw new RuntimeException("ChestEspMod.onInitialize() ran twice!");
		
		instance = new ChestEspMod();
	}
	
	public static ChestEspMod getInstance()
	{
		return instance;
	}
}
