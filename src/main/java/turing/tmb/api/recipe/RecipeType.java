package turing.tmb.api.recipe;

public final class RecipeType<T> {
	public static <T> RecipeType<T> create(String namespace, String name, Class<? extends T> recipeClass) {
		return new RecipeType<>(namespace + ":" + name, recipeClass);
	}

	private final String uid;
	private final Class<? extends T> recipeClass;

	public RecipeType(String uid, Class<? extends T> recipeClass) {
		this.uid = uid;
		this.recipeClass = recipeClass;
	}

	public String getUid() {
		return uid;
	}

	public Class<? extends T> getRecipeClass() {
		return recipeClass;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof RecipeType<?>)) return false;
		return getRecipeClass() == ((RecipeType<?>) obj).getRecipeClass() && getUid().equals(((RecipeType<?>) obj).getUid());
	}

	@Override
	public int hashCode() {
		return 31 * uid.hashCode() + recipeClass.hashCode();
	}

	@Override
	public String toString() {
		return "RecipeType[" + "uid=" + getUid() + ", recipeClass=" + getRecipeClass() + "]";
	}
}
