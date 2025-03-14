/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public enum RenderUtils
{
	;
	
	private static final Minecraft MC = Minecraft.getInstance();
	private static final AABB DEFAULT_BOX = new AABB(0, 0, 0, 1, 1, 1);
	
	public static void applyRegionalRenderOffset(PoseStack matrixStack)
	{
		applyRegionalRenderOffset(matrixStack, getCameraRegion());
	}
	
	public static void applyRegionalRenderOffset(PoseStack matrixStack,
		ChunkAccess chunk)
	{
		applyRegionalRenderOffset(matrixStack, RegionPos.of(chunk.getPos()));
	}
	
	public static void applyRegionalRenderOffset(PoseStack matrixStack,
		RegionPos region)
	{
		Vec3 offset = region.toVec3d().subtract(getCameraPos());
		matrixStack.translate(offset.x, offset.y, offset.z);
	}
	
	public static void applyRenderOffset(PoseStack matrixStack)
	{
		Vec3 camPos = getCameraPos();
		matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);
	}
	
	public static Vec3 getCameraPos()
	{
		Camera camera = MC.getBlockEntityRenderDispatcher().camera;
		if(camera == null)
			return Vec3.ZERO;
		
		return camera.getPosition();
	}
	
	public static BlockPos getCameraBlockPos()
	{
		Camera camera = MC.getBlockEntityRenderDispatcher().camera;
		if(camera == null)
			return BlockPos.ZERO;
		
		return camera.getBlockPosition();
	}
	
	public static RegionPos getCameraRegion()
	{
		return RegionPos.of(getCameraBlockPos());
	}
	
	public static float[] getRainbowColor()
	{
		float x = System.currentTimeMillis() % 2000 / 1000F;
		float pi = (float)Math.PI;
		
		float[] rainbow = new float[3];
		rainbow[0] = 0.5F + 0.5F * Mth.sin(x * pi);
		rainbow[1] = 0.5F + 0.5F * Mth.sin((x + 4F / 3F) * pi);
		rainbow[2] = 0.5F + 0.5F * Mth.sin((x + 8F / 3F) * pi);
		return rainbow;
	}
	
	public static void drawSolidBox(PoseStack matrixStack)
	{
		drawSolidBox(DEFAULT_BOX, matrixStack);
	}
	
	public static void drawSolidBox(AABB bb, PoseStack matrixStack)
	{
		float minX = (float)bb.minX;
		float minY = (float)bb.minY;
		float minZ = (float)bb.minZ;
		float maxX = (float)bb.maxX;
		float maxY = (float)bb.maxY;
		float maxZ = (float)bb.maxZ;
		
		Matrix4f matrix = matrixStack.last().pose();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		Tesselator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS,
			DefaultVertexFormat.POSITION);
		
		bufferBuilder.addVertex(matrix, minX, minY, minZ);
		bufferBuilder.addVertex(matrix, maxX, minY, minZ);
		bufferBuilder.addVertex(matrix, maxX, minY, maxZ);
		bufferBuilder.addVertex(matrix, minX, minY, maxZ);
		
		bufferBuilder.addVertex(matrix, minX, maxY, minZ);
		bufferBuilder.addVertex(matrix, minX, maxY, maxZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, maxZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, minZ);
		
		bufferBuilder.addVertex(matrix, minX, minY, minZ);
		bufferBuilder.addVertex(matrix, minX, maxY, minZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, minZ);
		bufferBuilder.addVertex(matrix, maxX, minY, minZ);
		
		bufferBuilder.addVertex(matrix, maxX, minY, minZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, minZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, maxZ);
		bufferBuilder.addVertex(matrix, maxX, minY, maxZ);
		
		bufferBuilder.addVertex(matrix, minX, minY, maxZ);
		bufferBuilder.addVertex(matrix, maxX, minY, maxZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, maxZ);
		bufferBuilder.addVertex(matrix, minX, maxY, maxZ);
		
		bufferBuilder.addVertex(matrix, minX, minY, minZ);
		bufferBuilder.addVertex(matrix, minX, minY, maxZ);
		bufferBuilder.addVertex(matrix, minX, maxY, maxZ);
		bufferBuilder.addVertex(matrix, minX, maxY, minZ);
		
		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
	}
	
	public static void drawSolidBox(AABB bb, VertexBuffer vertexBuffer)
	{
		Tesselator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS,
			DefaultVertexFormat.POSITION);
		drawSolidBox(bb, bufferBuilder);
		MeshData buffer = bufferBuilder.buildOrThrow();
		
		vertexBuffer.bind();
		vertexBuffer.upload(buffer);
		VertexBuffer.unbind();
	}
	
	public static void drawSolidBox(AABB bb, BufferBuilder bufferBuilder)
	{
		float minX = (float)bb.minX;
		float minY = (float)bb.minY;
		float minZ = (float)bb.minZ;
		float maxX = (float)bb.maxX;
		float maxY = (float)bb.maxY;
		float maxZ = (float)bb.maxZ;
		
		bufferBuilder.addVertex(minX, minY, minZ);
		bufferBuilder.addVertex(maxX, minY, minZ);
		bufferBuilder.addVertex(maxX, minY, maxZ);
		bufferBuilder.addVertex(minX, minY, maxZ);
		
		bufferBuilder.addVertex(minX, maxY, minZ);
		bufferBuilder.addVertex(minX, maxY, maxZ);
		bufferBuilder.addVertex(maxX, maxY, maxZ);
		bufferBuilder.addVertex(maxX, maxY, minZ);
		
		bufferBuilder.addVertex(minX, minY, minZ);
		bufferBuilder.addVertex(minX, maxY, minZ);
		bufferBuilder.addVertex(maxX, maxY, minZ);
		bufferBuilder.addVertex(maxX, minY, minZ);
		
		bufferBuilder.addVertex(maxX, minY, minZ);
		bufferBuilder.addVertex(maxX, maxY, minZ);
		bufferBuilder.addVertex(maxX, maxY, maxZ);
		bufferBuilder.addVertex(maxX, minY, maxZ);
		
		bufferBuilder.addVertex(minX, minY, maxZ);
		bufferBuilder.addVertex(maxX, minY, maxZ);
		bufferBuilder.addVertex(maxX, maxY, maxZ);
		bufferBuilder.addVertex(minX, maxY, maxZ);
		
		bufferBuilder.addVertex(minX, minY, minZ);
		bufferBuilder.addVertex(minX, minY, maxZ);
		bufferBuilder.addVertex(minX, maxY, maxZ);
		bufferBuilder.addVertex(minX, maxY, minZ);
	}
	
	public static void drawOutlinedBox(PoseStack matrixStack)
	{
		drawOutlinedBox(DEFAULT_BOX, matrixStack);
	}
	
	public static void drawOutlinedBox(AABB bb, PoseStack matrixStack)
	{
		Matrix4f matrix = matrixStack.last().pose();
		Tesselator tessellator = RenderSystem.renderThreadTesselator();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		BufferBuilder bufferBuilder = tessellator
			.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION);
		
		float minX = (float)bb.minX;
		float minY = (float)bb.minY;
		float minZ = (float)bb.minZ;
		float maxX = (float)bb.maxX;
		float maxY = (float)bb.maxY;
		float maxZ = (float)bb.maxZ;
		
		bufferBuilder.addVertex(matrix, minX, minY, minZ);
		bufferBuilder.addVertex(matrix, maxX, minY, minZ);
		
		bufferBuilder.addVertex(matrix, maxX, minY, minZ);
		bufferBuilder.addVertex(matrix, maxX, minY, maxZ);
		
		bufferBuilder.addVertex(matrix, maxX, minY, maxZ);
		bufferBuilder.addVertex(matrix, minX, minY, maxZ);
		
		bufferBuilder.addVertex(matrix, minX, minY, maxZ);
		bufferBuilder.addVertex(matrix, minX, minY, minZ);
		
		bufferBuilder.addVertex(matrix, minX, minY, minZ);
		bufferBuilder.addVertex(matrix, minX, maxY, minZ);
		
		bufferBuilder.addVertex(matrix, maxX, minY, minZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, minZ);
		
		bufferBuilder.addVertex(matrix, maxX, minY, maxZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, maxZ);
		
		bufferBuilder.addVertex(matrix, minX, minY, maxZ);
		bufferBuilder.addVertex(matrix, minX, maxY, maxZ);
		
		bufferBuilder.addVertex(matrix, minX, maxY, minZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, minZ);
		
		bufferBuilder.addVertex(matrix, maxX, maxY, minZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, maxZ);
		
		bufferBuilder.addVertex(matrix, maxX, maxY, maxZ);
		bufferBuilder.addVertex(matrix, minX, maxY, maxZ);
		
		bufferBuilder.addVertex(matrix, minX, maxY, maxZ);
		bufferBuilder.addVertex(matrix, minX, maxY, minZ);
		
		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
	}
	
	public static void drawOutlinedBox(AABB bb, VertexBuffer vertexBuffer)
	{
		Tesselator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator
			.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION);
		drawOutlinedBox(bb, bufferBuilder);
		MeshData buffer = bufferBuilder.buildOrThrow();
		
		vertexBuffer.bind();
		vertexBuffer.upload(buffer);
		VertexBuffer.unbind();
	}
	
	public static void drawOutlinedBox(AABB bb, BufferBuilder bufferBuilder)
	{
		float minX = (float)bb.minX;
		float minY = (float)bb.minY;
		float minZ = (float)bb.minZ;
		float maxX = (float)bb.maxX;
		float maxY = (float)bb.maxY;
		float maxZ = (float)bb.maxZ;
		
		bufferBuilder.addVertex(minX, minY, minZ);
		bufferBuilder.addVertex(maxX, minY, minZ);
		
		bufferBuilder.addVertex(maxX, minY, minZ);
		bufferBuilder.addVertex(maxX, minY, maxZ);
		
		bufferBuilder.addVertex(maxX, minY, maxZ);
		bufferBuilder.addVertex(minX, minY, maxZ);
		
		bufferBuilder.addVertex(minX, minY, maxZ);
		bufferBuilder.addVertex(minX, minY, minZ);
		
		bufferBuilder.addVertex(minX, minY, minZ);
		bufferBuilder.addVertex(minX, maxY, minZ);
		
		bufferBuilder.addVertex(maxX, minY, minZ);
		bufferBuilder.addVertex(maxX, maxY, minZ);
		
		bufferBuilder.addVertex(maxX, minY, maxZ);
		bufferBuilder.addVertex(maxX, maxY, maxZ);
		
		bufferBuilder.addVertex(minX, minY, maxZ);
		bufferBuilder.addVertex(minX, maxY, maxZ);
		
		bufferBuilder.addVertex(minX, maxY, minZ);
		bufferBuilder.addVertex(maxX, maxY, minZ);
		
		bufferBuilder.addVertex(maxX, maxY, minZ);
		bufferBuilder.addVertex(maxX, maxY, maxZ);
		
		bufferBuilder.addVertex(maxX, maxY, maxZ);
		bufferBuilder.addVertex(minX, maxY, maxZ);
		
		bufferBuilder.addVertex(minX, maxY, maxZ);
		bufferBuilder.addVertex(minX, maxY, minZ);
	}
	
	public static void drawCrossBox(AABB bb, PoseStack matrixStack)
	{
		float minX = (float)bb.minX;
		float minY = (float)bb.minY;
		float minZ = (float)bb.minZ;
		float maxX = (float)bb.maxX;
		float maxY = (float)bb.maxY;
		float maxZ = (float)bb.maxZ;
		
		Matrix4f matrix = matrixStack.last().pose();
		Tesselator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator
			.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION);
		
		bufferBuilder.addVertex(matrix, minX, minY, minZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, minZ);
		
		bufferBuilder.addVertex(matrix, maxX, minY, minZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, maxZ);
		
		bufferBuilder.addVertex(matrix, maxX, minY, maxZ);
		bufferBuilder.addVertex(matrix, minX, maxY, maxZ);
		
		bufferBuilder.addVertex(matrix, minX, minY, maxZ);
		bufferBuilder.addVertex(matrix, minX, maxY, minZ);
		
		bufferBuilder.addVertex(matrix, maxX, minY, minZ);
		bufferBuilder.addVertex(matrix, minX, maxY, minZ);
		
		bufferBuilder.addVertex(matrix, maxX, minY, maxZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, minZ);
		
		bufferBuilder.addVertex(matrix, minX, maxY, maxZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, maxZ);
		
		bufferBuilder.addVertex(matrix, minX, minY, minZ);
		bufferBuilder.addVertex(matrix, minX, maxY, maxZ);
		
		bufferBuilder.addVertex(matrix, minX, maxY, minZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, minZ);
		
		bufferBuilder.addVertex(matrix, minX, maxY, maxZ);
		bufferBuilder.addVertex(matrix, maxX, maxY, maxZ);
		
		bufferBuilder.addVertex(matrix, maxX, minY, minZ);
		bufferBuilder.addVertex(matrix, minX, minY, maxZ);
		
		bufferBuilder.addVertex(matrix, maxX, minY, maxZ);
		bufferBuilder.addVertex(matrix, maxX, minY, minZ);
		
		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
	}
	
	public static void drawCrossBox(AABB bb, VertexBuffer vertexBuffer)
	{
		Tesselator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator
			.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION);
		drawCrossBox(bb, bufferBuilder);
		MeshData buffer = bufferBuilder.buildOrThrow();
		
		vertexBuffer.bind();
		vertexBuffer.upload(buffer);
		VertexBuffer.unbind();
	}
	
	public static void drawCrossBox(AABB bb, BufferBuilder bufferBuilder)
	{
		float minX = (float)bb.minX;
		float minY = (float)bb.minY;
		float minZ = (float)bb.minZ;
		float maxX = (float)bb.maxX;
		float maxY = (float)bb.maxY;
		float maxZ = (float)bb.maxZ;
		
		bufferBuilder.addVertex(minX, minY, minZ);
		bufferBuilder.addVertex(maxX, maxY, minZ);
		
		bufferBuilder.addVertex(maxX, minY, minZ);
		bufferBuilder.addVertex(maxX, maxY, maxZ);
		
		bufferBuilder.addVertex(maxX, minY, maxZ);
		bufferBuilder.addVertex(minX, maxY, maxZ);
		
		bufferBuilder.addVertex(minX, minY, maxZ);
		bufferBuilder.addVertex(minX, maxY, minZ);
		
		bufferBuilder.addVertex(maxX, minY, minZ);
		bufferBuilder.addVertex(minX, maxY, minZ);
		
		bufferBuilder.addVertex(maxX, minY, maxZ);
		bufferBuilder.addVertex(maxX, maxY, minZ);
		
		bufferBuilder.addVertex(minX, minY, maxZ);
		bufferBuilder.addVertex(maxX, maxY, maxZ);
		
		bufferBuilder.addVertex(minX, minY, minZ);
		bufferBuilder.addVertex(minX, maxY, maxZ);
		
		bufferBuilder.addVertex(minX, maxY, minZ);
		bufferBuilder.addVertex(maxX, maxY, maxZ);
		
		bufferBuilder.addVertex(maxX, maxY, minZ);
		bufferBuilder.addVertex(minX, maxY, maxZ);
		
		bufferBuilder.addVertex(maxX, minY, minZ);
		bufferBuilder.addVertex(minX, minY, maxZ);
		
		bufferBuilder.addVertex(maxX, minY, maxZ);
		bufferBuilder.addVertex(minX, minY, minZ);
	}
	
	public static void drawNode(AABB bb, PoseStack matrixStack)
	{
		Matrix4f matrix = matrixStack.last().pose();
		Tesselator tessellator = RenderSystem.renderThreadTesselator();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		
		double midX = (bb.minX + bb.maxX) / 2;
		double midY = (bb.minY + bb.maxY) / 2;
		double midZ = (bb.minZ + bb.maxZ) / 2;
		
		BufferBuilder bufferBuilder = tessellator
			.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION);
		
		bufferBuilder.addVertex(matrix, (float)midX, (float)midY,
			(float)bb.maxZ);
		bufferBuilder.addVertex(matrix, (float)bb.minX, (float)midY,
			(float)midZ);
		
		bufferBuilder.addVertex(matrix, (float)bb.minX, (float)midY,
			(float)midZ);
		bufferBuilder.addVertex(matrix, (float)midX, (float)midY,
			(float)bb.minZ);
		
		bufferBuilder.addVertex(matrix, (float)midX, (float)midY,
			(float)bb.minZ);
		bufferBuilder.addVertex(matrix, (float)bb.maxX, (float)midY,
			(float)midZ);
		
		bufferBuilder.addVertex(matrix, (float)bb.maxX, (float)midY,
			(float)midZ);
		bufferBuilder.addVertex(matrix, (float)midX, (float)midY,
			(float)bb.maxZ);
		
		bufferBuilder.addVertex(matrix, (float)midX, (float)bb.maxY,
			(float)midZ);
		bufferBuilder.addVertex(matrix, (float)bb.maxX, (float)midY,
			(float)midZ);
		
		bufferBuilder.addVertex(matrix, (float)midX, (float)bb.maxY,
			(float)midZ);
		bufferBuilder.addVertex(matrix, (float)bb.minX, (float)midY,
			(float)midZ);
		
		bufferBuilder.addVertex(matrix, (float)midX, (float)bb.maxY,
			(float)midZ);
		bufferBuilder.addVertex(matrix, (float)midX, (float)midY,
			(float)bb.minZ);
		
		bufferBuilder.addVertex(matrix, (float)midX, (float)bb.maxY,
			(float)midZ);
		bufferBuilder.addVertex(matrix, (float)midX, (float)midY,
			(float)bb.maxZ);
		
		bufferBuilder.addVertex(matrix, (float)midX, (float)bb.minY,
			(float)midZ);
		bufferBuilder.addVertex(matrix, (float)bb.maxX, (float)midY,
			(float)midZ);
		
		bufferBuilder.addVertex(matrix, (float)midX, (float)bb.minY,
			(float)midZ);
		bufferBuilder.addVertex(matrix, (float)bb.minX, (float)midY,
			(float)midZ);
		
		bufferBuilder.addVertex(matrix, (float)midX, (float)bb.minY,
			(float)midZ);
		bufferBuilder.addVertex(matrix, (float)midX, (float)midY,
			(float)bb.minZ);
		
		bufferBuilder.addVertex(matrix, (float)midX, (float)bb.minY,
			(float)midZ);
		bufferBuilder.addVertex(matrix, (float)midX, (float)midY,
			(float)bb.maxZ);
		
		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
	}
	
	public static void drawNode(AABB bb, VertexBuffer vertexBuffer)
	{
		Tesselator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator
			.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION);
		drawNode(bb, bufferBuilder);
		MeshData buffer = bufferBuilder.buildOrThrow();
		
		vertexBuffer.bind();
		vertexBuffer.upload(buffer);
		VertexBuffer.unbind();
	}
	
	public static void drawNode(AABB bb, BufferBuilder bufferBuilder)
	{
		float minX = (float)bb.minX;
		float minY = (float)bb.minY;
		float minZ = (float)bb.minZ;
		float maxX = (float)bb.maxX;
		float maxY = (float)bb.maxY;
		float maxZ = (float)bb.maxZ;
		float midX = (minX + maxX) / 2F;
		float midY = (minY + maxY) / 2F;
		float midZ = (minZ + maxZ) / 2F;
		
		bufferBuilder.addVertex(midX, midY, maxZ);
		bufferBuilder.addVertex(minX, midY, midZ);
		
		bufferBuilder.addVertex(minX, midY, midZ);
		bufferBuilder.addVertex(midX, midY, minZ);
		
		bufferBuilder.addVertex(midX, midY, minZ);
		bufferBuilder.addVertex(maxX, midY, midZ);
		
		bufferBuilder.addVertex(maxX, midY, midZ);
		bufferBuilder.addVertex(midX, midY, maxZ);
		
		bufferBuilder.addVertex(midX, maxY, midZ);
		bufferBuilder.addVertex(maxX, midY, midZ);
		
		bufferBuilder.addVertex(midX, maxY, midZ);
		bufferBuilder.addVertex(minX, midY, midZ);
		
		bufferBuilder.addVertex(midX, maxY, midZ);
		bufferBuilder.addVertex(midX, midY, minZ);
		
		bufferBuilder.addVertex(midX, maxY, midZ);
		bufferBuilder.addVertex(midX, midY, maxZ);
		
		bufferBuilder.addVertex(midX, minY, midZ);
		bufferBuilder.addVertex(maxX, midY, midZ);
		
		bufferBuilder.addVertex(midX, minY, midZ);
		bufferBuilder.addVertex(minX, midY, midZ);
		
		bufferBuilder.addVertex(midX, minY, midZ);
		bufferBuilder.addVertex(midX, midY, minZ);
		
		bufferBuilder.addVertex(midX, minY, midZ);
		bufferBuilder.addVertex(midX, midY, maxZ);
	}
	
	public static void drawArrow(Vec3 from, Vec3 to, PoseStack matrixStack)
	{
		RenderSystem.setShader(GameRenderer::getPositionShader);
		
		Tesselator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator
			.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION);
		
		double startX = from.x;
		double startY = from.y;
		double startZ = from.z;
		
		double endX = to.x;
		double endY = to.y;
		double endZ = to.z;
		
		matrixStack.pushPose();
		Matrix4f matrix = matrixStack.last().pose();
		
		bufferBuilder.addVertex(matrix, (float)startX, (float)startY,
			(float)startZ);
		bufferBuilder.addVertex(matrix, (float)endX, (float)endY, (float)endZ);
		
		matrixStack.translate(endX, endY, endZ);
		matrixStack.scale(0.1F, 0.1F, 0.1F);
		
		double xDiff = endX - startX;
		double yDiff = endY - startY;
		double zDiff = endZ - startZ;
		
		float xAngle = (float)(Math.atan2(yDiff, -zDiff) + Math.toRadians(90));
		matrix.rotate(xAngle, new Vector3f(1, 0, 0));
		
		double yzDiff = Math.sqrt(yDiff * yDiff + zDiff * zDiff);
		float zAngle = (float)Math.atan2(xDiff, yzDiff);
		matrix.rotate(zAngle, new Vector3f(0, 0, 1));
		
		bufferBuilder.addVertex(matrix, 0, 2, 1);
		bufferBuilder.addVertex(matrix, -1, 2, 0);
		
		bufferBuilder.addVertex(matrix, -1, 2, 0);
		bufferBuilder.addVertex(matrix, 0, 2, -1);
		
		bufferBuilder.addVertex(matrix, 0, 2, -1);
		bufferBuilder.addVertex(matrix, 1, 2, 0);
		
		bufferBuilder.addVertex(matrix, 1, 2, 0);
		bufferBuilder.addVertex(matrix, 0, 2, 1);
		
		bufferBuilder.addVertex(matrix, 1, 2, 0);
		bufferBuilder.addVertex(matrix, -1, 2, 0);
		
		bufferBuilder.addVertex(matrix, 0, 2, 1);
		bufferBuilder.addVertex(matrix, 0, 2, -1);
		
		bufferBuilder.addVertex(matrix, 0, 0, 0);
		bufferBuilder.addVertex(matrix, 1, 2, 0);
		
		bufferBuilder.addVertex(matrix, 0, 0, 0);
		bufferBuilder.addVertex(matrix, -1, 2, 0);
		
		bufferBuilder.addVertex(matrix, 0, 0, 0);
		bufferBuilder.addVertex(matrix, 0, 2, -1);
		
		bufferBuilder.addVertex(matrix, 0, 0, 0);
		bufferBuilder.addVertex(matrix, 0, 2, 1);
		
		matrixStack.popPose();
		
		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
	}
	
	public static void drawArrow(Vec3 from, Vec3 to, VertexBuffer vertexBuffer)
	{
		Tesselator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator
			.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION);
		drawArrow(from, to, bufferBuilder);
		MeshData buffer = bufferBuilder.buildOrThrow();
		
		vertexBuffer.bind();
		vertexBuffer.upload(buffer);
		VertexBuffer.unbind();
	}
	
	public static void drawArrow(Vec3 from, Vec3 to,
		BufferBuilder bufferBuilder)
	{
		double startX = from.x;
		double startY = from.y;
		double startZ = from.z;
		
		double endX = to.x;
		double endY = to.y;
		double endZ = to.z;
		
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		
		bufferBuilder.addVertex(matrix, (float)startX, (float)startY,
			(float)startZ);
		bufferBuilder.addVertex(matrix, (float)endX, (float)endY, (float)endZ);
		
		matrix.translate((float)endX, (float)endY, (float)endZ);
		matrix.scale(0.1F, 0.1F, 0.1F);
		
		double xDiff = endX - startX;
		double yDiff = endY - startY;
		double zDiff = endZ - startZ;
		
		float xAngle = (float)(Math.atan2(yDiff, -zDiff) + Math.toRadians(90));
		matrix.rotate(xAngle, new Vector3f(1, 0, 0));
		
		double yzDiff = Math.sqrt(yDiff * yDiff + zDiff * zDiff);
		float zAngle = (float)Math.atan2(xDiff, yzDiff);
		matrix.rotate(zAngle, new Vector3f(0, 0, 1));
		
		bufferBuilder.addVertex(matrix, 0, 2, 1);
		bufferBuilder.addVertex(matrix, -1, 2, 0);
		
		bufferBuilder.addVertex(matrix, -1, 2, 0);
		bufferBuilder.addVertex(matrix, 0, 2, -1);
		
		bufferBuilder.addVertex(matrix, 0, 2, -1);
		bufferBuilder.addVertex(matrix, 1, 2, 0);
		
		bufferBuilder.addVertex(matrix, 1, 2, 0);
		bufferBuilder.addVertex(matrix, 0, 2, 1);
		
		bufferBuilder.addVertex(matrix, 1, 2, 0);
		bufferBuilder.addVertex(matrix, -1, 2, 0);
		
		bufferBuilder.addVertex(matrix, 0, 2, 1);
		bufferBuilder.addVertex(matrix, 0, 2, -1);
		
		bufferBuilder.addVertex(matrix, 0, 0, 0);
		bufferBuilder.addVertex(matrix, 1, 2, 0);
		
		bufferBuilder.addVertex(matrix, 0, 0, 0);
		bufferBuilder.addVertex(matrix, -1, 2, 0);
		
		bufferBuilder.addVertex(matrix, 0, 0, 0);
		bufferBuilder.addVertex(matrix, 0, 2, -1);
		
		bufferBuilder.addVertex(matrix, 0, 0, 0);
		bufferBuilder.addVertex(matrix, 0, 2, 1);
	}
	
	public static void drawItem(GuiGraphics context, ItemStack stack, int x,
		int y, boolean large)
	{
		PoseStack matrixStack = context.pose();
		
		matrixStack.pushPose();
		matrixStack.translate(x, y, 0);
		if(large)
			matrixStack.scale(1.5F, 1.5F, 1.5F);
		else
			matrixStack.scale(0.75F, 0.75F, 0.75F);
		
		ItemStack renderStack = stack.isEmpty() || stack.getItem() == null
			? new ItemStack(Blocks.GRASS_BLOCK) : stack;
		
		Lighting.setupFor3DItems();
		context.renderItem(renderStack, 0, 0);
		Lighting.setupForFlatItems();
		
		matrixStack.popPose();
		
		if(stack.isEmpty())
		{
			matrixStack.pushPose();
			matrixStack.translate(x, y, 250);
			if(large)
				matrixStack.scale(2, 2, 2);
			
			Font tr = MC.font;
			context.drawString(tr, "?", 3, 2, 0xf0f0f0, true);
			
			matrixStack.popPose();
		}
		
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
