package turing.tmb.api.recipe;

import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.ingredient.ITypedIngredient;

import java.util.List;

public interface IRecipeLayout {
	List<IRecipeSlot<?, ?>> getSlots();
}
