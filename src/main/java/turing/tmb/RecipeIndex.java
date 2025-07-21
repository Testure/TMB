package turing.tmb;

import net.minecraft.core.util.collection.Pair;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.*;
import turing.tmb.util.LookupContext;

import java.util.*;
import java.util.function.Function;

public class RecipeIndex implements IRecipeIndex {
	private final TMBRuntime runtime;
	protected final List<IRecipeCategory<?>> categories = new ArrayList<>();
	protected final List<String> hiddenCategories = new ArrayList<>();
	protected final Map<IRecipeCategory<?>, List<ITypedIngredient<?>>> catalysts = new HashMap<>();
	protected final Map<IRecipeCategory<?>, List<IRecipeTranslator<?>>> recipeLists = new HashMap<>();
	protected final Map<ILookupContext, List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>>> recipeLookupCache = new HashMap<>();

	public RecipeIndex(TMBRuntime runtime) {
		this.runtime = runtime;
	}

	protected void clear() {
		catalysts.clear();
		categories.clear();
		recipeLists.clear();
		recipeLookupCache.clear();
		hiddenCategories.clear();
	}

	protected void loadLists() {
		for (IRecipeCategory<?> category : categories) {
			catalysts.computeIfAbsent(category, k -> new ArrayList<>());
			recipeLists.computeIfAbsent(category, k -> new ArrayList<>());
		}
	}

	public List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> searchRecipes(ILookupContext context) {
		List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> existing = recipeLookupCache.get(context);

		if (existing != null) {
			return existing;
		}

		List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> list = context.getRole() == RecipeIngredientRole.INPUT ? getRecipesCatalyst(context.getIngredient(), false) : new ArrayList<>();
		for (Map.Entry<IRecipeCategory<?>, List<IRecipeTranslator<?>>> entry : recipeLists.entrySet()) {
			if (!hiddenCategories.contains(entry.getKey().getName())) {
				for (IRecipeTranslator<?> translator : entry.getValue()) {
					boolean isIn = false;
					switch (context.getRole()) {
						case INPUT:
							if (translator.isValidInput(context.getIngredient())) {
								isIn = true;
							}
							break;
						case OUTPUT:
							if (translator.isOutput(context.getIngredient())) {
								isIn = true;
							}
							break;
						default:
							break;
					}
					if (isIn) {
						list.add(Pair.of(entry.getKey(), translator));
					}
				}
			}
		}

		recipeLookupCache.put(context, list);
		return list;
	}

	@Override
	public List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> getRecipesInput(ITypedIngredient<?> ingredient) {
		return searchRecipes(new LookupContext(ingredient, RecipeIngredientRole.INPUT));
	}

	@Override
	public List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> getRecipesOutput(ITypedIngredient<?> ingredient) {
		return searchRecipes(new LookupContext(ingredient, RecipeIngredientRole.OUTPUT));
	}

	@Override
	public List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> getRecipesCatalyst(ITypedIngredient<?> ingredient) {
		return getRecipesCatalyst(ingredient, true);
	}

	public List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> getRecipesCatalyst(ITypedIngredient<?> ingredient, boolean cache) {
		ILookupContext context = new LookupContext(ingredient, RecipeIngredientRole.CATALYST);
		List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> pairList = recipeLookupCache.get(context);

		if (pairList != null) {
			return pairList;
		} else {
			pairList = new ArrayList<>();
		}

		List<IRecipeCategory<?>> categoryList = getCategoriesForCatalyst(ingredient);

		for (IRecipeCategory<?> category : categoryList) {
			if (!hiddenCategories.contains(category.getName())) {
				for (IRecipeTranslator<?> translator : recipeLists.get(category)) {
					pairList.add(Pair.of(category, translator));
				}
			}
		}

		if (cache) recipeLookupCache.put(context, pairList);
		return pairList;
 	}

	@Override
	public List<IRecipeCategory<?>> getAllCategories() {
		return Collections.unmodifiableList(categories);
	}

	@Override
	public Map<IRecipeCategory<?>, List<IRecipeTranslator<?>>> getRecipeLists() {
		return recipeLists;
	}

	@Override
	public void registerCatalyst(IRecipeCategory<?> category, ITypedIngredient<?> catalyst) {
		catalysts.get(category).add(catalyst);
	}

	@Override
	public void hideCategory(String name) {
		hiddenCategories.add(name);
	}

	@Override
	public <T extends IRecipeCategory<?>> T registerCategory(T category) {
		categories.add(category);
		return category;
	}

	@Override
	public <R, T extends IRecipeTranslator<R>> void registerRecipes(IRecipeCategory<T> category, Collection<R> recipes, Function<R, T> conv) {
		for (R recipe : recipes) {
			registerRecipe(category, recipe, conv);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R, T extends IRecipeTranslator<R>> void registerRecipe(IRecipeCategory<T> category, R recipe, Function<R, T> conv) {
		List<T> list = (List<T>) recipeLists.get(category);
		list.add(conv.apply(recipe));
	}

	@Override
	public List<ITypedIngredient<?>> getCatalystsForCategory(IRecipeCategory<?> category) {
		if (category == null) return Collections.emptyList();
		return catalysts.get(category);
	}

	@Override
	public List<IRecipeCategory<?>> getCategoriesForCatalyst(ITypedIngredient<?> ingredient) {
		if (ingredient == null) return getAllCategories();
		ILookupContext context = new LookupContext(ingredient, RecipeIngredientRole.CATALYST);
		List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> existing = recipeLookupCache.get(context);
		List<IRecipeCategory<?>> list;

		if (existing != null) {
			list = new ArrayList<>(existing.size());
			for (Pair<IRecipeCategory<?>, ?> pair : existing) {
				list.add(pair.getLeft());
			}
		} else {
			list = new ArrayList<>();
			List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> cacheList = new ArrayList<>();
			for (IRecipeCategory<?> category : categories) {
				List<ITypedIngredient<?>> catalystList = catalysts.get(category);
				if (catalystList != null) {
					if (catalystList.stream().anyMatch((i) -> i.hashCode() == ingredient.hashCode())) {
						list.add(category);
						cacheList.add(Pair.of(category, null));
					}
				}
			}
			recipeLookupCache.put(context, cacheList);
		}
		return list;
	}
}
