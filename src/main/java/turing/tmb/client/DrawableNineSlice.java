package turing.tmb.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.tessellator.Tessellator;
import turing.tmb.api.drawable.gui.IGuiHelper;

public class DrawableNineSlice {
	private final String image;

	public DrawableNineSlice(String image) {
		this.image = image;
	}

	public void draw(Minecraft mc, IGuiHelper helper, int xOffset, int yOffset, int width, int height, int sliceTop, int sliceBottom, int sliceLeft, int sliceRight) {
		TextureManager textureManager = mc.textureManager;
		textureManager.bindTexture(textureManager.loadTexture(image));

		Tessellator tessellator = Tessellator.instance;

		float minU = 0;
		float maxU = width;
		float minV = 0;
		float maxV = height;
		float uSize = maxU - minU;
		float vSize = maxV - minV;

		float uLeft = minU + uSize * (sliceLeft / (float) width);
		float uRight = maxU - uSize * (sliceRight / (float) width);
		float vTop = minV + vSize * (sliceTop / (float) height);
		float vBottom = maxV - vSize * (sliceBottom / (float) height);

		tessellator.startDrawing(7);

		draw(tessellator, minU, minV, uLeft, vTop, xOffset, yOffset, sliceLeft, sliceTop);
		draw(tessellator, minU, vBottom, uLeft, maxV, xOffset, yOffset + height - sliceBottom, sliceLeft, sliceBottom);
		draw(tessellator, uRight, minV, maxU, vTop, xOffset + width - sliceRight, yOffset, sliceRight, sliceTop);
		draw(tessellator, uRight, vBottom, maxU, maxV, xOffset + width - sliceRight, yOffset + height - sliceBottom, sliceRight, sliceBottom);

		int middleWidth = width - sliceLeft - sliceRight;
		int middleHeight = height - sliceTop - sliceBottom;
		int tiledMiddleWidth = width - sliceLeft - sliceRight;
		int tiledMiddleHeight = height - sliceTop - sliceBottom;
		if (tiledMiddleWidth > 0) {
			drawTiled(tessellator, uLeft, minV, uRight, vTop, xOffset + sliceLeft, yOffset, tiledMiddleWidth, sliceTop, middleWidth, sliceTop);
			drawTiled(tessellator, uLeft, vBottom, uRight, maxV, xOffset + sliceLeft, yOffset + height - sliceBottom, tiledMiddleWidth, sliceBottom, middleWidth, sliceBottom);
		}
		if (tiledMiddleHeight > 0) {
			drawTiled(tessellator, minU, vTop, uLeft, vBottom, xOffset, yOffset + sliceTop, sliceLeft, tiledMiddleHeight, sliceLeft, middleHeight);
			drawTiled(tessellator, uRight, vTop, maxU, vBottom, xOffset + width - sliceRight, yOffset + sliceTop, sliceRight, tiledMiddleHeight, sliceRight, middleHeight);
		}
		if (tiledMiddleHeight > 0 && tiledMiddleWidth > 0) {
			drawTiled(tessellator, uLeft, vTop, uRight, vBottom, xOffset + sliceLeft, yOffset + sliceTop, tiledMiddleWidth, tiledMiddleHeight, middleWidth, middleHeight);
		}

		tessellator.draw();
	}

	private void drawTiled(Tessellator tessellator, float uMin, float vMin, float uMax, float vMax, int xOffset, int yOffset, int tiledWidth, int tiledHeight, int width, int height) {
		int xTileCount = tiledWidth / width;
		int yTileCount = tiledHeight / height;
		int xRemainder = tiledWidth - (xTileCount * width);
		int yRemainder = tiledHeight - (yTileCount * height);

		int yStart = yOffset + tiledHeight;

		float uSize = uMax - uMin;
		float vSize = vMax - vMin;

		for (int xTile = 0; xTile <= xTileCount; xTile++) {
			for (int yTile = 0; yTile <= yTileCount; yTile++) {
				int tileWidth = (xTile == xTileCount) ? xRemainder : width;
				int tileHeight = (yTile == yTileCount) ? yRemainder : height;
				int x = xOffset + (xTile * width);
				int y = yStart - ((yTile + 1) * height);
				if (tileWidth > 0 && tileHeight > 0) {
					int maskRight = width - tileWidth;
					int maskTop = height - tileHeight;
					float uOffset = (maskRight / (float) width) * uSize;
					float vOffset = (maskTop / (float) height) * vSize;
					draw(tessellator, uMin, vMin + vOffset,  uMax - uOffset, vMax, x, y + maskTop, tileWidth, tileHeight);
				}
			}
		}
	}

	private static void draw(Tessellator tessellator, float minU, float minV, float maxU, float maxV, int xOffset, int yOffset, int width, int height) {
		tessellator.addVertexWithUV(xOffset, yOffset + height, 0, minU, maxV);
		tessellator.addVertexWithUV(xOffset + width, yOffset + height, 0, maxU, maxV);
		tessellator.addVertexWithUV(xOffset + width, yOffset, 0, maxU, minV);
		tessellator.addVertexWithUV(xOffset, yOffset, 0, minU, minV);
	}
}
