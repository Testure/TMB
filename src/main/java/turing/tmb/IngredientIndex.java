package turing.tmb;

import turing.tmb.api.ISearchQuery;
import turing.tmb.api.ingredient.IIngredientType;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.runtime.IIngredientIndex;

import java.util.*;
import java.util.stream.Collectors;

public class IngredientIndex implements IIngredientIndex {
	protected final TMBRuntime runtime;
	protected final List<ITypedIngredient<?>> completeIngredientList = new ArrayList<>();
	protected List<ITypedIngredient<?>> visibleIngredients = new ArrayList<>();
	protected List<Object> hidden = new ArrayList<>();
	protected Map<ISearchQuery, Collection<ITypedIngredient<?>>> searchCache = new HashMap<>(10);

	protected IngredientIndex(TMBRuntime runtime) {
		this.runtime = runtime;
	}

	protected void clear() {
		completeIngredientList.clear();
		visibleIngredients.clear();
		hidden.clear();
		searchCache.clear();
	}

	protected void gatherIngredients() {
		for (Map.Entry<IIngredientType<?>, IngredientRegistry<?>> entry : runtime.ingredientRegistries.entrySet()) {
			for (ITypedIngredient<?> ingredient : entry.getValue().ingredients) {
				completeIngredientList.add(ingredient);
				if (hidden.stream().noneMatch((o) -> o.hashCode() == ingredient.hashCode())) {
					visibleIngredients.add(ingredient);
				}
			}
		}
	}

	@Override
	public Collection<ITypedIngredient<?>> getAllIngredients() {
		return Collections.unmodifiableList(completeIngredientList);
	}

	@Override
	public Collection<ITypedIngredient<?>> getAllVisibleIngredients() {
		return Collections.unmodifiableList(visibleIngredients);
	}

	@Override
	public Collection<ITypedIngredient<?>> getFilteredIngredients(ISearchQuery query) {
		return cacheSearch(query);
	}

	@Override
	public <T> void hideIngredient(T ingredient) {
		hidden.add(ingredient);
	}

	@Override
	public <T> void unhideIngredient(T ingredient) {
		hidden.remove(ingredient);
	}

	private Collection<ITypedIngredient<?>> cacheSearch(ISearchQuery query) {
		Collection<ITypedIngredient<?>> existing = searchCache.get(query);
		if (existing == null) {
			existing = visibleIngredients.stream().filter(t -> {
				if (!query.getNamespaceFilter().isEmpty()) {
					if (!t.getNamespace().toLowerCase().contains(query.getNamespaceFilter().toLowerCase())) return false;
				}
				return t.getUid().toLowerCase().contains(query.getText().toLowerCase());
			}).collect(Collectors.toList());
			if (searchCache.size() > 10) {
				searchCache.remove(searchCache.keySet().stream().findFirst().get());
			}
			searchCache.put(query, existing);
		}
		return existing;
	}
}
