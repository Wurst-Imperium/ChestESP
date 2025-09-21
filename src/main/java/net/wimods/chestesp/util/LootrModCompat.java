/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import net.minecraft.block.entity.BlockEntity;

/**
 * Detects block entities from the Lootr mod for use in ChestESP.
 *
 * <p>
 * Last tested with lootr-fabric-1.20-0.7.35.85.
 */
public enum LootrModCompat
{
	;
	
	private static final Class<?> lootrBarrelClass = getClassIfExists(
		"net.zestyblaze.lootr.block.entities.LootrBarrelBlockEntity");
	private static final Class<?> lootrShulkerBoxClass = getClassIfExists(
		"net.zestyblaze.lootr.block.entities.LootrShulkerBlockEntity");
	
	public static boolean isLootrBarrel(BlockEntity blockEntity)
	{
		if(blockEntity == null || lootrBarrelClass == null)
			return false;
		
		return lootrBarrelClass.isInstance(blockEntity);
	}
	
	public static boolean isLootrShulkerBox(BlockEntity blockEntity)
	{
		if(blockEntity == null || lootrShulkerBoxClass == null)
			return false;
		
		return lootrShulkerBoxClass.isInstance(blockEntity);
	}
	
	private static Class<?> getClassIfExists(String name)
	{
		try
		{
			return Class.forName(name, false,
				LootrModCompat.class.getClassLoader());
			
		}catch(ClassNotFoundException e)
		{
			return null;
		}
	}
}
