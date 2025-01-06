package turing.tmb.api.ingredient;

public interface IIngredientRegistry<T> {
	void registerIngredient(String namespace, String name, T ingredient);
}
