package turing.tmb.vanilla;

import net.minecraft.client.gui.guidebook.mobs.MobInfoRegistry;
import net.minecraft.core.item.ItemStack;
import turing.tmb.RecipeTranslator;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.ingredient.IIngredientTypeWithSubtypes;
import turing.tmb.api.ingredient.ITypedIngredient;

public class MobInfoRecipeTranslator extends RecipeTranslator<MobInfoRegistry.MobInfo> {
	public MobInfoRecipeTranslator(MobInfoRegistry.MobInfo recipe) {
		super(recipe);
	}

	@Override
	public <I> boolean isValidInput(ITypedIngredient<I> ingredient) {
		return false;
	}

	@Override
	public <I> boolean isOutput(ITypedIngredient<I> ingredient) {
		ItemStack stack = null;
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			stack = ingredient.getCastIngredient(VanillaTypes.ITEM_STACK);
		}
		if (ingredient.getType() instanceof IIngredientTypeWithSubtypes<?, I> type && type.getIngredientBaseClass() == ItemStack.class) {
			stack = ingredient.getBaseIngredient((IIngredientTypeWithSubtypes<ItemStack, I>) type);
		}
		if (recipe.getDrops() != null && stack != null) {
			for (MobInfoRegistry.MobDrop drop : recipe.getDrops()) {
				if (stack.isItemEqual(drop.getStack())) {
					return true;
				}
			}
		}
		return false;
	}
}
