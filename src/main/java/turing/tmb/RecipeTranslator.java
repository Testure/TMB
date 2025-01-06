package turing.tmb;

import turing.tmb.api.recipe.IRecipeTranslator;

public abstract class RecipeTranslator<T> implements IRecipeTranslator<T> {
	protected final T recipe;

	public RecipeTranslator(T recipe) {
		this.recipe = recipe;
	}

	@Override
	public T getOriginal() {
		return recipe;
	}
}
