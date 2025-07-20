package turing.tmb.api.recipe;

import org.jetbrains.annotations.Nullable;
import turing.tmb.api.drawable.IDrawable;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.ingredient.IIngredientType;
import turing.tmb.api.runtime.ITMBRuntime;

import java.util.Collections;
import java.util.List;

public interface IRecipeCategory<R extends IRecipeTranslator<?>> {
	@Nullable
	default IDrawable getIcon() {
		return null;
	}

	String getName();

	String getNamespace();

	IDrawable getBackground();

	void drawRecipe(ITMBRuntime runtime, R recipe, IRecipeLayout layout, List<IIngredientList> ingredients, ILookupContext context);

	void getIngredients(R recipe, IRecipeLayout layout, ILookupContext context, List<IIngredientList> ingredients);

	IRecipeLayout getRecipeLayout();

	default <I, T extends IIngredientType<I>> List<String>  getTooltips(R recipe, IRecipeSlot<I, T> slot, int mouseX, int mouseY) {
		return Collections.emptyList();
	}
}
