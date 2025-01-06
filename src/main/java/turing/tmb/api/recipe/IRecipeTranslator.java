package turing.tmb.api.recipe;

import turing.tmb.api.ingredient.ITypedIngredient;

public interface IRecipeTranslator<T> {
	boolean isValidInput(ITypedIngredient<?> ingredient);

	boolean isOutput(ITypedIngredient<?> ingredient);

	T getOriginal();
}
