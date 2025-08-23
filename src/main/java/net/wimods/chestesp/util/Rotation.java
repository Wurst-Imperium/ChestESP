/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public record Rotation(float yaw, float pitch)
{
	public Vec3d toLookVec()
	{
		float radPerDeg = MathHelper.RADIANS_PER_DEGREE;
		float pi = MathHelper.PI;
		
		float adjustedYaw = -MathHelper.wrapDegrees(yaw) * radPerDeg - pi;
		float cosYaw = MathHelper.cos(adjustedYaw);
		float sinYaw = MathHelper.sin(adjustedYaw);
		
		float adjustedPitch = -MathHelper.wrapDegrees(pitch) * radPerDeg;
		float nCosPitch = -MathHelper.cos(adjustedPitch);
		float sinPitch = MathHelper.sin(adjustedPitch);
		
		return new Vec3d(sinYaw * nCosPitch, sinPitch, cosYaw * nCosPitch);
	}
}
