package turing.tmb.api.ingredient;

import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import turing.tmb.api.VanillaTypes;

import java.util.Optional;

@ApiStatus.NonExtendable
public interface ITypedIngredient<T> {
	IIngredientType<T> getType();

	T getIngredient();

	String getNamespace();

	String getUid();

	String getName();

	void addAmount(int amount);

	int getAmount();

	boolean matches(Object ingredient);

	default <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
		return ingredientType.castIngredient(getIngredient());
	}

	@Nullable
	default <V> V getCastIngredient(IIngredientType<V> ingredientType) {
		return ingredientType.getCastIngredient(getIngredient());
	}

	@SuppressWarnings("unchecked")
	@Nullable
	default <V> ITypedIngredient<V> cast(IIngredientType<V> ingredientType) {
		if (getType().equals(ingredientType)) {
			return (ITypedIngredient<V>) this;
		}
		return null;
	}

	default <B> B getBaseIngredient(IIngredientTypeWithSubtypes<B, T> ingredientType) {
		return ingredientType.getBase(getIngredient());
	}

	default Optional<ItemStack> getItemStack() {
		return getIngredient(VanillaTypes.ITEM_STACK);
	}
}
