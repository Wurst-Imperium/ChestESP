/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

public enum ChunkUtils
{
	;
	
	private static final Minecraft MC = Minecraft.getInstance();
	
	public static Stream<BlockEntity> getLoadedBlockEntities()
	{
		return getLoadedChunks()
			.flatMap(chunk -> chunk.getBlockEntities().values().stream());
	}
	
	public static Stream<LevelChunk> getLoadedChunks()
	{
		int radius = Math.max(2, MC.options.getEffectiveRenderDistance()) + 3;
		int diameter = radius * 2 + 1;
		
		ChunkPos center = MC.player.chunkPosition();
		ChunkPos min = new ChunkPos(center.x - radius, center.z - radius);
		ChunkPos max = new ChunkPos(center.x + radius, center.z + radius);
		
		Stream<LevelChunk> stream = Stream.<ChunkPos> iterate(min, pos -> {
			
			int x = pos.x;
			int z = pos.z;
			
			x++;
			
			if(x > max.x)
			{
				x = min.x;
				z++;
			}
			
			if(z > max.z)
				throw new IllegalStateException("Stream limit didn't work.");
			
			return new ChunkPos(x, z);
			
		}).limit(diameter * diameter).filter(c -> MC.level.hasChunk(c.x, c.z))
			.map(c -> MC.level.getChunk(c.x, c.z)).filter(Objects::nonNull);
		
		return stream;
	}
	
}
