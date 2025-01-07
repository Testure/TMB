package turing.tmb.util;

import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.ILookupContext;
import turing.tmb.api.recipe.RecipeIngredientRole;

public class LookupContext implements ILookupContext {
	private RecipeIngredientRole role = RecipeIngredientRole.INPUT;
	private final ITypedIngredient<?> ingredient;

	public LookupContext(ITypedIngredient<?> ingredient) {
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
