package turing.tmb.vanilla;

import net.minecraft.core.data.registry.recipe.entry.RecipeEntryTrommel;
import net.minecraft.core.item.ItemStack;
import turing.tmb.RecipeTranslator;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.ingredient.IIngredientTypeWithSubtypes;
import turing.tmb.api.ingredient.ITypedIngredient;

public class TrommelRecipeTranslator extends RecipeTranslator<RecipeEntryTrommel> {
	public TrommelRecipeTranslator(RecipeEntryTrommel recipe) {
		super(recipe);
	}

	@Override
	public <I> boolean isValidInput(ITypedIngredient<I> ingredient) {
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			return recipe.getInput().matches(ingredient.getCastIngredient(VanillaTypes.ITEM_STACK));
		}
		if (ingredient.getType() instanceof IIngredientTypeWithSubtypes<?, I> type && type.getIngredientBaseClass() == ItemStack.class) {
			return recipe.getInput().matches(ingredient.getBaseIngredient((IIngredientTypeWithSubtypes<ItemStack, I>) type));
		}
		return false;
	}

	@Override
	public <I> boolean isOutput(ITypedIngredient<I> ingredient) {
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			return recipe.getOutput().getEntries().stream().anyMatch((w) -> w.getDefinedItemStack().isItemEqual(ingredient.getCastIngredient(VanillaTypes.ITEM_STACK)));
		}
		if (ingredient.getType() instanceof IIngredientTypeWithSubtypes<?, I> type && type.getIngredientBaseClass() == ItemStack.class) {
			return recipe.getOutput().getEntries().stream().anyMatch((w) -> w.getDefinedItemStack().isItemEqual(ingredient.getBaseIngredient((IIngredientTypeWithSubtypes<ItemStack, I>) type)));
		}
		return false;
	}
}
