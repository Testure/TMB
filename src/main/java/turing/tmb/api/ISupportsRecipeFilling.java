package turing.tmb.api;

import turing.tmb.api.recipe.IRecipeTranslator;

public interface ISupportsRecipeFilling {
	void fillRecipe(IRecipeTranslator<?> recipe);
}
