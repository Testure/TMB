package turing.tmb.api;

import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import turing.tmb.TypedIngredient;
import turing.tmb.api.ingredient.IIngredientType;
import turing.tmb.api.ingredient.IIngredientTypeWithSubtypes;

public final class VanillaTypes {
	public static final IIngredientTypeWithSubtypes<Item, ItemStack> ITEM_STACK = new IIngredientTypeWithSubtypes<Item, ItemStack>() {
		@Override
		public String getUid() {
			return "item_stack";
		}

		@Override
		public Class<? extends ItemStack> getIngredientClass() {
			return ItemStack.class;
		}

		@Override
		public Class<? extends Item> getIngredientBaseClass() {
			return Item.class;
		}

		@Override
		public Item getBase(ItemStack ingredient) {
			return ingredient.getItem();
		}

		@Override
		public ItemStack getDefaultIngredient(Item base) {
			return base.getDefaultStack();
		}

		@Override
		public String getName(ItemStack ingredient) {
			return ingredient.getDisplayName();
		}

		@Override
		public void add(ItemStack ingredient, int amount) {
			ingredient.stackSize += amount;
		}

		@Override
		public int getAmount(ItemStack ingredient) {
			return ingredient.stackSize;
		}

		@Override
		public boolean matches(ItemStack ingredient, Object otherIngredient) {
			if(otherIngredient instanceof TypedIngredient<?>) throw new IllegalArgumentException("Received TypedIngredient instead of actual ingredient class, use .getIngredient() when calling this method.");
			if(!(otherIngredient instanceof ItemStack)) return false;
			return ingredient.isItemEqual((ItemStack) otherIngredient);
		}
	};

	public static final IIngredientType<WeightedRandomLootObject> LOOT_OBJECT = new IIngredientType<WeightedRandomLootObject>() {
		@Override
		public Class<? extends WeightedRandomLootObject> getIngredientClass() {
			return WeightedRandomLootObject.class;
		}

		@Override
		public String getUid() {
			return "loot_object";
		}
	};

	private VanillaTypes() {

	}
}
