package turing.tmb;

import org.jetbrains.annotations.Nullable;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.drawable.IDrawable;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.recipe.ILookupContext;
import turing.tmb.api.recipe.IRecipeCategory;
import turing.tmb.api.recipe.IRecipeLayout;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.client.DrawableBlank;
import turing.tmb.client.DrawableTexture;
import turing.tmb.util.IngredientList;

import java.util.List;

public class InfoRecipeCategory implements IRecipeCategory<InfoRecipeTranslator> {
	private final IDrawable background;
	private final IDrawable icon;

	public InfoRecipeCategory() {
		this.background = new DrawableBlank(180, 120);
		this.icon = new DrawableTexture("/assets/tmb/textures/gui/info.png", 0, 0, 16, 16, 256, 256);
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return icon;
	}

	@Override
	public String getName() {
		return "tmb.category.info";
	}

	@Override
	public String getNamespace() {
		return "Too Many Blocks";
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawRecipe(ITMBRuntime runtime, InfoRecipeTranslator recipe, IRecipeLayout layout, List<IIngredientList> ingredients, ILookupContext context) {
		ingredients.add(0, new IngredientList(recipe.getOriginal().getIngredient()));

		String text = recipe.getOriginal().getInfoTranslated();
		runtime.getGuiHelper().getMinecraft().fontRenderer.func_40609_a/*drawStringIntoConstrainedBlock*/(text, 3, 21, background.getWidth() - 5, 0xFFFFFF, true);
		runtime.getGuiHelper().getMinecraft().fontRenderer.func_27278_a/*drawStringIntoConstrainedBlock*/(text, 2, 20, background.getWidth() - 4, 0xFFFFFF);
	}

	@Override
	public IRecipeLayout getRecipeLayout() {
		return new RecipeLayoutBuilder()
			.addInputSlot(0, VanillaTypes.ITEM_STACK).setPosition((background.getWidth() / 2) - 13, 0).build()
			.build();
	}
}
