/*
 * Copyright (c) 2023-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public enum RotationUtils
{
	;
	
	private static final Minecraft MC = Minecraft.getInstance();
	
	public static Vec3 getClientLookVec(float partialTicks)
	{
		float yaw = MC.player.getViewYRot(partialTicks);
		float pitch = MC.player.getViewXRot(partialTicks);
		return new Rotation(yaw, pitch).toLookVec();
	}
}
