package turing.tmb.mixin.client;

import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.gui.container.ScreenFurnace;
import net.minecraft.core.block.entity.TileEntityFurnace;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryBlastFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.util.collection.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import turing.tmb.api.RecipeFiller;
import turing.tmb.api.recipe.IRecipeTranslator;
import turing.tmb.util.InventoryWrapper;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ScreenFurnace.class, remap = false)
public abstract class ScreenFurnaceMixin extends ScreenContainerAbstract implements RecipeFiller<RecipeEntryBase<RecipeSymbol, ItemStack, Void>, ScreenFurnace> {
	@Shadow
	@Final
	private TileEntityFurnace furnaceInventory;

	public ScreenFurnaceMixin(MenuAbstract container) {
		super(container);
	}

	@Override
	public void fillRecipe(IRecipeTranslator<RecipeEntryBase<RecipeSymbol, ItemStack, Void>> translator, boolean maximum) {
		InventoryWrapper w = new InventoryWrapper(mc.thePlayer.inventory);
		for (Slot slot : inventorySlots.slots) {
			if(slot.hasItem() && slot.index == 0){
				mc.thePlayer.inventory.insertItem(slot.getItemStack(), false);
				slot.set(null);
			}
		}

		if (translator.getOriginal() instanceof RecipeEntryFurnace) {
			RecipeEntryFurnace recipe = (RecipeEntryFurnace) translator.getOriginal();
			List<RecipeSymbol> list = new ArrayList<>();
			list.add(recipe.getInput());
			Pair<Boolean, List<ItemStack>> pair = w.containsRecipe(list);
			if(pair.getLeft()){
				List<ItemStack> materials = pair.getRight();
				List<ItemStack> resolved = recipe.getInput().resolve();
				Pair<Boolean, ItemStack> contains = InventoryWrapper.listContains(resolved, materials.get(0), ItemStack::isItemEqual, true);
				if(contains.getLeft()){
					ItemStack recipeStack = contains.getRight();
					int amount = recipeStack.stackSize;
					if(maximum){
						amount = materials.get(0).stackSize / recipeStack.stackSize;
					}
					ItemStack removedStack = w.removeUntil(recipeStack.itemID, recipeStack.getMetadata(), amount, recipeStack.getData(), false, false);
					inventorySlots.slots.get(0).set(removedStack);
				}
			}
		}

		if (translator.getOriginal() instanceof RecipeEntryBlastFurnace) {
			RecipeEntryBlastFurnace recipe = (RecipeEntryBlastFurnace) translator.getOriginal();
			List<RecipeSymbol> list = new ArrayList<>();
			list.add(recipe.getInput());
			Pair<Boolean, List<ItemStack>> pair = w.containsRecipe(list);
			if(pair.getLeft()){
				List<ItemStack> materials = pair.getRight();
				List<ItemStack> resolved = recipe.getInput().resolve();
				Pair<Boolean, ItemStack> contains = InventoryWrapper.listContains(resolved, materials.get(0), ItemStack::isItemEqual, true);
				if(contains.getLeft()){
					ItemStack recipeStack = contains.getRight();
					int amount = recipeStack.stackSize;
					if(maximum){
						amount = materials.get(0).stackSize / recipeStack.stackSize;
					}
					ItemStack removedStack = w.removeUntil(recipeStack.itemID, recipeStack.getMetadata(), amount, recipeStack.getData(), false, false);
					inventorySlots.slots.get(0).set(removedStack);
				}
			}
		}
	}

	@Override
	public List<Class<? extends RecipeEntryBase<?, ?, ?>>> getSupportedRecipes() {
		ArrayList<Class<? extends RecipeEntryBase<?, ?, ?>>> list = new ArrayList<>();
		if(furnaceInventory instanceof TileEntityFurnace){
			list.add(RecipeEntryFurnace.class);
		} else {
			list.add(RecipeEntryBlastFurnace.class);
		}
		return list;
	}
}
