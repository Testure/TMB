package turing.tmb.api.runtime;

import turing.tmb.api.ISearchQuery;
import turing.tmb.api.ingredient.ITypedIngredient;

import java.util.Collection;

public interface IIngredientIndex {
	Collection<ITypedIngredient<?>> getAllIngredients();

	Collection<ITypedIngredient<?>> getAllVisibleIngredients();

	Collection<ITypedIngredient<?>> getFilteredIngredients(ISearchQuery query);

	<T> void hideIngredient(T ingredient);

	<T> void unhideIngredient(T ingredient);
}
