package turing.tmb.vanilla;

import net.minecraft.core.data.registry.recipe.entry.RecipeEntryTrommel;
import turing.tmb.RecipeTranslator;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.ingredient.ITypedIngredient;

public class TrommelRecipeTranslator extends RecipeTranslator<RecipeEntryTrommel> {
	public TrommelRecipeTranslator(RecipeEntryTrommel recipe) {
		super(recipe);
	}

	@Override
	public boolean isValidInput(ITypedIngredient<?> ingredient) {
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			return recipe.getInput().matches(ingredient.getCastIngredient(VanillaTypes.ITEM_STACK));
		}
		return false;
	}

	@Override
	public boolean isOutput(ITypedIngredient<?> ingredient) {
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			return recipe.getOutput().getEntries().stream().anyMatch((w) -> w.getDefinedItemStack().isItemEqual(ingredient.getCastIngredient(VanillaTypes.ITEM_STACK)));
		}
		return false;
	}
}
