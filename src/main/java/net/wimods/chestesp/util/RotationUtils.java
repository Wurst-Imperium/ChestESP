/*
 * Copyright (c) 2023-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public enum RotationUtils
{
	;
	
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	
	public static Vec3d getClientLookVec(float partialTicks)
	{
		float yaw = MC.player.getYaw(partialTicks);
		float pitch = MC.player.getPitch(partialTicks);
		return new Rotation(yaw, pitch).toLookVec();
	}
}
