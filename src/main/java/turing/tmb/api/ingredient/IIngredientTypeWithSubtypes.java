package turing.tmb.api.ingredient;

public interface IIngredientTypeWithSubtypes<B, T> extends IIngredientType<T> {
	@Override
	Class<? extends T> getIngredientClass();

	Class<? extends B> getIngredientBaseClass();

	B getBase(T ingredient);

	default T getDefaultIngredient(B base) {
		throw new UnsupportedOperationException();
	}

	String getName(T ingredient);

	default void add(T ingredient, int amount) {

	}

	default boolean canAdd() {
		return false;
	}

	int getAmount(T ingredient);
}
