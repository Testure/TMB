package turing.tmb.api;

import net.minecraft.core.WeightedRandomLootObject;
import org.lwjgl.opengl.GL11;
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
			GL11.glScalef(0.9F, 0.9F, 1F);
			helper.getMinecraft().fontRenderer.drawStringWithShadow(text, 0, 9, 0xFFFFFF);
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
