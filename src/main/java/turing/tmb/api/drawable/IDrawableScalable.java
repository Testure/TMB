package turing.tmb.api.drawable;

import turing.tmb.api.drawable.gui.IGuiHelper;
import turing.tmb.util.Rect2i;

public interface IDrawableScalable extends IDrawable {
	void draw(IGuiHelper helper, int x, int y, int width, int height);

	default void draw(IGuiHelper helper, Rect2i area) {
		draw(helper, area.x, area.y, area.sizeX, area.sizeY);
	}
}
