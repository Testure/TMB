package turing.tmb;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTooltip;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.collection.Pair;
import org.jetbrains.annotations.Nullable;
import turing.tmb.api.ingredient.IIngredientRegistry;
import turing.tmb.api.ingredient.IIngredientType;
import turing.tmb.api.ingredient.IIngredientTypeRegistry;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.ILookupContext;
import turing.tmb.api.recipe.IRecipeCategory;
import turing.tmb.api.recipe.IRecipeTranslator;
import turing.tmb.api.recipe.RecipeIngredientRole;
import turing.tmb.api.runtime.IIngredientIndex;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.client.ScreenTMBRecipe;
import turing.tmb.util.GuiHelper;
import turing.tmb.util.LookupContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TMBRuntime implements ITMBRuntime {
	protected final IngredientTypeRegistry ingredientTypeRegistry = new IngredientTypeRegistry(this);
	protected final Map<IIngredientType<?>, IngredientRegistry<?>> ingredientRegistries = new HashMap<>();
	protected final IngredientIndex index = new IngredientIndex(this);
	protected final RecipeIndex recipeIndex = new RecipeIndex(this);
	protected static final GuiTooltip tooltipElement = new GuiTooltip(Minecraft.getMinecraft(TMBRuntime.class));
	protected final GuiHelper guiHelper = new GuiHelper(this);
	protected boolean isReady;

	protected TMBRuntime() {

	}

	@Override
	public void showRecipe(ILookupContext lookup) {
		List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> results = recipeIndex.searchRecipes(lookup);
		if (lookup.getRole() == RecipeIngredientRole.INPUT) {
			List<IRecipeCategory<?>> categories = recipeIndex.getCategoriesForCatalyst(lookup.getIngredient());
			for (IRecipeCategory<?> category : categories) {
				if (results.isEmpty() || results.stream().noneMatch(pair -> pair.getLeft().hashCode() == category.hashCode())) {
					results.add(Pair.of(category, null));
				}
			}
		}
		if (!results.isEmpty()) {
			ScreenTMBRecipe.show(results, lookup, null);
		}
	}

	@Override
	public void showAllRecipes() {
		List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> recipes = new ArrayList<>();
		for (Map.Entry<IRecipeCategory<?>, List<IRecipeTranslator<?>>> entry : recipeIndex.recipeLists.entrySet()) {
			for (IRecipeTranslator<?> translator : entry.getValue()) {
				recipes.add(Pair.of(entry.getKey(), translator));
			}
		}
		ScreenTMBRecipe.show(recipes, null, null);
	}

	@Override
	public void showRecipe(ITypedIngredient<?> ingredient, RecipeIngredientRole role) {
		showRecipe(new LookupContext(ingredient, role));
	}

	@Override
	public IIngredientTypeRegistry getIngredientTypeRegistry() {
		return ingredientTypeRegistry;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> IIngredientRegistry<T> getRegistryForIngredientType(IIngredientType<T> type) {
		IIngredientRegistry<T> registry = (IIngredientRegistry<T>) ingredientRegistries.get(type);
		if (registry == null) {
			throw new NullPointerException("Could not find registry for IngredientType " + type.toString());
		}
		return registry;
	}

	@Override
	public IIngredientIndex getIngredientIndex() {
		return index;
	}

	@Override
	public GuiHelper getGuiHelper() {
		return guiHelper;
	}

	@Override
	public RecipeIndex getRecipeIndex() {
		return recipeIndex;
	}

	public static String getTooltipText(ItemStack stack, boolean showDescription) {
		return tooltipElement.getTooltipText(stack, showDescription);
	}
}
