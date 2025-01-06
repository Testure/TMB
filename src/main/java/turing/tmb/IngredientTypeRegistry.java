package turing.tmb;

import turing.tmb.api.ingredient.IIngredientRenderer;
import turing.tmb.api.ingredient.IIngredientType;
import turing.tmb.api.ingredient.IIngredientTypeRegistry;

import java.util.ArrayList;
import java.util.List;

public class IngredientTypeRegistry implements IIngredientTypeRegistry {
	public static final List<IIngredientType<?>> INGREDIENT_TYPES = new ArrayList<>();
	private final TMBRuntime runtime;

	public IngredientTypeRegistry(TMBRuntime runtime) {
		this.runtime = runtime;
	}

	protected void clear() {
		INGREDIENT_TYPES.clear();
	}

	@Override
	public <T> void registerIngredientType(IIngredientType<T> ingredientType, IIngredientRenderer<T> renderer) {
		INGREDIENT_TYPES.add(ingredientType);
		runtime.ingredientRegistries.put(ingredientType, new IngredientRegistry<>(ingredientType, renderer));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> IIngredientRenderer<T> getRenderer(IIngredientType<T> ingredientType) {
		return (IIngredientRenderer<T>) runtime.ingredientRegistries.get(ingredientType).renderer;
	}
}
