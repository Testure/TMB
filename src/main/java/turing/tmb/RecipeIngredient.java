package turing.tmb;

import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.IRecipeCategory;
import turing.tmb.api.recipe.IRecipeTranslator;
import turing.tmb.api.recipe.RecipeIngredientRole;

public class RecipeIngredient {
	public final ITypedIngredient<?> ingredient;
	public final IRecipeTranslator<?> recipe;
	public final IRecipeCategory<? super IRecipeTranslator<?>> category;
	public final RecipeIngredientRole role;

	public RecipeIngredient(ITypedIngredient<?> ingredient, IRecipeTranslator<?> recipe, IRecipeCategory<?> category, RecipeIngredientRole role) {
		this.ingredient = ingredient;
		this.recipe = recipe;
		this.category = (IRecipeCategory<? super IRecipeTranslator<?>>) category;
		this.role = role;
	}
}
