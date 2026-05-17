package turing.tmb.mixin.client;

import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.gui.container.ScreenFurnace;
import net.minecraft.client.gui.container.ScreenFurnaceBlast;
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

@Mixin(value = ScreenFurnaceBlast.class, remap = false)
public abstract class ScreenFurnaceBlastMixin extends ScreenContainerAbstract implements RecipeFiller<RecipeEntryBase<RecipeSymbol[], ItemStack, Void>, ScreenFurnaceBlast> {
	public ScreenFurnaceBlastMixin(MenuAbstract container) {
		super(container);
	}

	@Override
	public void fillRecipe(IRecipeTranslator<RecipeEntryBase<RecipeSymbol[], ItemStack, Void>> translator, boolean maximum) {
		InventoryWrapper w = new InventoryWrapper(mc.thePlayer.inventory);
		for (Slot slot : inventorySlots.slots) {
			if(slot.hasItem() && slot.index == 0){
				mc.thePlayer.inventory.insertItem(slot.getItemStack(), false);
				slot.set(null);
			}
		}

		RecipeEntryBlastFurnace recipe = (RecipeEntryBlastFurnace) translator.getOriginal();
		List<RecipeSymbol> input = new ArrayList<>(List.of(recipe.getInput()));
		Pair<Boolean, List<ItemStack>> pair = w.containsRecipe(input);
		if(pair.getLeft()){
			List<ItemStack> materials = pair.getRight();
			int maxAmount = materials.stream().mapToInt(stack -> stack.stackSize).min().orElse(0);
			for (int i = 0; i < maxAmount; i++) {
				for (int j = 0; j < 2; j++) {
					if(input.get(j) == null) continue;
					for (ItemStack material : materials) {
						List<ItemStack> resolved = new ArrayList<>();
						if (input.get(j) != null) {
							resolved = input.get(j).resolve();
						}
						Pair<Boolean, ItemStack> contains = InventoryWrapper.listContains(resolved, material, ItemStack::isItemEqual, true);
						if (contains.getLeft()) {
							ItemStack recipeStack = contains.getRight();
							ItemStack removedStack = w.removeUntil(recipeStack.itemID, recipeStack.getMetadata(), recipeStack.stackSize, recipeStack.getData(), false, false);
							if(removedStack == null) continue;
							if (inventorySlots.slots.get(j).getItemStack() == null) {
								inventorySlots.slots.get(j).set(removedStack);
							} else if (removedStack.isItemEqual(inventorySlots.slots.get(j).getItemStack())) {
								inventorySlots.slots.get(j).getItemStack().stackSize += removedStack.stackSize;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public List<Class<? extends RecipeEntryBase<?, ?, ?>>> getSupportedRecipes() {
		ArrayList<Class<? extends RecipeEntryBase<?, ?, ?>>> list = new ArrayList<>();
		list.add(RecipeEntryBlastFurnace.class);
		return list;
	}
}
