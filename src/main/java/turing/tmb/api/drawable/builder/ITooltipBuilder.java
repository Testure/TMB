package turing.tmb.api.drawable.builder;

import turing.tmb.api.ingredient.ITypedIngredient;

import java.util.Collection;

public interface ITooltipBuilder {
	void add(String text);

	void addAll(Collection<String> text);

	void setIngredient(ITypedIngredient<?> ingredient);

	void clear();
}
