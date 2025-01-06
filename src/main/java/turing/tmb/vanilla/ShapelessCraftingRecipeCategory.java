package turing.tmb.vanilla;

import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShapeless;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.ILookupContext;
import turing.tmb.api.recipe.IRecipeLayout;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.util.IngredientList;

import java.util.List;

public class ShapelessCraftingRecipeCategory extends AbstractCraftingRecipeCategory<RecipeEntryCraftingShapeless, ShapelessCraftingRecipeTranslator> {
	@Override
	void addInputs(ITMBRuntime runtime, ShapelessCraftingRecipeTranslator recipe, IRecipeLayout layout, List<IIngredientList> ingredients, ILookupContext context) {
		if (recipe.getOriginal().getInput().size() > 4) {
			for (int i = 0; i < Math.max(recipe.getOriginal().getInput().size(), 9); i++) {
				IIngredientList list;
				if (i >= recipe.getOriginal().getInput().size()) {
					list = new IngredientList();
				} else {
					list = IngredientList.fromRecipeSymbol(recipe.getOriginal().getInput().get(i));
				}
				list.getIngredients().forEach(ingredient -> ingredient.getItemStack().ifPresent((stack) -> stack.stackSize = 1));
				ingredients.add(i + 1, list);
			}
		} else if (recipe.getOriginal().getInput().size() > 1) {
			for (int i = 0; i < Math.max(recipe.getOriginal().getInput().size(), 4); i++) {
				IIngredientList list;
				if (i >= recipe.getOriginal().getInput().size()) {
					list = new IngredientList();
				} else {
					list = IngredientList.fromRecipeSymbol(recipe.getOriginal().getInput().get(i));
				}
				if (i == 2) {
					ingredients.add(i + 1, new IngredientList());
				}
				list.getIngredients().forEach(ingredient -> ingredient.getItemStack().ifPresent((stack) -> stack.stackSize = 1));
				ingredients.add(i + (i == 2 ? 2 : 1), list);
			}
		} else {
			for (int i = 0; i < 4; i++) {
				ingredients.add(i + 1, new IngredientList());
			}
			ingredients.add(5, IngredientList.fromRecipeSymbol(recipe.getOriginal().getInput().get(0)));
		}
	}

	@Override
	public String getName() {
		return "tmb.category.shapelessCrafting";
	}
}
