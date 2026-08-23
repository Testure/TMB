package turing.tmb.vanilla;

import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShapeless;
import net.minecraft.core.item.ItemStack;
import turing.tmb.RecipeTranslator;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.ingredient.IIngredientTypeWithSubtypes;
import turing.tmb.api.ingredient.ITypedIngredient;

public class ShapelessCraftingRecipeTranslator extends RecipeTranslator<RecipeEntryCraftingShapeless> {
	public ShapelessCraftingRecipeTranslator(RecipeEntryCraftingShapeless recipe) {
		super(recipe);
	}

	@Override
	public <T> boolean isValidInput(ITypedIngredient<T> ingredient) {
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			return recipe.getInput().stream().anyMatch((s) -> s.matches(ingredient.getCastIngredient(VanillaTypes.ITEM_STACK)));
		}
		if (ingredient.getType() instanceof IIngredientTypeWithSubtypes<?, T> type && type.getIngredientBaseClass() == ItemStack.class) {
			return recipe.getInput().stream().anyMatch((s) -> s != null && s.matches(ingredient.getBaseIngredient((IIngredientTypeWithSubtypes<ItemStack, T>) type)));
		}
		return false;
	}

	@Override
	public <T> boolean isOutput(ITypedIngredient<T> ingredient) {
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			return recipe.getOutput().isItemEqual(ingredient.getCastIngredient(VanillaTypes.ITEM_STACK));
		}
		if (ingredient.getType() instanceof IIngredientTypeWithSubtypes<?, T> type && type.getIngredientBaseClass() == ItemStack.class) {
			return recipe.getOutput().isItemEqual(ingredient.getBaseIngredient((IIngredientTypeWithSubtypes<ItemStack, T>) type));
		}
		return false;
	}
}
