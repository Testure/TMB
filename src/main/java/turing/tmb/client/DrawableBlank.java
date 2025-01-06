package turing.tmb.client;

import turing.tmb.api.drawable.IDrawableAnimated;
import turing.tmb.api.drawable.IDrawableStatic;
import turing.tmb.api.drawable.gui.IGuiHelper;

public class DrawableBlank implements IDrawableStatic, IDrawableAnimated {
	private final int width;
	private final int height;

	public DrawableBlank(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void draw(IGuiHelper helper, int xOffset, int yOffset, int maskTop, int maskBottom, int maskLeft, int maskRight) {

	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void draw(IGuiHelper helper) {

	}
}
