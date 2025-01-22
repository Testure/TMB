package turing.tmb.vanilla;

import net.minecraft.client.gui.guidebook.mobs.MobInfoRegistry;
import turing.tmb.RecipeTranslator;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.ingredient.ITypedIngredient;

public class MobInfoRecipeTranslator extends RecipeTranslator<MobInfoRegistry.MobInfo> {
	public MobInfoRecipeTranslator(MobInfoRegistry.MobInfo recipe) {
		super(recipe);
	}

	@Override
	public boolean isValidInput(ITypedIngredient<?> ingredient) {
		return false;
	}

	@Override
	public boolean isOutput(ITypedIngredient<?> ingredient) {
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			if (recipe.getDrops() != null) {
				for (MobInfoRegistry.MobDrop drop : recipe.getDrops()) {
					if (ingredient.getCastIngredient(VanillaTypes.ITEM_STACK).isItemEqual(drop.getStack())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
