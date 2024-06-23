/*
 * Copyright (c) 2023-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public enum RotationUtils
{
	;
	
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	
	public static Vec3d getClientLookVec()
	{
		ClientPlayerEntity player = MC.player;
		float f = 0.017453292F;
		float pi = (float)Math.PI;
		
		float f1 = MathHelper.cos(-player.getYaw() * f - pi);
		float f2 = MathHelper.sin(-player.getYaw() * f - pi);
		float f3 = -MathHelper.cos(-player.getPitch() * f);
		float f4 = MathHelper.sin(-player.getPitch() * f);
		
		return new Vec3d(f2 * f3, f4, f1 * f3);
	}
}
