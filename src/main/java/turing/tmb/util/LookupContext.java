package turing.tmb.util;

import net.minecraft.core.item.ItemStack;
import turing.tmb.TypedIngredient;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.ILookupContext;
import turing.tmb.api.recipe.RecipeIngredientRole;

public class LookupContext implements ILookupContext {
	private RecipeIngredientRole role = RecipeIngredientRole.INPUT;
	private final ITypedIngredient<?> ingredient;

	public LookupContext(ITypedIngredient<?> ingredient) {
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			ItemStack stack = ingredient.getCastIngredient(VanillaTypes.ITEM_STACK);
			stack.stackSize = 1;
			ingredient = TypedIngredient.itemStackIngredient(stack);
		}
		this.ingredient = ingredient;
	}

	public LookupContext(ITypedIngredient<?> ingredient, RecipeIngredientRole role) {
		this(ingredient);
		this.role = role;
	}

	@Override
	public RecipeIngredientRole getRole() {
		return role;
	}

	@Override
	public ITypedIngredient<?> getIngredient() {
		return ingredient;
	}

	@Override
	public int hashCode() {
		return role.hashCode() + ingredient.hashCode();
	}
}
