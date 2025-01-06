package turing.tmb.api.drawable;

import org.lwjgl.opengl.GL11;
import turing.tmb.api.drawable.gui.IGuiHelper;

public interface IDrawable {
	int getWidth();

	int getHeight();

	void draw(IGuiHelper helper);

	default void draw(IGuiHelper guiHelper, int xOffset, int yOffset) {
		GL11.glPushMatrix();
		GL11.glTranslatef(xOffset, yOffset, 0);
		draw(guiHelper);
		GL11.glPopMatrix();
	}
}
