/*
 * Copyright (c) 2023-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import java.util.List;

import org.joml.Vector3f;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.wimods.chestesp.ChestEspRenderLayers;

public enum RenderUtils
{
	;
	
	private static final Minecraft MC = Minecraft.getInstance();
	
	public static Vec3 getCameraPos()
	{
		Camera camera = MC.getBlockEntityRenderDispatcher().camera;
		if(camera == null)
			return Vec3.ZERO;
		
		return camera.getPosition();
	}
	
	public static MultiBufferSource.BufferSource getVCP()
	{
		return MC.renderBuffers().bufferSource();
	}
	
	private static Vec3 getTracerOrigin(float partialTicks)
	{
		Vec3 start = RotationUtils.getClientLookVec(partialTicks).scale(10);
		if(MC.options.getCameraType() == CameraType.THIRD_PERSON_FRONT)
			start = start.reverse();
		
		return start;
	}
	
	public static void drawTracers(PoseStack matrices, float partialTicks,
		List<Vec3> ends, int color, boolean depthTest)
	{
		int depthFunc = depthTest ? GlConst.GL_LEQUAL : GlConst.GL_ALWAYS;
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(depthFunc);
		
		MultiBufferSource.BufferSource vcp = getVCP();
		RenderType layer = ChestEspRenderLayers.getLines(depthTest);
		VertexConsumer buffer = vcp.getBuffer(layer);
		
		Vec3 start = getTracerOrigin(partialTicks);
		Vec3 offset = getCameraPos().reverse();
		for(Vec3 end : ends)
			drawLine(matrices, buffer, start, end.add(offset), color);
		
		vcp.endBatch(layer);
	}
	
	public static void drawLine(PoseStack matrices, VertexConsumer buffer,
		Vec3 start, Vec3 end, int color)
	{
		PoseStack.Pose entry = matrices.last();
		float x1 = (float)start.x;
		float y1 = (float)start.y;
		float z1 = (float)start.z;
		float x2 = (float)end.x;
		float y2 = (float)end.y;
		float z2 = (float)end.z;
		drawLine(entry, buffer, x1, y1, z1, x2, y2, z2, color);
	}
	
	public static void drawLine(PoseStack.Pose entry, VertexConsumer buffer,
		float x1, float y1, float z1, float x2, float y2, float z2, int color)
	{
		Vector3f normal = new Vector3f(x2, y2, z2).sub(x1, y1, z1).normalize();
		buffer.addVertex(entry, x1, y1, z1).setColor(color).setNormal(entry,
			normal.x, normal.y, normal.z);
		
		// If the line goes through the screen, add another vertex there. This
		// works around a bug in Minecraft's line shader.
		float t = new Vector3f(x1, y1, z1).negate().dot(normal);
		float length = new Vector3f(x2, y2, z2).sub(x1, y1, z1).length();
		if(t > 0 && t < length)
		{
			Vector3f closeToCam = new Vector3f(normal).mul(t).add(x1, y1, z1);
			buffer.addVertex(entry, closeToCam).setColor(color).setNormal(entry,
				normal.x, normal.y, normal.z);
			buffer.addVertex(entry, closeToCam).setColor(color).setNormal(entry,
				normal.x, normal.y, normal.z);
		}
		
		buffer.addVertex(entry, x2, y2, z2).setColor(color).setNormal(entry,
			normal.x, normal.y, normal.z);
	}
	
	public static void drawSolidBoxes(PoseStack matrices, List<AABB> boxes,
		int color, boolean depthTest)
	{
		int depthFunc = depthTest ? GlConst.GL_LEQUAL : GlConst.GL_ALWAYS;
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(depthFunc);
		
		MultiBufferSource.BufferSource vcp = getVCP();
		RenderType layer = ChestEspRenderLayers.getQuads(depthTest);
		VertexConsumer buffer = vcp.getBuffer(layer);
		
		Vec3 camOffset = getCameraPos().reverse();
		for(AABB box : boxes)
			drawSolidBox(matrices, buffer, box.move(camOffset), color);
		
		vcp.endBatch(layer);
	}
	
	public static void drawSolidBox(PoseStack matrices, VertexConsumer buffer,
		AABB box, int color)
	{
		PoseStack.Pose entry = matrices.last();
		float x1 = (float)box.minX;
		float y1 = (float)box.minY;
		float z1 = (float)box.minZ;
		float x2 = (float)box.maxX;
		float y2 = (float)box.maxY;
		float z2 = (float)box.maxZ;
		
		buffer.addVertex(entry, x1, y1, z1).setColor(color);
		buffer.addVertex(entry, x2, y1, z1).setColor(color);
		buffer.addVertex(entry, x2, y1, z2).setColor(color);
		buffer.addVertex(entry, x1, y1, z2).setColor(color);
		
		buffer.addVertex(entry, x1, y2, z1).setColor(color);
		buffer.addVertex(entry, x1, y2, z2).setColor(color);
		buffer.addVertex(entry, x2, y2, z2).setColor(color);
		buffer.addVertex(entry, x2, y2, z1).setColor(color);
		
		buffer.addVertex(entry, x1, y1, z1).setColor(color);
		buffer.addVertex(entry, x1, y2, z1).setColor(color);
		buffer.addVertex(entry, x2, y2, z1).setColor(color);
		buffer.addVertex(entry, x2, y1, z1).setColor(color);
		
		buffer.addVertex(entry, x2, y1, z1).setColor(color);
		buffer.addVertex(entry, x2, y2, z1).setColor(color);
		buffer.addVertex(entry, x2, y2, z2).setColor(color);
		buffer.addVertex(entry, x2, y1, z2).setColor(color);
		
		buffer.addVertex(entry, x1, y1, z2).setColor(color);
		buffer.addVertex(entry, x2, y1, z2).setColor(color);
		buffer.addVertex(entry, x2, y2, z2).setColor(color);
		buffer.addVertex(entry, x1, y2, z2).setColor(color);
		
		buffer.addVertex(entry, x1, y1, z1).setColor(color);
		buffer.addVertex(entry, x1, y1, z2).setColor(color);
		buffer.addVertex(entry, x1, y2, z2).setColor(color);
		buffer.addVertex(entry, x1, y2, z1).setColor(color);
	}
	
	public static void drawOutlinedBoxes(PoseStack matrices, List<AABB> boxes,
		int color, boolean depthTest)
	{
		int depthFunc = depthTest ? GlConst.GL_LEQUAL : GlConst.GL_ALWAYS;
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(depthFunc);
		
		MultiBufferSource.BufferSource vcp = getVCP();
		RenderType layer = ChestEspRenderLayers.getLines(depthTest);
		VertexConsumer buffer = vcp.getBuffer(layer);
		
		Vec3 camOffset = getCameraPos().reverse();
		for(AABB box : boxes)
			drawOutlinedBox(matrices, buffer, box.move(camOffset), color);
		
		vcp.endBatch(layer);
	}
	
	public static void drawOutlinedBox(PoseStack matrices,
		VertexConsumer buffer, AABB box, int color)
	{
		PoseStack.Pose entry = matrices.last();
		float x1 = (float)box.minX;
		float y1 = (float)box.minY;
		float z1 = (float)box.minZ;
		float x2 = (float)box.maxX;
		float y2 = (float)box.maxY;
		float z2 = (float)box.maxZ;
		
		// bottom lines
		buffer.addVertex(entry, x1, y1, z1).setColor(color).setNormal(entry, 1,
			0, 0);
		buffer.addVertex(entry, x2, y1, z1).setColor(color).setNormal(entry, 1,
			0, 0);
		buffer.addVertex(entry, x1, y1, z1).setColor(color).setNormal(entry, 0,
			0, 1);
		buffer.addVertex(entry, x1, y1, z2).setColor(color).setNormal(entry, 0,
			0, 1);
		buffer.addVertex(entry, x2, y1, z1).setColor(color).setNormal(entry, 0,
			0, 1);
		buffer.addVertex(entry, x2, y1, z2).setColor(color).setNormal(entry, 0,
			0, 1);
		buffer.addVertex(entry, x1, y1, z2).setColor(color).setNormal(entry, 1,
			0, 0);
		buffer.addVertex(entry, x2, y1, z2).setColor(color).setNormal(entry, 1,
			0, 0);
		
		// top lines
		buffer.addVertex(entry, x1, y2, z1).setColor(color).setNormal(entry, 1,
			0, 0);
		buffer.addVertex(entry, x2, y2, z1).setColor(color).setNormal(entry, 1,
			0, 0);
		buffer.addVertex(entry, x1, y2, z1).setColor(color).setNormal(entry, 0,
			0, 1);
		buffer.addVertex(entry, x1, y2, z2).setColor(color).setNormal(entry, 0,
			0, 1);
		buffer.addVertex(entry, x2, y2, z1).setColor(color).setNormal(entry, 0,
			0, 1);
		buffer.addVertex(entry, x2, y2, z2).setColor(color).setNormal(entry, 0,
			0, 1);
		buffer.addVertex(entry, x1, y2, z2).setColor(color).setNormal(entry, 1,
			0, 0);
		buffer.addVertex(entry, x2, y2, z2).setColor(color).setNormal(entry, 1,
			0, 0);
		
		// side lines
		buffer.addVertex(entry, x1, y1, z1).setColor(color).setNormal(entry, 0,
			1, 0);
		buffer.addVertex(entry, x1, y2, z1).setColor(color).setNormal(entry, 0,
			1, 0);
		buffer.addVertex(entry, x2, y1, z1).setColor(color).setNormal(entry, 0,
			1, 0);
		buffer.addVertex(entry, x2, y2, z1).setColor(color).setNormal(entry, 0,
			1, 0);
		buffer.addVertex(entry, x1, y1, z2).setColor(color).setNormal(entry, 0,
			1, 0);
		buffer.addVertex(entry, x1, y2, z2).setColor(color).setNormal(entry, 0,
			1, 0);
		buffer.addVertex(entry, x2, y1, z2).setColor(color).setNormal(entry, 0,
			1, 0);
		buffer.addVertex(entry, x2, y2, z2).setColor(color).setNormal(entry, 0,
			1, 0);
	}
}
