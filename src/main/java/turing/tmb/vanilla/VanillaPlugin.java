package turing.tmb.vanilla;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.entry.*;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.menu.MenuInventoryCreative;
import turing.tmb.TypedIngredient;
import turing.tmb.api.ITMBPlugin;
import turing.tmb.api.ItemStackIngredientRenderer;
import turing.tmb.api.LootObjectIngredientRenderer;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.ingredient.IIngredientRegistry;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.util.ModIDHelper;

public class VanillaPlugin implements ITMBPlugin {
	private ShapedCraftingRecipeCategory shapedCraftingCategory;
	private ShapelessCraftingRecipeCategory shapelessCraftingCategory;
	private FurnaceRecipeCategory<RecipeEntryFurnace, FurnaceRecipeTranslator<RecipeEntryFurnace>> furnaceCategory;
	private FurnaceRecipeCategory<RecipeEntryBlastFurnace, FurnaceRecipeTranslator<RecipeEntryBlastFurnace>> blastFurnaceCategory;
	private TrommelRecipeCategory trommelCategory;

	@Override
	public void registerIngredientTypes(ITMBRuntime runtime) {
		runtime.getIngredientTypeRegistry().registerIngredientType(VanillaTypes.ITEM_STACK, ItemStackIngredientRenderer.INSTANCE);
		runtime.getIngredientTypeRegistry().registerIngredientType(VanillaTypes.LOOT_OBJECT, LootObjectIngredientRenderer.INSTANCE);
	}

	@Override
	public void registerIngredients(ITMBRuntime runtime) {
		IIngredientRegistry<ItemStack> registry = runtime.getRegistryForIngredientType(VanillaTypes.ITEM_STACK);
		for (ItemStack stack : MenuInventoryCreative.creativeItems) {
			if (stack != null && stack.itemID > 0) {
				registry.registerIngredient(ModIDHelper.getModIDForItem(stack), stack.getDisplayName(), stack);
			}
		}
	}

	@Override
	public void registerRecipes(ITMBRuntime runtime) {
		runtime.getRecipeIndex().registerRecipes(furnaceCategory, Registries.RECIPES.FURNACE.getAllRecipes(), FurnaceRecipeTranslator::new);
		runtime.getRecipeIndex().registerRecipes(blastFurnaceCategory, Registries.RECIPES.BLAST_FURNACE.getAllRecipes(), FurnaceRecipeTranslator::new);
		runtime.getRecipeIndex().registerRecipes(trommelCategory, Registries.RECIPES.TROMMEL.getAllRecipes(), TrommelRecipeTranslator::new);

		for (RecipeEntryCrafting<?, ?> entryCrafting : Registries.RECIPES.getAllCraftingRecipes()) {
			if (entryCrafting instanceof RecipeEntryCraftingShaped) {
				runtime.getRecipeIndex().registerRecipe(shapedCraftingCategory, (RecipeEntryCraftingShaped) entryCrafting, ShapedCraftingRecipeTranslator::new);
			} else if (entryCrafting instanceof RecipeEntryCraftingShapeless) {
				runtime.getRecipeIndex().registerRecipe(shapelessCraftingCategory, (RecipeEntryCraftingShapeless) entryCrafting, ShapelessCraftingRecipeTranslator::new);
			}
		}
	}

	@Override
	public void registerRecipeCategories(ITMBRuntime runtime) {
		shapedCraftingCategory = runtime.getRecipeIndex().registerCategory(new ShapedCraftingRecipeCategory());
		shapelessCraftingCategory = runtime.getRecipeIndex().registerCategory(new ShapelessCraftingRecipeCategory());
		furnaceCategory = runtime.getRecipeIndex().registerCategory(new FurnaceRecipeCategory<>(runtime, "guidebook.section.furnace", Blocks.FURNACE_STONE_ACTIVE.getDefaultStack(), false));
		blastFurnaceCategory = runtime.getRecipeIndex().registerCategory(new FurnaceRecipeCategory<>(runtime, "guidebook.section.blast_furnace", Blocks.FURNACE_BLAST_ACTIVE.getDefaultStack(), true));
		trommelCategory = runtime.getRecipeIndex().registerCategory(new TrommelRecipeCategory());
	}

	@Override
	public void registerRecipeCatalysts(ITMBRuntime runtime) {
		runtime.getRecipeIndex().registerCatalyst(shapedCraftingCategory, TypedIngredient.itemStackIngredient(Blocks.WORKBENCH.getDefaultStack()));
		runtime.getRecipeIndex().registerCatalyst(shapelessCraftingCategory, TypedIngredient.itemStackIngredient(Blocks.WORKBENCH.getDefaultStack()));
		runtime.getRecipeIndex().registerCatalyst(furnaceCategory, TypedIngredient.itemStackIngredient(Blocks.FURNACE_STONE_IDLE.getDefaultStack()));
		runtime.getRecipeIndex().registerCatalyst(blastFurnaceCategory, TypedIngredient.itemStackIngredient(Blocks.FURNACE_BLAST_IDLE.getDefaultStack()));
		runtime.getRecipeIndex().registerCatalyst(trommelCategory, TypedIngredient.itemStackIngredient(Blocks.TROMMEL_IDLE.getDefaultStack()));
	}
}
