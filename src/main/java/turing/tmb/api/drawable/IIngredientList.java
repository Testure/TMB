package turing.tmb.api.drawable;

import turing.tmb.api.ingredient.ITypedIngredient;

import java.util.List;

public interface IIngredientList {
	int getSize();

	List<ITypedIngredient<?>> getIngredients();

	void add(ITypedIngredient<?>... ingredients);
}
