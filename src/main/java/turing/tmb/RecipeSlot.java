package turing.tmb;

import turing.tmb.api.drawable.builder.ITooltipBuilder;
import turing.tmb.api.ingredient.IIngredientType;
import turing.tmb.api.recipe.IRecipeSlot;
import turing.tmb.api.recipe.RecipeIngredientRole;
import turing.tmb.api.recipe.SlotTooltipFunction;

import java.util.List;

public class RecipeSlot<I, T extends IIngredientType<I>> implements IRecipeSlot<I, T> {
	private final T type;
	private final RecipeIngredientRole role;
	private SlotTooltipFunction<I> tooltipFunction;
	public int x;
	public int y;

	public RecipeSlot(T type, RecipeIngredientRole role) {
		this.type = type;
		this.role = role;
	}

	public void setTooltipFunction(SlotTooltipFunction<I> tooltipFunction) {
		this.tooltipFunction = tooltipFunction;
	}

	@Override
	public List<String> getTooltips(ITooltipBuilder tooltipBuilder, I ingredient, int mouseX, int mouseY, boolean isCtrl, boolean isShift) {
		if (tooltipFunction != null) {
			return tooltipFunction.apply(tooltipBuilder, ingredient, mouseX, mouseY, isCtrl, isShift);
		}
		return IRecipeSlot.super.getTooltips(tooltipBuilder, ingredient, mouseX, mouseY, isCtrl, isShift);
	}

	@Override
	public RecipeIngredientRole getRole() {
		return role;
	}

	@Override
	public T getType() {
		return type;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public IRecipeSlot<I, T> setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}
}
