package turing.tmb.api;

import turing.tmb.api.drawable.gui.IGuiHelper;
import turing.tmb.api.runtime.ITMBRuntime;

public interface ITMBPlugin {
	default void registerIngredientTypes(ITMBRuntime runtime) {

	}

	default void registerIngredients(ITMBRuntime runtime) {

	}

	default void registerExtraScreens(IGuiHelper helper) {

	}

	default void registerRecipeCatalysts(ITMBRuntime runtime) {

	}

	default void registerRecipeCategories(ITMBRuntime runtime) {

	}

	default void registerRecipes(ITMBRuntime runtime) {

	}
}
