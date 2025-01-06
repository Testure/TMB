package turing.tmb;

import turing.tmb.api.ingredient.IIngredientRegistry;
import turing.tmb.api.ingredient.IIngredientRenderer;
import turing.tmb.api.ingredient.IIngredientType;
import turing.tmb.api.ingredient.ITypedIngredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientRegistry<T> implements IIngredientRegistry<T> {
	protected final List<ITypedIngredient<T>> ingredients = new ArrayList<>();
	protected final IIngredientType<T> type;
	protected final IIngredientRenderer<T> renderer;

	protected IngredientRegistry(IIngredientType<T> type, IIngredientRenderer<T> renderer) {
		this.type = type;
		this.renderer = renderer;
	}

	@Override
	public void registerIngredient(String namespace, String name, T ingredient) {
		ingredients.add(new TypedIngredient<>(namespace, name, type, ingredient));
	}
}
