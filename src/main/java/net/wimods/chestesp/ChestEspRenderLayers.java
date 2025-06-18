/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp;

import java.util.OptionalDouble;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public enum ChestEspRenderLayers
{
	;
	
	/**
	 * Similar to {@link RenderType#lines()}, but with line width 2.
	 */
	public static final RenderType.CompositeRenderType LINES = RenderType
		.create("chestesp:lines", 1536, ChestEspPipelines.DEPTH_TEST_LINES,
			RenderType.CompositeState.builder()
				.setLineState(
					new RenderStateShard.LineStateShard(OptionalDouble.of(2)))
				.setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
				.setOutputState(RenderType.ITEM_ENTITY_TARGET)
				.createCompositeState(false));
	
	/**
	 * Similar to {@link RenderType#lines()}, but with line width 2 and no
	 * depth test.
	 */
	public static final RenderType.CompositeRenderType ESP_LINES = RenderType
		.create("chestesp:esp_lines", 1536, ChestEspPipelines.ESP_LINES,
			RenderType.CompositeState.builder()
				.setLineState(
					new RenderStateShard.LineStateShard(OptionalDouble.of(2)))
				.setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
				.setOutputState(RenderType.ITEM_ENTITY_TARGET)
				.createCompositeState(false));
	
	/**
	 * Similar to {@link RenderType#debugQuads()}, but with culling enabled.
	 */
	public static final RenderType.CompositeRenderType QUADS = RenderType
		.create("chestesp:quads", 1536, false, true, ChestEspPipelines.QUADS,
			RenderType.CompositeState.builder().createCompositeState(false));
	
	/**
	 * Similar to {@link RenderType#debugQuads()}, but with culling enabled
	 * and no depth test.
	 */
	public static final RenderType.CompositeRenderType ESP_QUADS =
		RenderType.create("chestesp:esp_quads", 1536, false, true,
			ChestEspPipelines.ESP_QUADS,
			RenderType.CompositeState.builder().createCompositeState(false));
	
	/**
	 * Returns either {@link #QUADS} or {@link #ESP_QUADS} depending on the
	 * value of {@code depthTest}.
	 */
	public static RenderType getQuads(boolean depthTest)
	{
		return depthTest ? QUADS : ESP_QUADS;
	}
	
	/**
	 * Returns either {@link #LINES} or {@link #ESP_LINES} depending on the
	 * value of {@code depthTest}.
	 */
	public static RenderType getLines(boolean depthTest)
	{
		return depthTest ? LINES : ESP_LINES;
	}
}
