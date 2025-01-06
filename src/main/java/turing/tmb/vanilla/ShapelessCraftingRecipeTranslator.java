package turing.tmb.vanilla;

import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShapeless;
import turing.tmb.RecipeTranslator;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.ingredient.ITypedIngredient;

public class ShapelessCraftingRecipeTranslator extends RecipeTranslator<RecipeEntryCraftingShapeless> {
	public ShapelessCraftingRecipeTranslator(RecipeEntryCraftingShapeless recipe) {
		super(recipe);
	}

	@Override
	public boolean isValidInput(ITypedIngredient<?> ingredient) {
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			return recipe.getInput().stream().anyMatch((s) -> s.matches(ingredient.getCastIngredient(VanillaTypes.ITEM_STACK)));
		}
		return false;
	}

	@Override
	public boolean isOutput(ITypedIngredient<?> ingredient) {
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			return recipe.getOutput().isItemEqual(ingredient.getCastIngredient(VanillaTypes.ITEM_STACK));
		}
		return false;
	}
}
