package turing.tmb.api.recipe;

import turing.tmb.api.ingredient.ITypedIngredient;

public interface IRecipeTranslator<T> {
	<I> boolean isValidInput(ITypedIngredient<I> ingredient);

	<I> boolean isOutput(ITypedIngredient<I> ingredient);

	T getOriginal();
}
