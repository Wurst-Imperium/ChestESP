/*
 * Copyright (c) 2023-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp;

import java.util.Objects;
import java.util.stream.Stream;

import org.joml.Matrix4f;

import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.wimods.chestesp.util.RegionPos;
import net.wimods.chestesp.util.RenderUtils;
import net.wimods.chestesp.util.RotationUtils;

public final class ChestEspRenderer
{
	private static VertexBuffer solidBox;
	private static VertexBuffer outlinedBox;
	
	private final PoseStack matrixStack;
	private final RegionPos region;
	private final Vec3 start;
	
	public ChestEspRenderer(PoseStack matrixStack, float partialTicks)
	{
		this.matrixStack = matrixStack;
		region = RenderUtils.getCameraRegion();
		start = RotationUtils.getClientLookVec(partialTicks)
			.add(RenderUtils.getCameraPos()).subtract(region.toVec3d());
	}
	
	public void renderBoxes(ChestEspGroup group)
	{
		float[] colorF = group.getColorF();
		
		for(AABB box : group.getBoxes())
		{
			matrixStack.pushPose();
			
			matrixStack.translate(box.minX - region.x(), box.minY,
				box.minZ - region.z());
			
			matrixStack.scale((float)(box.maxX - box.minX),
				(float)(box.maxY - box.minY), (float)(box.maxZ - box.minZ));
			
			Matrix4f viewMatrix = matrixStack.last().pose();
			Matrix4f projMatrix = RenderSystem.getProjectionMatrix();
			CompiledShaderProgram shader = RenderSystem.getShader();
			
			RenderSystem.setShaderColor(colorF[0], colorF[1], colorF[2], 0.25F);
			solidBox.bind();
			solidBox.drawWithShader(viewMatrix, projMatrix, shader);
			VertexBuffer.unbind();
			
			RenderSystem.setShaderColor(colorF[0], colorF[1], colorF[2], 0.5F);
			outlinedBox.bind();
			outlinedBox.drawWithShader(viewMatrix, projMatrix, shader);
			VertexBuffer.unbind();
			
			matrixStack.popPose();
		}
	}
	
	public void renderLines(ChestEspGroup group)
	{
		if(group.getBoxes().isEmpty())
			return;
		
		Matrix4f matrix = matrixStack.last().pose();
		Tesselator tessellator = RenderSystem.renderThreadTesselator();
		
		float[] colorF = group.getColorF();
		RenderSystem.setShaderColor(colorF[0], colorF[1], colorF[2], 0.5F);
		
		BufferBuilder bufferBuilder = tessellator
			.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION);
		
		for(AABB box : group.getBoxes())
		{
			Vec3 end = box.getCenter().subtract(region.toVec3d());
			
			bufferBuilder.addVertex(matrix, (float)start.x, (float)start.y,
				(float)start.z);
			
			bufferBuilder.addVertex(matrix, (float)end.x, (float)end.y,
				(float)end.z);
		}
		
		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
	}
	
	public static void prepareBuffers()
	{
		closeBuffers();
		solidBox = new VertexBuffer(BufferUsage.STATIC_WRITE);
		outlinedBox = new VertexBuffer(BufferUsage.STATIC_WRITE);
		
		AABB box = new AABB(BlockPos.ZERO);
		RenderUtils.drawSolidBox(box, solidBox);
		RenderUtils.drawOutlinedBox(box, outlinedBox);
	}
	
	public static void closeBuffers()
	{
		Stream.of(solidBox, outlinedBox).filter(Objects::nonNull)
			.forEach(VertexBuffer::close);
		solidBox = null;
		outlinedBox = null;
	}
}
