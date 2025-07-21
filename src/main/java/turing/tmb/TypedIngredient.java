package turing.tmb;

import net.minecraft.core.item.ItemStack;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.ingredient.IIngredientType;
import turing.tmb.api.ingredient.IIngredientTypeWithSubtypes;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.util.ModIDHelper;

public class TypedIngredient<T> implements ITypedIngredient<T> {
	protected final IIngredientType<T> type;
	protected final T ingredient;
	public final String namespace;
	public final String name;

	public TypedIngredient(String namespace, String name, IIngredientType<T> type, T ingredient) {
		this.type = type;
		this.ingredient = ingredient;
		this.namespace = namespace;
		this.name = name;
	}

	public static TypedIngredient<ItemStack> itemStackIngredient(ItemStack stack) {
		return new TypedIngredient<>(ModIDHelper.getModIDForItem(stack), stack.getDisplayName(), VanillaTypes.ITEM_STACK, stack.copy());
	}

	@Override
	public IIngredientType<T> getType() {
		return type;
	}

	@Override
	public T getIngredient() {
		return ingredient;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getUid() {
		return name;
	}

	@Override
	public String getName() {
		if(getType() instanceof IIngredientTypeWithSubtypes){
			return ((IIngredientTypeWithSubtypes<?, T>) getType()).getName(ingredient);
		}
		return "";
	}

	@Override
	public void addAmount(int amount) {
		if(getType() instanceof IIngredientTypeWithSubtypes){
			((IIngredientTypeWithSubtypes<?, T>) getType()).add(ingredient, amount);
		}
	}

	@Override
	public int getAmount() {
		if(getType() instanceof IIngredientTypeWithSubtypes){
			return ((IIngredientTypeWithSubtypes<?, T>) getType()).getAmount(ingredient);
		}
		return 1;
	}

	public boolean matches(Object ingredient){
		if(getType() instanceof IIngredientTypeWithSubtypes){
			return ((IIngredientTypeWithSubtypes<?, T>) getType()).matches(this.ingredient, ingredient);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getUid().hashCode() + getNamespace().hashCode();
	}
}
