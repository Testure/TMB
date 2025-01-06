package turing.tmb;

import turing.tmb.api.recipe.IRecipeLayout;
import turing.tmb.api.recipe.IRecipeSlot;

import java.util.ArrayList;
import java.util.List;

public class RecipeLayout implements IRecipeLayout {
	private final List<IRecipeSlot<?, ?>> slots;

	public RecipeLayout(List<IRecipeSlot<?, ?>> slots) {
		this.slots = slots;
	}

	@Override
	public List<IRecipeSlot<?, ?>> getSlots() {
		return slots;
	}
}
