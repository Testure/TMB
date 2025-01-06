package turing.tmb.api.ingredient;

import turing.tmb.api.runtime.ITMBRuntime;

import javax.annotation.Nullable;
import java.util.Optional;

@FunctionalInterface
public interface IIngredientType<T> {
	Class<? extends T> getIngredientClass();

	@SuppressWarnings("unchecked")
	default IIngredientRenderer<T> getRenderer(ITMBRuntime runtime) {
		IIngredientRenderer<T> render = runtime.getIngredientTypeRegistry().getRenderer(this);
		return render != null ? render : (IIngredientRenderer<T>) IIngredientRenderer.EMPTY;
	}

	default String getUid() {
		Class<? extends T> ingredientClass = getIngredientClass();
		return ingredientClass.getName();
	}

	default Optional<T> castIngredient(@Nullable Object ingredient) {
		Class<? extends T> ingredientClass = getIngredientClass();
		if (ingredientClass.isInstance(ingredient)) {
			return Optional.of(ingredientClass.cast(ingredient));
		}
		return Optional.empty();
	}

	@Nullable
	default T getCastIngredient(@Nullable Object ingredient) {
		Class<? extends T> ingredientClass = getIngredientClass();
		if (ingredientClass.isInstance(ingredient)) {
			return ingredientClass.cast(ingredient);
		}
		return null;
	}
}
