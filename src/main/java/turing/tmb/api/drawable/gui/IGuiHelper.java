package turing.tmb.api.drawable.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.render.Font;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.util.CycleTimer;

public interface IGuiHelper {
	ITMBRuntime getRuntime();

	CycleTimer getCycleTimer();

	void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int uvWidth, int uvHeight);

	void drawTexturedModalRect(int x, int y, int u, int v, int width, int height);

	void drawGuiIcon(int x, int y, int width, int height, IconCoordinate coordinate);

	void drawString(String s, int x, int y, int color, boolean isShadow);

	default void drawString(String s, int x, int y, int color) {
		drawString(s, x, y, color, false);
	}

	void drawStringWithShadow(String s, int x, int y, int color);

	void drawRect(int minX, int minY, int maxX, int maxY, int color);

	void drawIngredients(ITypedIngredient<?>... ingredients);

	void drawIngredients(IIngredientList ingredients);

	<T extends Screen> void registerScreen(Class<? extends T> clazz, IScreenHandler<T> handler);

	<T extends Screen> void blacklistScreen(Class<? extends T> clazz);

	Minecraft getMinecraft();

	Font getFont();
}
