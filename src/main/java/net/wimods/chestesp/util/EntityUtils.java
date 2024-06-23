/*
 * Copyright (c) 2023-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public enum EntityUtils
{
	;
	
	/**
	 * Interpolates (or "lerps") between the entity's position in the previous
	 * tick and its position in the current tick to get the exact position where
	 * the entity will be rendered in the next frame.
	 *
	 * <p>
	 * This interpolation is important for smooth animations. Using the entity's
	 * current tick position directly would cause animations to look choppy
	 * because that position is only updated 20 times per second.
	 */
	public static Vec3d getLerpedPos(Entity e, float partialTicks)
	{
		// When an entity is removed, it stops moving and its lastRenderX/Y/Z
		// values are no longer updated.
		if(e.isRemoved())
			return e.getPos();
		
		double x = MathHelper.lerp(partialTicks, e.lastRenderX, e.getX());
		double y = MathHelper.lerp(partialTicks, e.lastRenderY, e.getY());
		double z = MathHelper.lerp(partialTicks, e.lastRenderZ, e.getZ());
		return new Vec3d(x, y, z);
	}
	
	/**
	 * Interpolates (or "lerps") between the entity's bounding box in the
	 * previous tick and its bounding box in the current tick to get the exact
	 * bounding box that the entity will have in the next frame.
	 *
	 * <p>
	 * This interpolation is important for smooth animations. Using the entity's
	 * current tick bounding box directly would cause animations to look choppy
	 * because that box, just like the position, is only updated 20 times per
	 * second.
	 */
	public static Box getLerpedBox(Entity e, float partialTicks)
	{
		// When an entity is removed, it stops moving and its lastRenderX/Y/Z
		// values are no longer updated.
		if(e.isRemoved())
			return e.getBoundingBox();
		
		Vec3d offset = getLerpedPos(e, partialTicks).subtract(e.getPos());
		return e.getBoundingBox().offset(offset);
	}
}
