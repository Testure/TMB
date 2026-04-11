package turing.tmb.api;

import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import turing.tmb.api.recipe.IRecipeTranslator;

import java.util.List;

public interface ISupportsRecipeFilling {
	void fillRecipe(IRecipeTranslator<?> translator, boolean maximum);

	List<Class<? extends RecipeEntryBase<?, ?, ?>>> getSupportedRecipes();
}
