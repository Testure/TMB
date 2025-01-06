package turing.tmb.api.drawable;

import turing.tmb.api.drawable.gui.IGuiHelper;

public interface IDrawableStatic extends IDrawable {
	void draw(IGuiHelper helper, int xOffset, int yOffset, int maskTop, int maskBottom, int maskLeft, int maskRight);
}
