package turing.tmb.api.runtime;

import turing.tmb.api.drawable.gui.IGuiHelper;
import turing.tmb.api.ingredient.IIngredientRegistry;
import turing.tmb.api.ingredient.IIngredientType;
import turing.tmb.api.ingredient.IIngredientTypeRegistry;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.ILookupContext;
import turing.tmb.api.recipe.IRecipeIndex;
import turing.tmb.api.recipe.RecipeIngredientRole;

public interface ITMBRuntime {
	IIngredientTypeRegistry getIngredientTypeRegistry();

	<T> IIngredientRegistry<T> getRegistryForIngredientType(IIngredientType<T> type);

	IIngredientIndex getIngredientIndex();

	IGuiHelper getGuiHelper();

	IRecipeIndex getRecipeIndex();

	void showRecipe(ILookupContext lookup);

	void showRecipe(ITypedIngredient<?> ingredient, RecipeIngredientRole role);

	void showAllRecipes();
}
