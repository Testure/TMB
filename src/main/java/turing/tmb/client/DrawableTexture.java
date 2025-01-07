package turing.tmb.client;

import turing.tmb.api.drawable.IDrawableStatic;
import turing.tmb.api.drawable.gui.IGuiHelper;

public class DrawableTexture implements IDrawableStatic {
	private final String texture;
	private final int textureWidth;
	private final int textureHeight;

	private final int u;
	private final int v;
	private final int width;
	private final int height;
	private final int paddingTop;
	private final int paddingBottom;
	private final int paddingLeft;
	private final int paddingRight;

	public DrawableTexture(String texture, int u, int v, int width, int height, int paddingTop, int paddingBottom, int paddingLeft, int paddingRight, int textureWidth, int textureHeight) {
		this.texture = texture;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
		this.paddingTop = paddingTop;
		this.paddingBottom = paddingBottom;
		this.paddingLeft = paddingLeft;
		this.paddingRight = paddingRight;
	}

	public DrawableTexture(String texture, int u, int v, int width, int height, int paddingTop, int paddingBottom, int paddingLeft, int paddingRight) {
		this(texture, u, v, width, height, paddingTop, paddingBottom, paddingLeft, paddingRight, width, height);
	}

	public DrawableTexture(String texture, int u, int v, int width, int height) {
		this(texture, u, v, width, height, 0, 0, 0, 0);
	}

	public DrawableTexture(String texture, int u, int v, int width, int height, int textureWidth, int textureHeight) {
		this(texture, u, v, width, height, 0, 0, 0, 0, textureWidth, textureHeight);
	}

	@Override
	public void draw(IGuiHelper helper, int xOffset, int yOffset, int maskTop, int maskBottom, int maskLeft, int maskRight) {
		int texture1 = helper.getMinecraft().renderEngine.getTexture(texture);
		helper.getMinecraft().renderEngine.bindTexture(texture1);

		int x = xOffset + this.paddingLeft + maskLeft;
		int y = yOffset + this.paddingTop + maskTop;
		int u = this.u + maskLeft;
		int v = this.v + maskTop;
		int width = this.width - maskRight - maskLeft;
		int height = this.height - maskBottom - maskTop;

		helper.drawTexturedModalRect(x, y, u, v, width, height, textureWidth - maskRight - maskLeft, textureHeight - maskTop - maskBottom);
	}

	@Override
	public int getWidth() {
		return width + paddingLeft + paddingRight;
	}

	@Override
	public int getHeight() {
		return height + paddingTop + paddingBottom;
	}

	@Override
	public void draw(IGuiHelper guiHelper, int xOffset, int yOffset) {
		draw(guiHelper, xOffset, yOffset, 0, 0, 0, 0);
	}

	@Override
	public void draw(IGuiHelper helper) {
		draw(helper, 0, 0);
	}
}
