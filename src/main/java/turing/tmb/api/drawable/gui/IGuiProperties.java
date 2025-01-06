package turing.tmb.api.drawable.gui;

import net.minecraft.client.gui.GuiScreen;

public interface IGuiProperties {
	Class<? extends GuiScreen> screenClass();

	int guiLeft();

	int guiTop();

	int guiXSize();

	int guiYSize();

	int screenWidth();

	int screenHeight();
}
