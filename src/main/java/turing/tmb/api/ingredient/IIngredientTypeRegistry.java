package turing.tmb.api.ingredient;

public interface IIngredientTypeRegistry {
	<T> void registerIngredientType(IIngredientType<T> ingredientType, IIngredientRenderer<T> renderer);

	<T> IIngredientRenderer<T> getRenderer(IIngredientType<T> ingredientType);
}
