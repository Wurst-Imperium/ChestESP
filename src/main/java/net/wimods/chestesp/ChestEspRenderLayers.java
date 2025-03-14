/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp;

import java.util.OptionalDouble;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

public enum ChestEspRenderLayers
{
	;
	
	/**
	 * Similar to {@link RenderLayer#getLines()}, but with line width 2.
	 */
	public static final RenderLayer.MultiPhase LINES =
		RenderLayer.of("chestesp:lines", VertexFormats.LINES,
			VertexFormat.DrawMode.LINES, 1536, false, true,
			RenderLayer.MultiPhaseParameters.builder()
				.program(RenderLayer.LINES_PROGRAM)
				.lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(2)))
				.layering(RenderLayer.VIEW_OFFSET_Z_LAYERING)
				.transparency(RenderLayer.TRANSLUCENT_TRANSPARENCY)
				.target(RenderLayer.ITEM_ENTITY_TARGET)
				.writeMaskState(RenderLayer.ALL_MASK)
				.depthTest(RenderLayer.LEQUAL_DEPTH_TEST)
				.cull(RenderLayer.DISABLE_CULLING).build(false));
	
	/**
	 * Similar to {@link RenderLayer#getLines()}, but with line width 2 and no
	 * depth test.
	 *
	 * @apiNote Until 25w08a (1.21.5), turning off depth test has to be done
	 *          manually, by calling
	 *          {@code RenderSystem.depthFunc(GlConst.GL_ALWAYS);} before
	 *          drawing the ESP lines. Without this code, ESP lines will be
	 *          drawn with depth test set to LEQUAL (only visible if not
	 *          obstructed).
	 */
	public static final RenderLayer.MultiPhase ESP_LINES =
		RenderLayer.of("chestesp:esp_lines", VertexFormats.LINES,
			VertexFormat.DrawMode.LINES, 1536, false, true,
			RenderLayer.MultiPhaseParameters.builder()
				.program(RenderLayer.LINES_PROGRAM)
				.lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(2)))
				.layering(RenderLayer.VIEW_OFFSET_Z_LAYERING)
				.transparency(RenderLayer.TRANSLUCENT_TRANSPARENCY)
				.target(RenderLayer.ITEM_ENTITY_TARGET)
				.writeMaskState(RenderLayer.ALL_MASK)
				.depthTest(RenderLayer.ALWAYS_DEPTH_TEST)
				.cull(RenderLayer.DISABLE_CULLING).build(false));
	
	/**
	 * Similar to {@link RenderLayer#getDebugQuads()}, but with culling enabled.
	 */
	public static final RenderLayer.MultiPhase QUADS =
		RenderLayer.of("chestesp:quads", VertexFormats.POSITION_COLOR,
			VertexFormat.DrawMode.QUADS, 1536, false, true,
			RenderLayer.MultiPhaseParameters.builder()
				.program(RenderLayer.POSITION_COLOR_PROGRAM)
				.transparency(RenderLayer.TRANSLUCENT_TRANSPARENCY)
				.depthTest(RenderLayer.LEQUAL_DEPTH_TEST).build(false));
	
	/**
	 * Similar to {@link RenderLayer#getDebugQuads()}, but with culling enabled
	 * and no depth test.
	 *
	 * @apiNote Until 25w08a (1.21.5), turning off depth test has to be done
	 *          manually, by calling
	 *          {@code RenderSystem.depthFunc(GlConst.GL_ALWAYS);} before
	 *          drawing the ESP lines. Without this code, ESP lines will be
	 *          drawn with depth test set to LEQUAL (only visible if not
	 *          obstructed).
	 */
	public static final RenderLayer.MultiPhase ESP_QUADS =
		RenderLayer.of("chestesp:esp_quads", VertexFormats.POSITION_COLOR,
			VertexFormat.DrawMode.QUADS, 1536, false, true,
			RenderLayer.MultiPhaseParameters.builder()
				.program(RenderLayer.POSITION_COLOR_PROGRAM)
				.transparency(RenderLayer.TRANSLUCENT_TRANSPARENCY)
				.depthTest(RenderLayer.ALWAYS_DEPTH_TEST).build(false));
	
	/**
	 * Returns either {@link #QUADS} or {@link #ESP_QUADS} depending on the
	 * value of {@code depthTest}.
	 *
	 * @apiNote Until 25w08a (1.21.5), turning off depth test has to be done
	 *          manually, by calling
	 *          {@code RenderSystem.depthFunc(GlConst.GL_ALWAYS);} before
	 *          drawing the ESP lines. Without this code, ESP lines will be
	 *          drawn with depth test set to LEQUAL (only visible if not
	 *          obstructed).
	 */
	public static RenderLayer getQuads(boolean depthTest)
	{
		return depthTest ? QUADS : ESP_QUADS;
	}
	
	/**
	 * Returns either {@link #LINES} or {@link #ESP_LINES} depending on the
	 * value of {@code depthTest}.
	 *
	 * @apiNote Until 25w08a (1.21.5), turning off depth test has to be done
	 *          manually, by calling
	 *          {@code RenderSystem.depthFunc(GlConst.GL_ALWAYS);} before
	 *          drawing the ESP lines. Without this code, ESP lines will be
	 *          drawn with depth test set to LEQUAL (only visible if not
	 *          obstructed).
	 */
	public static RenderLayer getLines(boolean depthTest)
	{
		return depthTest ? LINES : ESP_LINES;
	}
}
