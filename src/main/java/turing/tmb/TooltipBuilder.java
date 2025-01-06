package turing.tmb;

import turing.tmb.api.drawable.builder.ITooltipBuilder;
import turing.tmb.api.ingredient.ITypedIngredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TooltipBuilder implements ITooltipBuilder {
	protected final List<String> lines = new ArrayList<>();

	@Override
	public void add(String text) {
		lines.add(text);
	}

	@Override
	public void addAll(Collection<String> text) {
		lines.addAll(text);
	}

	@Override
	public void setIngredient(ITypedIngredient<?> ingredient) {

	}

	@Override
	public void clear() {
		lines.clear();
	}

	public List<String> getLines() {
		return lines;
	}
}
