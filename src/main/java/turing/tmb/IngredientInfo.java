package turing.tmb;

import net.minecraft.core.lang.I18n;
import turing.tmb.api.ingredient.ITypedIngredient;

public class IngredientInfo {
	private final ITypedIngredient<?> ingredient;
	private final String info;
	private final boolean isTranslated;

	public IngredientInfo(ITypedIngredient<?> ingredient, String info, boolean isTranslated) {
		this.ingredient = ingredient;
		this.info = info;
		this.isTranslated = isTranslated;
	}

	public IngredientInfo(ITypedIngredient<?> ingredient, String info) {
		this(ingredient, info, true);
	}

	public ITypedIngredient<?> getIngredient() {
		return ingredient;
	}

	public String getInfo() {
		return info;
	}

	public boolean isTranslated() {
		return isTranslated;
	}

	public String getInfoTranslated() {
		return isTranslated() ? I18n.getInstance().translateKey(getInfo()) : getInfo();
	}
}
