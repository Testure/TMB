package turing.tmb.api.ingredient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.FontRenderer;
import org.lwjgl.opengl.GL11;
import turing.tmb.TMB;
import turing.tmb.api.drawable.builder.ITooltipBuilder;
import turing.tmb.api.drawable.gui.IGuiHelper;

import java.util.Collections;
import java.util.List;

public interface IIngredientRenderer<T> {
	void render(IGuiHelper helper, T ingredient);

	default void render(IGuiHelper helper, T ingredient, int posX, int posY) {
		GL11.glPushMatrix();
		GL11.glTranslatef(posX, posY, 0);
		render(helper, ingredient);
		GL11.glPopMatrix();
	}

	List<String> getTooltip(ITooltipBuilder tooltipBuilder, boolean isCtrl, boolean isShift);

	default void getTooltip(ITooltipBuilder tooltipBuilder, T ingredient, boolean isCtrl, boolean isShift) {
		List<String> list = getTooltip(tooltipBuilder, isCtrl, isShift);
		tooltipBuilder.addAll(list);
	}

	default FontRenderer getFontRenderer(Minecraft minecraft, T ingredient) {
		return minecraft.fontRenderer;
	}

	default int getWidth() {
		return 16;
	}

	default int getHeight() {
		return 16;
	}

	IIngredientRenderer<?> EMPTY = new IIngredientRenderer<Object>() {
		@Override
		public void render(IGuiHelper helper, Object ingredient) {

		}

		@Override
		public List<String> getTooltip(ITooltipBuilder tooltipBuilder, boolean isCtrl, boolean isShift) {
			return Collections.emptyList();
		}
	};
}
