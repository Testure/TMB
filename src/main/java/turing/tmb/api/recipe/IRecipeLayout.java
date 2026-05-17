package turing.tmb.api.recipe;

import java.util.List;

public interface IRecipeLayout {
	List<IRecipeSlot<?, ?>> getSlots();
}
