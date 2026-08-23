package turing.tmb;

import turing.tmb.api.ingredient.ITypedIngredient;

public class InfoRecipeTranslator extends RecipeTranslator<IngredientInfo> {
	public InfoRecipeTranslator(IngredientInfo recipe) {
		super(recipe);
	}

	@Override
	public <I> boolean isValidInput(ITypedIngredient<I> ingredient) {
		return ingredient.matches(recipe.getIngredient());
	}

	@Override
	public <I> boolean isOutput(ITypedIngredient<I> ingredient) {
		return ingredient.matches(recipe.getIngredient());
	}
}
