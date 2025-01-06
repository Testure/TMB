package turing.tmb.api.recipe;

import org.lwjgl.opengl.GL11;
import turing.tmb.api.drawable.IDrawable;
import turing.tmb.api.drawable.builder.ITooltipBuilder;
import turing.tmb.api.drawable.gui.IGuiHelper;
import turing.tmb.api.drawable.placement.IPlaceable;
import turing.tmb.api.ingredient.IIngredientType;

import java.util.Collections;
import java.util.List;

public interface IRecipeSlot<I, T extends IIngredientType<I>> extends IDrawable, IPlaceable<IRecipeSlot<I, T>> {
	RecipeIngredientRole getRole();

	T getType();

	int getX();

	int getY();

	@Override
	default void draw(IGuiHelper helper) {
		GL11.glPushMatrix();
		GL11.glColor4f(1F, 1F, 1F, 1F);
		helper.getMinecraft().textureManager.loadTexture("/assets/minecraft/textures/gui/container/crafting.png").bind();
		helper.drawTexturedModalRect(0, 0, 7, 83, 18, 18);
		GL11.glPopMatrix();
	}

	@Override
	default int getHeight() {
		return 16;
	}

	@Override
	default int getWidth() {
		return 16;
	}

	default List<String> getTooltips(ITooltipBuilder tooltipBuilder, I ingredient, int mouseX, int mouseY, boolean isCtrl, boolean isShift) {
		return Collections.emptyList();
	}
}
