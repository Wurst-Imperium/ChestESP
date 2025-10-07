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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.wimods.chestesp.ChestEspRenderLayers;

public enum RenderUtils
{
	;
	
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	
	public static Vec3d getCameraPos()
	{
		Camera camera = MC.gameRenderer.getCamera();
		if(camera == null)
			return Vec3d.ZERO;
		
		return camera.getPos();
	}
	
	public static VertexConsumerProvider.Immediate getVCP()
	{
		return MC.getBufferBuilders().getEntityVertexConsumers();
	}
	
	private static Vec3d getTracerOrigin(float partialTicks)
	{
		Vec3d start = RotationUtils.getClientLookVec(partialTicks).multiply(10);
		if(MC.options.getPerspective() == Perspective.THIRD_PERSON_FRONT)
			start = start.negate();
		
		return start;
	}
	
	public static void drawTracers(MatrixStack matrices, float partialTicks,
		List<Vec3d> ends, int color, boolean depthTest)
	{
		VertexConsumerProvider.Immediate vcp = getVCP();
		RenderLayer layer = ChestEspRenderLayers.getLines(depthTest);
		VertexConsumer buffer = vcp.getBuffer(layer);
		
		Vec3d start = getTracerOrigin(partialTicks);
		Vec3d offset = getCameraPos().negate();
		for(Vec3d end : ends)
			drawLine(matrices, buffer, start, end.add(offset), color);
		
		vcp.draw(layer);
	}
	
	public static void drawLine(MatrixStack matrices, VertexConsumer buffer,
		Vec3d start, Vec3d end, int color)
	{
		Entry entry = matrices.peek();
		float x1 = (float)start.x;
		float y1 = (float)start.y;
		float z1 = (float)start.z;
		float x2 = (float)end.x;
		float y2 = (float)end.y;
		float z2 = (float)end.z;
		drawLine(entry, buffer, x1, y1, z1, x2, y2, z2, color);
	}
	
	public static void drawLine(MatrixStack.Entry entry, VertexConsumer buffer,
		float x1, float y1, float z1, float x2, float y2, float z2, int color)
	{
		Vector3f normal = new Vector3f(x2, y2, z2).sub(x1, y1, z1).normalize();
		buffer.vertex(entry, x1, y1, z1).color(color).normal(entry, normal);
		
		// If the line goes through the screen, add another vertex there. This
		// works around a bug in Minecraft's line shader.
		float t = new Vector3f(x1, y1, z1).negate().dot(normal);
		float length = new Vector3f(x2, y2, z2).sub(x1, y1, z1).length();
		if(t > 0 && t < length)
		{
			Vector3f closeToCam = new Vector3f(normal).mul(t).add(x1, y1, z1);
			buffer.vertex(entry, closeToCam).color(color).normal(entry, normal);
			buffer.vertex(entry, closeToCam).color(color).normal(entry, normal);
		}
		
		buffer.vertex(entry, x2, y2, z2).color(color).normal(entry, normal);
	}
	
	public static void drawSolidBoxes(MatrixStack matrices, List<Box> boxes,
		int color, boolean depthTest)
	{
		VertexConsumerProvider.Immediate vcp = getVCP();
		RenderLayer layer = ChestEspRenderLayers.getQuads(depthTest);
		VertexConsumer buffer = vcp.getBuffer(layer);
		
		Vec3d camOffset = getCameraPos().negate();
		for(Box box : boxes)
			drawSolidBox(matrices, buffer, box.offset(camOffset), color);
		
		vcp.draw(layer);
	}
	
	public static void drawSolidBox(MatrixStack matrices, VertexConsumer buffer,
		Box box, int color)
	{
		MatrixStack.Entry entry = matrices.peek();
		float x1 = (float)box.minX;
		float y1 = (float)box.minY;
		float z1 = (float)box.minZ;
		float x2 = (float)box.maxX;
		float y2 = (float)box.maxY;
		float z2 = (float)box.maxZ;
		
		buffer.vertex(entry, x1, y1, z1).color(color);
		buffer.vertex(entry, x2, y1, z1).color(color);
		buffer.vertex(entry, x2, y1, z2).color(color);
		buffer.vertex(entry, x1, y1, z2).color(color);
		
		buffer.vertex(entry, x1, y2, z1).color(color);
		buffer.vertex(entry, x1, y2, z2).color(color);
		buffer.vertex(entry, x2, y2, z2).color(color);
		buffer.vertex(entry, x2, y2, z1).color(color);
		
		buffer.vertex(entry, x1, y1, z1).color(color);
		buffer.vertex(entry, x1, y2, z1).color(color);
		buffer.vertex(entry, x2, y2, z1).color(color);
		buffer.vertex(entry, x2, y1, z1).color(color);
		
		buffer.vertex(entry, x2, y1, z1).color(color);
		buffer.vertex(entry, x2, y2, z1).color(color);
		buffer.vertex(entry, x2, y2, z2).color(color);
		buffer.vertex(entry, x2, y1, z2).color(color);
		
		buffer.vertex(entry, x1, y1, z2).color(color);
		buffer.vertex(entry, x2, y1, z2).color(color);
		buffer.vertex(entry, x2, y2, z2).color(color);
		buffer.vertex(entry, x1, y2, z2).color(color);
		
		buffer.vertex(entry, x1, y1, z1).color(color);
		buffer.vertex(entry, x1, y1, z2).color(color);
		buffer.vertex(entry, x1, y2, z2).color(color);
		buffer.vertex(entry, x1, y2, z1).color(color);
	}
	
	public static void drawOutlinedBoxes(MatrixStack matrices, List<Box> boxes,
		int color, boolean depthTest)
	{
		VertexConsumerProvider.Immediate vcp = getVCP();
		RenderLayer layer = ChestEspRenderLayers.getLines(depthTest);
		VertexConsumer buffer = vcp.getBuffer(layer);
		
		Vec3d camOffset = getCameraPos().negate();
		for(Box box : boxes)
			drawOutlinedBox(matrices, buffer, box.offset(camOffset), color);
		
		vcp.draw(layer);
	}
	
	public static void drawOutlinedBox(MatrixStack matrices,
		VertexConsumer buffer, Box box, int color)
	{
		MatrixStack.Entry entry = matrices.peek();
		float x1 = (float)box.minX;
		float y1 = (float)box.minY;
		float z1 = (float)box.minZ;
		float x2 = (float)box.maxX;
		float y2 = (float)box.maxY;
		float z2 = (float)box.maxZ;
		
		// bottom lines
		buffer.vertex(entry, x1, y1, z1).color(color).normal(entry, 1, 0, 0);
		buffer.vertex(entry, x2, y1, z1).color(color).normal(entry, 1, 0, 0);
		buffer.vertex(entry, x1, y1, z1).color(color).normal(entry, 0, 0, 1);
		buffer.vertex(entry, x1, y1, z2).color(color).normal(entry, 0, 0, 1);
		buffer.vertex(entry, x2, y1, z1).color(color).normal(entry, 0, 0, 1);
		buffer.vertex(entry, x2, y1, z2).color(color).normal(entry, 0, 0, 1);
		buffer.vertex(entry, x1, y1, z2).color(color).normal(entry, 1, 0, 0);
		buffer.vertex(entry, x2, y1, z2).color(color).normal(entry, 1, 0, 0);
		
		// top lines
		buffer.vertex(entry, x1, y2, z1).color(color).normal(entry, 1, 0, 0);
		buffer.vertex(entry, x2, y2, z1).color(color).normal(entry, 1, 0, 0);
		buffer.vertex(entry, x1, y2, z1).color(color).normal(entry, 0, 0, 1);
		buffer.vertex(entry, x1, y2, z2).color(color).normal(entry, 0, 0, 1);
		buffer.vertex(entry, x2, y2, z1).color(color).normal(entry, 0, 0, 1);
		buffer.vertex(entry, x2, y2, z2).color(color).normal(entry, 0, 0, 1);
		buffer.vertex(entry, x1, y2, z2).color(color).normal(entry, 1, 0, 0);
		buffer.vertex(entry, x2, y2, z2).color(color).normal(entry, 1, 0, 0);
		
		// side lines
		buffer.vertex(entry, x1, y1, z1).color(color).normal(entry, 0, 1, 0);
		buffer.vertex(entry, x1, y2, z1).color(color).normal(entry, 0, 1, 0);
		buffer.vertex(entry, x2, y1, z1).color(color).normal(entry, 0, 1, 0);
		buffer.vertex(entry, x2, y2, z1).color(color).normal(entry, 0, 1, 0);
		buffer.vertex(entry, x1, y1, z2).color(color).normal(entry, 0, 1, 0);
		buffer.vertex(entry, x1, y2, z2).color(color).normal(entry, 0, 1, 0);
		buffer.vertex(entry, x2, y1, z2).color(color).normal(entry, 0, 1, 0);
		buffer.vertex(entry, x2, y2, z2).color(color).normal(entry, 0, 1, 0);
	}
}
