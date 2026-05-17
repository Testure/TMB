package turing.tmb.api;

import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.WeightedRandomLootObject;
import turing.tmb.api.drawable.builder.ITooltipBuilder;
import turing.tmb.api.drawable.gui.IGuiHelper;
import turing.tmb.api.ingredient.IIngredientRenderer;

import java.util.ArrayList;
import java.util.List;

public class LootObjectIngredientRenderer implements IIngredientRenderer<WeightedRandomLootObject> {
	@Override
	public void render(IGuiHelper helper, WeightedRandomLootObject ingredient) {
		ItemStackIngredientRenderer.INSTANCE.render(helper, ingredient.getDefinedItemStack());
		if (ingredient.isRandomYield()) {
			String text = ingredient.getMinYield() + "-" + ingredient.getMaxYield();
			GLRenderer.modelM4f().scale(0.9F, 0.9F, 1F);
			helper.getMinecraft().font.render(text, 0, 9).setColor(0xFFFFFF).setShadow().call();
		}
	}

	@Override
	public List<String> getTooltip(ITooltipBuilder tooltipBuilder, boolean isCtrl, boolean isShift) {
		return new ArrayList<>();
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltipBuilder, WeightedRandomLootObject ingredient, boolean isCtrl, boolean isShift) {
		ItemStackIngredientRenderer.INSTANCE.getTooltip(tooltipBuilder, ingredient.getDefinedItemStack(), isCtrl, isShift);
	}

	public static final LootObjectIngredientRenderer INSTANCE = new LootObjectIngredientRenderer();
}
