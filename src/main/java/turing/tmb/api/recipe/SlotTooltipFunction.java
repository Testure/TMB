package turing.tmb.api.recipe;

import turing.tmb.api.drawable.builder.ITooltipBuilder;

import java.util.List;

@FunctionalInterface
public interface SlotTooltipFunction<T> {
	List<String> apply(ITooltipBuilder tooltipBuilder, T ingredient, int mouseX, int mouseY, boolean isCtrl, boolean isShift);
}
