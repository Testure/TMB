package turing.tmb.util;

import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import turing.tmb.TypedIngredient;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.ingredient.ITypedIngredient;

import java.util.*;
import java.util.stream.Collectors;

public class IngredientList implements IIngredientList {
	private final List<ITypedIngredient<?>> list = new ArrayList<>();
	public String itemGroup;

	public IngredientList() {

	}

	public IngredientList(ITypedIngredient<?>... ingredients) {
		add(ingredients);
	}

	public IngredientList(Collection<ITypedIngredient<?>> ingredients) {
		list.addAll(ingredients);
	}

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public List<ITypedIngredient<?>> getIngredients() {
		return Collections.unmodifiableList(list);
	}

	@Override
	public void add(ITypedIngredient<?>... ingredients) {
		list.addAll(Arrays.asList(ingredients));
	}

	public static IngredientList fromRecipeSymbol(RecipeSymbol symbol) {
		if (symbol == null) {
			return new IngredientList();
		}
		List<ITypedIngredient<?>> ingredients = symbol.resolve().stream().map(TypedIngredient::itemStackIngredient).collect(Collectors.toList());
		IngredientList ingredientList = new IngredientList(ingredients);

		if (symbol.getItemGroup() != null && !symbol.getItemGroup().isEmpty()) {
			ingredientList.itemGroup = symbol.getItemGroup();
		}

		return ingredientList;
	}
}
