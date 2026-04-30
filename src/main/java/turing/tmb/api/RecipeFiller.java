package turing.tmb.api;

import net.minecraft.client.gui.Screen;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import turing.tmb.api.recipe.IRecipeTranslator;

import java.util.List;

public interface RecipeFiller<R,S extends Screen> {
	default void fillRecipe(IRecipeTranslator<R> translator, boolean maximum) {

	}

	default void fillRecipe(IRecipeTranslator<R> translator, S screen, boolean maximum) {
		fillRecipe(translator, maximum);
	}

	List<Class<? extends RecipeEntryBase<?, ?, ?>>> getSupportedRecipes();
}
