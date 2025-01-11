package turing.tmb.api.recipe;

import net.minecraft.core.util.collection.Pair;
import turing.tmb.api.ingredient.ITypedIngredient;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface IRecipeIndex {
	List<IRecipeCategory<?>> getCategoriesForCatalyst(ITypedIngredient<?> ingredient);

	void registerCatalyst(IRecipeCategory<?> category, ITypedIngredient<?> catalyst);

	void hideCategory(String name);

	<T extends IRecipeCategory<?>> T registerCategory(T category);

	<R, T extends IRecipeTranslator<R>> void registerRecipes(IRecipeCategory<T> category, Collection<R> recipes, Function<R, T> conv);

	<R, T extends IRecipeTranslator<R>> void registerRecipe(IRecipeCategory<T> category, R recipe, Function<R, T> conv);

	List<IRecipeCategory<?>> getAllCategories();

	List<ITypedIngredient<?>> getCatalystsForCategory(IRecipeCategory<?> category);

	List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> getRecipesCatalyst(ITypedIngredient<?> ingredient);

	List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> getRecipesOutput(ITypedIngredient<?> ingredient);

	List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> getRecipesInput(ITypedIngredient<?> ingredient);
}
