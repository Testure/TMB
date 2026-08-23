package turing.tmb.api;

import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import turing.tmb.TypedIngredient;
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
		public boolean canAdd() {
			return true;
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

	public static final IIngredientTypeWithSubtypes<ItemStack, WeightedRandomLootObject> LOOT_OBJECT = new IIngredientTypeWithSubtypes<ItemStack, WeightedRandomLootObject>() {
		@Override
		public Class<? extends WeightedRandomLootObject> getIngredientClass() {
			return WeightedRandomLootObject.class;
		}

		@Override
		public Class<? extends ItemStack> getIngredientBaseClass() {
			return ItemStack.class;
		}

		@Override
		public String getName(WeightedRandomLootObject ingredient) {
			ItemStack definedStack = ingredient.getDefinedItemStack();
			return definedStack != null ? definedStack.getDisplayName() : "Empty";
		}

		@Override
		public int getAmount(WeightedRandomLootObject ingredient) {
			return ingredient.isRandomYield() ? ingredient.getMaxYield() : ingredient.getFixedYield();
		}

		@Override
		public ItemStack getBase(WeightedRandomLootObject ingredient) {
			return ingredient.getDefinedItemStack();
		}

		@Override
		public boolean matches(WeightedRandomLootObject ingredient, Object otherIngredient) {
			if (!(otherIngredient instanceof ItemStack) && !(otherIngredient instanceof WeightedRandomLootObject)) return false;
			if (otherIngredient instanceof ItemStack stack) return ingredient.getDefinedItemStack() != null && ingredient.getDefinedItemStack().isItemStackEqual(stack);
			if (otherIngredient instanceof WeightedRandomLootObject lootObject) {
				if (ingredient.getDefinedItemStack() != null) {
					if (lootObject.getDefinedItemStack() == null) return false;
					if (!ingredient.getDefinedItemStack().isItemStackEqual(lootObject.getDefinedItemStack())) return false;
				} else if (lootObject.getDefinedItemStack() != null) return false;
				if (ingredient.isRandomYield() != lootObject.isRandomYield()) return false;
				if (ingredient.isRandomMeta() != lootObject.isRandomMeta()) return false;
				if (ingredient.getFixedMeta() != lootObject.getFixedMeta()) return false;
				if (ingredient.getFixedYield() != lootObject.getFixedYield()) return false;
				if (ingredient.getMinMeta() != lootObject.getMinMeta()) return false;
				if (ingredient.getMaxMeta() != lootObject.getMaxMeta()) return false;
				if (ingredient.getMinYield() != lootObject.getMinYield()) return false;
				if (ingredient.getMaxYield() != lootObject.getMaxYield()) return false;
			}
			return true;
		}

		@Override
		public String getUid() {
			return "loot_object";
		}
	};

	private VanillaTypes() {

	}
}
