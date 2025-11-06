/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalDouble;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public enum ChestEspRenderLayers
{
	;
	
	/**
	 * Similar to {@link RenderType#lines()}, but with line width 2.
	 */
	public static final RenderType.CompositeRenderType LINES =
		RenderType.create("chestesp:lines", DefaultVertexFormat.POSITION_COLOR_NORMAL,
			VertexFormat.Mode.LINES, 1536, false, true,
			RenderType.CompositeState.builder()
				.setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
				.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(2)))
				.setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
				.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
				.setOutputState(RenderType.ITEM_ENTITY_TARGET)
				.setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
				.setDepthTestState(RenderType.LEQUAL_DEPTH_TEST)
				.setCullState(RenderType.NO_CULL).createCompositeState(false));
	
	/**
	 * Similar to {@link RenderType#lines()}, but with line width 2 and no
	 * depth test.
	 *
	 * @apiNote Until 25w08a (1.21.5), turning off depth test has to be done
	 *          manually, by calling
	 *          {@code RenderSystem.depthFunc(GlConst.GL_ALWAYS);} before
	 *          drawing the ESP lines. Without this code, ESP lines will be
	 *          drawn with depth test set to LEQUAL (only visible if not
	 *          obstructed).
	 */
	public static final RenderType.CompositeRenderType ESP_LINES =
		RenderType.create("chestesp:esp_lines", DefaultVertexFormat.POSITION_COLOR_NORMAL,
			VertexFormat.Mode.LINES, 1536, false, true,
			RenderType.CompositeState.builder()
				.setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
				.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(2)))
				.setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
				.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
				.setOutputState(RenderType.ITEM_ENTITY_TARGET)
				.setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
				.setDepthTestState(RenderType.NO_DEPTH_TEST)
				.setCullState(RenderType.NO_CULL).createCompositeState(false));
	
	/**
	 * Similar to {@link RenderType#debugQuads()}, but with culling enabled.
	 */
	public static final RenderType.CompositeRenderType QUADS =
		RenderType.create("chestesp:quads", DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS, 1536, false, true,
			RenderType.CompositeState.builder()
				.setShaderState(RenderType.POSITION_COLOR_SHADER)
				.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
				.setDepthTestState(RenderType.LEQUAL_DEPTH_TEST).createCompositeState(false));
	
	/**
	 * Similar to {@link RenderType#debugQuads()}, but with culling enabled
	 * and no depth test.
	 *
	 * @apiNote Until 25w08a (1.21.5), turning off depth test has to be done
	 *          manually, by calling
	 *          {@code RenderSystem.depthFunc(GlConst.GL_ALWAYS);} before
	 *          drawing the ESP lines. Without this code, ESP lines will be
	 *          drawn with depth test set to LEQUAL (only visible if not
	 *          obstructed).
	 */
	public static final RenderType.CompositeRenderType ESP_QUADS =
		RenderType.create("chestesp:esp_quads", DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS, 1536, false, true,
			RenderType.CompositeState.builder()
				.setShaderState(RenderType.POSITION_COLOR_SHADER)
				.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
				.setDepthTestState(RenderType.NO_DEPTH_TEST).createCompositeState(false));
	
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
	public static RenderType getQuads(boolean depthTest)
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
	public static RenderType getLines(boolean depthTest)
	{
		return depthTest ? LINES : ESP_LINES;
	}
}
