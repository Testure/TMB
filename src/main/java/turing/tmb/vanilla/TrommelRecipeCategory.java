package turing.tmb.vanilla;

import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.net.command.TextFormatting;
import org.jetbrains.annotations.Nullable;
import turing.tmb.RecipeLayoutBuilder;
import turing.tmb.TypedIngredient;
import turing.tmb.api.ItemStackIngredientRenderer;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.drawable.IDrawable;
import turing.tmb.api.drawable.IDrawableAnimated;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.recipe.ILookupContext;
import turing.tmb.api.recipe.IRecipeCategory;
import turing.tmb.api.recipe.IRecipeLayout;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.client.DrawableAnimated;
import turing.tmb.client.DrawableBlank;
import turing.tmb.client.DrawableIngredient;
import turing.tmb.client.DrawableTexture;
import turing.tmb.util.IngredientList;
import turing.tmb.util.ModIDHelper;

import java.util.ArrayList;
import java.util.List;

public class TrommelRecipeCategory implements IRecipeCategory<TrommelRecipeTranslator> {
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawable arrowBack;
	private final IDrawable arrow;
	private final int x = 44;

	public TrommelRecipeCategory() {
		this.background = new DrawableBlank(120, 60);
		this.icon = new DrawableIngredient<>(Blocks.TROMMEL_ACTIVE.getDefaultStack(), ItemStackIngredientRenderer.INSTANCE);
		this.arrow = new DrawableAnimated(new DrawableTexture("/assets/tmb/textures/gui/gui_vanilla.png", 82, 128, 24, 16, 0, 0, 0, 0, 24, 16), 12, IDrawableAnimated.StartDirection.LEFT, false);
		this.arrowBack = new DrawableTexture("/assets/tmb/textures/gui/gui_vanilla.png", 24, 133, 24, 16, 0, 0, 0, 0, 24, 16);
	}

	@Override
	public String getName() {
		return "guidebook.section.trommel";
	}

	@Override
	public String getNamespace() {
		return "Minecraft";
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return icon;
	}

	@Override
	public void drawRecipe(ITMBRuntime runtime, TrommelRecipeTranslator recipe, IRecipeLayout layout, List<IIngredientList> ingredients, ILookupContext context) {
		ingredients.add(0, IngredientList.fromRecipeSymbol(recipe.getOriginal().getInput()));

		for (int i = 1; i <= recipe.getOriginal().getOutput().getEntries().size(); i++) {
			WeightedRandomLootObject lootObject = recipe.getOriginal().getOutput().getEntries().get(i - 1);
			ingredients.add(i, new IngredientList(new TypedIngredient<>(ModIDHelper.getModIDForItem(lootObject.getDefinedItemStack()), lootObject.getDefinedItemStack().getDisplayName(), VanillaTypes.LOOT_OBJECT, lootObject)));
		}

		arrowBack.draw(runtime.getGuiHelper(), x + 26, (background.getHeight() / 2) + 7);
		arrow.draw(runtime.getGuiHelper(), x + 26, (background.getHeight() / 2) + 7);
	}

	@Override
	public List<String> getTooltips(TrommelRecipeTranslator recipe, int mouseX, int mouseY) {
		List<String> tooltips = new ArrayList<>();
		/*for (int i = 0; i < recipe.getOriginal().getOutput().getEntries().size(); i++) {
			WeightedRandomLootObject lootObject = recipe.getOriginal().getOutput().getEntries().get(i);
			int x = 100 + (18 * (i / 3));
			int y = (background.getHeight() / 2) + 24;
			if (i > 3) y -= 18;
			if (i > 6) y -= 18;

			if (mouseX >= x && mouseY >= y && mouseX < x + 18 && mouseY < y + 18) {
				String percentage = String.valueOf(recipe.getOriginal().getOutput().getAsPercentage(lootObject.getDefinedItemStack()));
				tooltips.add(TextFormatting.formatted(percentage.substring(0, Math.min(5, percentage.length())) + "%", TextFormatting.LIGHT_GRAY));
				break;
			}
		}*/
		return tooltips;
	}

	@Override
	public IRecipeLayout getRecipeLayout() {
		return new RecipeLayoutBuilder()
			.addInputSlot(0, VanillaTypes.ITEM_STACK).setPosition(x, (background.getHeight() / 2) + 6).build()
			.addOutputSlot(1, VanillaTypes.LOOT_OBJECT).setPosition(x + 56, (background.getHeight() / 2) + 24).build()
			.addOutputSlot(2, VanillaTypes.LOOT_OBJECT).setPosition(x + 74, (background.getHeight() / 2) + 24).build()
			.addOutputSlot(3, VanillaTypes.LOOT_OBJECT).setPosition(x + 92, (background.getHeight() / 2) + 24).build()
			.addOutputSlot(4, VanillaTypes.LOOT_OBJECT).setPosition(x + 56, (background.getHeight() / 2) + 6).build()
			.addOutputSlot(5, VanillaTypes.LOOT_OBJECT).setPosition(x + 74, (background.getHeight() / 2) + 6).build()
			.addOutputSlot(6, VanillaTypes.LOOT_OBJECT).setPosition(x + 92, (background.getHeight() / 2) + 6).build()
			.addOutputSlot(7, VanillaTypes.LOOT_OBJECT).setPosition(x + 56, (background.getHeight() / 2) - 12).build()
			.addOutputSlot(8, VanillaTypes.LOOT_OBJECT).setPosition(x + 74, (background.getHeight() / 2) - 12).build()
			.addOutputSlot(9, VanillaTypes.LOOT_OBJECT).setPosition(x + 92, (background.getHeight() / 2) - 12).build()
			.build();
	}
}
