package turing.tmb;

import turing.tmb.api.ingredient.ITypedIngredient;

public class InfoRecipeTranslator extends RecipeTranslator<IngredientInfo> {
	public InfoRecipeTranslator(IngredientInfo recipe) {
		super(recipe);
	}

	@Override
	public boolean isValidInput(ITypedIngredient<?> ingredient) {
		return ingredient.hashCode() == recipe.getIngredient().hashCode();
	}

	@Override
	public boolean isOutput(ITypedIngredient<?> ingredient) {
		return ingredient.hashCode() == recipe.getIngredient().hashCode();
	}
}
