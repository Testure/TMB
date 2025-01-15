package turing.tmb.vanilla;

import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShaped;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.recipe.ILookupContext;
import turing.tmb.api.recipe.IRecipeLayout;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.util.IngredientList;

import java.util.List;

public class ShapedCraftingRecipeCategory extends AbstractCraftingRecipeCategory<RecipeEntryCraftingShaped, ShapedCraftingRecipeTranslator> {
	@Override
	void addInputs(ITMBRuntime runtime, ShapedCraftingRecipeTranslator recipe, IRecipeLayout layout, List<IIngredientList> ingredients, ILookupContext context) {
		if (recipe.getOriginal().recipeHeight <= 2 && recipe.getOriginal().recipeWidth <= 2) {
			for (int i = 0; i < 4; i++) {
				int slotX = i % 2;
				int slotY = i / 2;
				IIngredientList list;
				if (slotX < recipe.getOriginal().recipeWidth && slotY < recipe.getOriginal().recipeHeight) {
					list = IngredientList.fromRecipeSymbol(recipe.getOriginal().getInput()[slotX + slotY * recipe.getOriginal().recipeWidth]);
				} else {
					list = new IngredientList();
				}
				int add = 1;
				if (((recipe.getOriginal().getInput().length > 2 && recipe.getOriginal().getInput()[3] != null) || recipe.getOriginal().recipeWidth == 1) && i == 2) {
					ingredients.add((slotX + slotY * recipe.getOriginal().recipeWidth) + 1, new IngredientList());
					add++;
				}
				ingredients.add((slotX + slotY * recipe.getOriginal().recipeWidth) + add, list);
			}
		} else {
			for (int i = 0; i < 9; i++) {
				int slotX = i % 3;
				int slotY = i / 3;
				IIngredientList list;
				if (slotX < recipe.getOriginal().recipeWidth && slotY < recipe.getOriginal().recipeHeight) {
					list = IngredientList.fromRecipeSymbol(recipe.getOriginal().getInput()[slotX + slotY * recipe.getOriginal().recipeWidth]);
				} else {
					list = new IngredientList();
				}
				ingredients.add(i + 1, list);
			}
		}
	}

	@Override
	public String getName() {
		return "tmb.category.shapedCrafting";
	}
}
