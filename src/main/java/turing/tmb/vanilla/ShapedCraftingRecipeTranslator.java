package turing.tmb.vanilla;

import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShaped;
import turing.tmb.RecipeTranslator;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.ingredient.ITypedIngredient;

import java.util.stream.Stream;

public class ShapedCraftingRecipeTranslator extends RecipeTranslator<RecipeEntryCraftingShaped> {
	public ShapedCraftingRecipeTranslator(RecipeEntryCraftingShaped recipe) {
		super(recipe);
	}

	@Override
	public boolean isValidInput(ITypedIngredient<?> ingredient) {
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			return Stream.of(recipe.getInput()).anyMatch((s) -> s != null && s.matches(ingredient.getCastIngredient(VanillaTypes.ITEM_STACK)));
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
