package turing.tmb.api.drawable.gui;

import net.minecraft.client.gui.Screen;

public interface IGuiProperties {
	Class<? extends Screen> screenClass();

	int guiLeft();

	int guiTop();

	int guiXSize();

	int guiYSize();

	int screenWidth();

	int screenHeight();
}
