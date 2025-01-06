package turing.tmb;

import turing.tmb.api.ingredient.IIngredientType;
import turing.tmb.api.recipe.IRecipeSlot;
import turing.tmb.api.recipe.RecipeIngredientRole;
import turing.tmb.api.recipe.SlotTooltipFunction;

import java.util.ArrayList;
import java.util.List;

public class RecipeLayoutBuilder {
	private final List<IRecipeSlot<?, ?>> slots = new ArrayList<>();

	public RecipeLayoutBuilder addSlot(int index, IRecipeSlot<?, ?> slot) {
		this.slots.add(index, slot);
		return this;
	}

	public <I, T extends IIngredientType<I>> SlotBuilder<I, T> addSlot(int index, T type, RecipeIngredientRole role) {
		return new SlotBuilder<>(this, index, type).setRole(role);
	}

	public <I, T extends IIngredientType<I>> SlotBuilder<I, T> addInputSlot(int index, T type) {
		return addSlot(index, type, RecipeIngredientRole.INPUT);
	}

	public <I, T extends IIngredientType<I>> SlotBuilder<I, T> addOutputSlot(int index, T type) {
		return addSlot(index, type, RecipeIngredientRole.OUTPUT);
	}

	public RecipeLayout build() {
		return new RecipeLayout(slots);
	}

	public static class SlotBuilder<I, T extends IIngredientType<I>> {
		private final T type;
		private int x;
		private int y;
		private RecipeIngredientRole role = RecipeIngredientRole.INPUT;
		private final RecipeLayoutBuilder builder;
		private final int index;
		private SlotTooltipFunction<I> tooltipFunction;

		public SlotBuilder(RecipeLayoutBuilder builder, int index, T type) {
			this.type = type;
			this.builder = builder;
			this.index = index;
		}

		public SlotBuilder<I, T> setPosition(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public SlotBuilder<I, T> setRole(RecipeIngredientRole role) {
			this.role = role;
			return this;
		}

		public SlotBuilder<I, T> setTooltipHandler(SlotTooltipFunction<I> tooltipFunction) {
			this.tooltipFunction = tooltipFunction;
			return this;
		}

		public RecipeLayoutBuilder build() {
			RecipeSlot<I, T> slot = new RecipeSlot<>(type, role);
			slot.setPosition(x, y);
			slot.setTooltipFunction(tooltipFunction);
			return builder.addSlot(index, slot);
		}
	}
}
