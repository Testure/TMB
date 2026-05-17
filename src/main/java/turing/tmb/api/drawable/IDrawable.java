package turing.tmb.api.drawable;

import net.minecraft.client.render.renderer.GLRenderer;

import turing.tmb.api.drawable.gui.IGuiHelper;

public interface IDrawable {
	int getWidth();

	int getHeight();

	void draw(IGuiHelper helper);

	default void draw(IGuiHelper guiHelper, int xOffset, int yOffset) {
		GLRenderer.pushFrame();
		GLRenderer.modelM4f().translate(xOffset,yOffset,0);
		draw(guiHelper);
		GLRenderer.popFrame();
	}
}
