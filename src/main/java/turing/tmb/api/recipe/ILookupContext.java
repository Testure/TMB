package turing.tmb.api.recipe;

import turing.tmb.api.ingredient.ITypedIngredient;

public interface ILookupContext {
	RecipeIngredientRole getRole();

	ITypedIngredient<?> getIngredient();
}
