package turing.tmb.vanilla;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import turing.tmb.RecipeLayoutBuilder;
import turing.tmb.RecipeTranslator;
import turing.tmb.TMB;
import turing.tmb.TypedIngredient;
import turing.tmb.api.ItemStackIngredientRenderer;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.drawable.IDrawable;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.recipe.ILookupContext;
import turing.tmb.api.recipe.IRecipeCategory;
import turing.tmb.api.recipe.IRecipeLayout;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.client.DrawableBlank;
import turing.tmb.client.DrawableIngredient;
import turing.tmb.client.DrawableTexture;
import turing.tmb.util.IngredientList;

import java.util.List;

public abstract class AbstractCraftingRecipeCategory<R extends RecipeEntryCrafting<?, ItemStack>, T extends RecipeTranslator<R>> implements IRecipeCategory<T> {
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawable arrow;
	protected final int x = 44;

	public AbstractCraftingRecipeCategory() {
		this.background = new DrawableBlank(120, 60);
		this.icon = new DrawableIngredient<>(Blocks.WORKBENCH.getDefaultStack(), ItemStackIngredientRenderer.INSTANCE);
		this.arrow = new DrawableTexture("/assets/tmb/textures/gui/gui_vanilla.png", 24, 133, 24, 16, 0, 0, 0, 0, 24, 16);
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

		arrow.draw(runtime.getGuiHelper(), x + 62, (background.getHeight() / 2) + 6);
	}

	abstract void addInputs(ITMBRuntime runtime, T recipe, IRecipeLayout layout, List<IIngredientList> ingredients, ILookupContext context);

	@Override
	public void getIngredients(T recipe, IRecipeLayout layout, ILookupContext context, List<IIngredientList> ingredients) {
		ingredients.add(0, new IngredientList(TypedIngredient.itemStackIngredient(recipe.getOriginal().getOutput())));
		addInputs(TMB.getRuntime(), recipe, layout, ingredients, context);
	}

	@Override
	public IRecipeLayout getRecipeLayout() {
		return new RecipeLayoutBuilder()
			.addOutputSlot(0, VanillaTypes.ITEM_STACK).setPosition(x + 92, (background.getHeight() / 2) + 6).build()
			.addInputSlot(1, VanillaTypes.ITEM_STACK).setPosition(x, (getBackground().getHeight() / 2) - 18).build()
			.addInputSlot(2, VanillaTypes.ITEM_STACK).setPosition(x + 18, (getBackground().getHeight() / 2) - 18).build()
			.addInputSlot(3, VanillaTypes.ITEM_STACK).setPosition(x + 36, (getBackground().getHeight() / 2) - 18).build()
			.addInputSlot(4, VanillaTypes.ITEM_STACK).setPosition(x, (getBackground().getHeight() / 2)).build()
			.addInputSlot(5, VanillaTypes.ITEM_STACK).setPosition(x + 18, (getBackground().getHeight() / 2)).build()
			.addInputSlot(6, VanillaTypes.ITEM_STACK).setPosition(x + 36, (getBackground().getHeight() / 2)).build()
			.addInputSlot(7, VanillaTypes.ITEM_STACK).setPosition(x, (getBackground().getHeight() / 2) + 18).build()
			.addInputSlot(8, VanillaTypes.ITEM_STACK).setPosition(x + 18, (getBackground().getHeight() / 2) + 18).build()
			.addInputSlot(9, VanillaTypes.ITEM_STACK).setPosition(x + 36, (getBackground().getHeight() / 2) + 18).build()
			.build();
	}
}
