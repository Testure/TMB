package turing.tmb.vanilla;

import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShaped;
import net.minecraft.core.item.ItemStack;
import turing.tmb.RecipeTranslator;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.ingredient.IIngredientTypeWithSubtypes;
import turing.tmb.api.ingredient.ITypedIngredient;

import java.util.stream.Stream;

public class ShapedCraftingRecipeTranslator extends RecipeTranslator<RecipeEntryCraftingShaped> {
	public ShapedCraftingRecipeTranslator(RecipeEntryCraftingShaped recipe) {
		super(recipe);
	}

	@Override
	public <T> boolean isValidInput(ITypedIngredient<T> ingredient) {
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			return Stream.of(recipe.getInput()).anyMatch((s) -> s != null && s.matches(ingredient.getCastIngredient(VanillaTypes.ITEM_STACK)));
		}
		if (ingredient.getType() instanceof IIngredientTypeWithSubtypes<?, T> type && type.getIngredientBaseClass() == ItemStack.class) {
			return Stream.of(recipe.getInput()).anyMatch((s) -> s != null && s.matches(ingredient.getBaseIngredient((IIngredientTypeWithSubtypes<ItemStack, T>) type)));
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
