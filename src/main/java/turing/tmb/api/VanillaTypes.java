package turing.tmb.api;

import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
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
