/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public record Rotation(float yaw, float pitch)
{
	public Vec3 toLookVec()
	{
		float radPerDeg = Mth.DEG_TO_RAD;
		float pi = Mth.PI;
		
		float adjustedYaw = -Mth.wrapDegrees(yaw) * radPerDeg - pi;
		float cosYaw = Mth.cos(adjustedYaw);
		float sinYaw = Mth.sin(adjustedYaw);
		
		float adjustedPitch = -Mth.wrapDegrees(pitch) * radPerDeg;
		float nCosPitch = -Mth.cos(adjustedPitch);
		float sinPitch = Mth.sin(adjustedPitch);
		
		return new Vec3(sinYaw * nCosPitch, sinPitch, cosYaw * nCosPitch);
	}
}
