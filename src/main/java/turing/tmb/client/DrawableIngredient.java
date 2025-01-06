package turing.tmb.client;

import turing.tmb.api.drawable.IDrawable;
import turing.tmb.api.drawable.gui.IGuiHelper;
import turing.tmb.api.ingredient.IIngredientRenderer;

public class DrawableIngredient<T> implements IDrawable {
	private final T ingredient;
	private final IIngredientRenderer<T> renderer;

	public DrawableIngredient(T ingredient, IIngredientRenderer<T> renderer) {
		this.ingredient = ingredient;
		this.renderer = renderer;
	}

	@Override
	public int getWidth() {
		return 16;
	}

	@Override
	public int getHeight() {
		return 16;
	}

	@Override
	public void draw(IGuiHelper helper) {
		renderer.render(helper, ingredient, 0, 0);;
	}
}
