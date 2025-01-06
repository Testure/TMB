package turing.tmb.vanilla;

import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.item.ItemStack;
import turing.tmb.RecipeTranslator;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.ingredient.ITypedIngredient;

public class FurnaceRecipeTranslator<T extends RecipeEntryBase<RecipeSymbol, ItemStack, Void>> extends RecipeTranslator<T> {
	public FurnaceRecipeTranslator(T recipe) {
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
			return recipe.getOutput().isItemEqual(ingredient.getCastIngredient(VanillaTypes.ITEM_STACK));
		}
		return false;
	}
}
