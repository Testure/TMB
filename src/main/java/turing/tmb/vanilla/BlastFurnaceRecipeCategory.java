package turing.tmb.vanilla;

import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.item.ItemStack;
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

import java.util.List;

public class BlastFurnaceRecipeCategory<R extends RecipeEntryBase<RecipeSymbol[], ItemStack, Void>, T extends BlastFurnaceRecipeTranslator<R>> implements IRecipeCategory<T> {
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawable arrow;
	private final IDrawable arrowBack;
	private final IDrawable flame;
	private final IDrawable flameBack;
	private final int x = 44;
	private final String title;

	public BlastFurnaceRecipeCategory(ITMBRuntime runtime, String title, ItemStack icon) {
		this.background = new DrawableBlank(120, 40);
		this.title = title;
		this.icon = new DrawableIngredient<>(icon, ItemStackIngredientRenderer.INSTANCE);
		this.arrow = new DrawableAnimated(new DrawableTexture("/assets/tmb/textures/gui/gui_vanilla.png", 82, 128, 24, 16, 0, 0, 0, 0, 24, 16), 15, IDrawableAnimated.StartDirection.LEFT, false);
		this.arrowBack = new DrawableTexture("/assets/tmb/textures/gui/gui_vanilla.png", 24, 133, 24, 16, 0, 0, 0, 0, 24, 16);
		this.flame = new DrawableAnimated(new DrawableTexture("/assets/tmb/textures/gui/gui_vanilla.png", 82, 114, 14, 14, 0, 0,0, 0, 14, 14), 1, IDrawableAnimated.StartDirection.TOP, true);
		this.flameBack = new DrawableTexture("/assets/tmb/textures/gui/gui_vanilla.png", 1, 134, 14, 14, 0, 0, 0, 0, 14, 14);
	}

	@Override
	public String getName() {
		return title;
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
	public void drawRecipe(ITMBRuntime runtime, T recipe, IRecipeLayout layout, List<IIngredientList> ingredients, ILookupContext context) {
		getIngredients(recipe, layout, context, ingredients);

		arrowBack.draw(runtime.getGuiHelper(), x + 26, (background.getHeight() / 2) - 5);
		arrow.draw(runtime.getGuiHelper(), x + 26, (background.getHeight() / 2) - 5);
		flameBack.draw(runtime.getGuiHelper(), x + 2, (background.getHeight() / 2) + 13);
		flame.draw(runtime.getGuiHelper(), x + 2, (background.getHeight() / 2) + 13);
	}

	@Override
	public void getIngredients(T recipe, IRecipeLayout layout, ILookupContext context, List<IIngredientList> ingredients) {
		ingredients.add(0, IngredientList.fromRecipeSymbol(recipe.getOriginal().getInput()[0]));
		if(recipe.getOriginal().getInput().length > 1){
			ingredients.add(1, IngredientList.fromRecipeSymbol(recipe.getOriginal().getInput()[1]));
		} else {
			ingredients.add(1, new IngredientList());
		}
		ingredients.add(2, new IngredientList(TypedIngredient.itemStackIngredient(recipe.getOriginal().getOutput())));
	}

	@Override
	public IRecipeLayout getRecipeLayout() {
		return new RecipeLayoutBuilder()
			.addInputSlot(0, VanillaTypes.ITEM_STACK).setPosition(x, (background.getHeight() / 2) - 6).build()
			.addInputSlot(1, VanillaTypes.ITEM_STACK).setPosition(x - 20, (background.getHeight() / 2) - 6).build()
			.addOutputSlot(2, VanillaTypes.ITEM_STACK).setPosition(x + 56, (background.getHeight() / 2) - 6).build()
			.build();
	}
}
