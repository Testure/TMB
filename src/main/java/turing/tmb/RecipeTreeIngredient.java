package turing.tmb;

public class RecipeTreeIngredient {
	public final RecipeIngredient ingredient;
	public int x;
	public int y;

	public RecipeTreeIngredient(RecipeIngredient ingredient, int x, int y) {
		this.ingredient = ingredient;
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
