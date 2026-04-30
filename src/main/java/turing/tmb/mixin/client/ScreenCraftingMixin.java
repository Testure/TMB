package turing.tmb.mixin.client;

import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.gui.container.ScreenCrafting;
import net.minecraft.client.gui.guidebook.SlotGuidebook;
import net.minecraft.client.gui.guidebook.crafting.RecipePageCrafting;
import net.minecraft.client.gui.guidebook.crafting.displays.RecipeDisplayAdapter;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShaped;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShapeless;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.util.collection.Pair;
import org.spongepowered.asm.mixin.Mixin;
import turing.tmb.api.RecipeFiller;
import turing.tmb.api.recipe.IRecipeTranslator;
import turing.tmb.util.InventoryWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = ScreenCrafting.class, remap = false)
public abstract class ScreenCraftingMixin extends ScreenContainerAbstract implements RecipeFiller<RecipeEntryCrafting<?, ItemStack>, ScreenCrafting> {
	private ScreenCraftingMixin(MenuAbstract container) {
		super(container);
	}

	@Override
	public void fillRecipe(IRecipeTranslator<RecipeEntryCrafting<?, ItemStack>> translator, boolean maximum) {
		InventoryWrapper w = new InventoryWrapper(mc.thePlayer.inventory);
		for (Slot slot : inventorySlots.slots) {
			if(slot.hasItem() && slot.index > 0 && slot.index < 10){
				mc.thePlayer.inventory.insertItem(slot.getItemStack(), false);
				slot.set(null);
			}
		}
		if (translator.getOriginal() instanceof RecipeEntryCraftingShaped) {
			RecipeEntryCraftingShaped recipe = (RecipeEntryCraftingShaped) translator.getOriginal();
			List<RecipeSymbol> list = Arrays.stream(recipe.getInput()).collect(Collectors.toList());
			Pair<Boolean, List<ItemStack>> pair = w.containsRecipe(list);
			if(pair.getLeft()){
				List<ItemStack> materials = pair.getRight();
				RecipeDisplayAdapter<RecipeEntryCraftingShaped> adapter = (RecipeDisplayAdapter<RecipeEntryCraftingShaped>) RecipePageCrafting.recipeToDisplayAdapterMap.get(RecipeEntryCraftingShaped.class);
				List<SlotGuidebook> slots = adapter.getSlots(recipe, 0, 0, 0);

				int maxAmount = materials.stream().mapToInt(stack -> stack.stackSize).min().orElse(0);

				for (int i = 0; i < maxAmount; i++) {
					for (SlotGuidebook slot : slots) {
						if (slot.symbol == null && slot.item == null) continue;
						for (ItemStack material : materials) {
							List<ItemStack> resolved = new ArrayList<>();
							if (slot.symbol != null) {
								resolved = slot.symbol.resolve();
							} else if (slot.item != null) {
								resolved.add(slot.item);
							}
							Pair<Boolean, ItemStack> contains = InventoryWrapper.listContains(resolved, material, ItemStack::isItemEqual, true);
							if (contains.getLeft()) {
								ItemStack recipeStack = contains.getRight();
								ItemStack removedStack = w.removeUntil(recipeStack.itemID, recipeStack.getMetadata(), recipeStack.stackSize, recipeStack.getData(), false, false);
								if(removedStack == null) continue;
								if (inventorySlots.slots.get(slot.index + 1).getItemStack() == null) {
									inventorySlots.slots.get(slot.index + 1).set(removedStack);
								} else if (removedStack.isItemEqual(inventorySlots.slots.get(slot.index + 1).getItemStack())) {
									inventorySlots.slots.get(slot.index + 1).getItemStack().stackSize += removedStack.stackSize;
								}
							}
						}
					}
				}
			}

		}
		if(translator.getOriginal() instanceof RecipeEntryCraftingShapeless) {
			RecipeEntryCraftingShapeless recipe = (RecipeEntryCraftingShapeless) translator.getOriginal();
			List<RecipeSymbol> list = new ArrayList<>(recipe.getInput());
			Pair<Boolean, List<ItemStack>> pair = w.containsRecipe(list);
			if(pair.getLeft()){
				List<ItemStack> materials = pair.getRight();
				RecipeDisplayAdapter<RecipeEntryCraftingShapeless> adapter = (RecipeDisplayAdapter<RecipeEntryCraftingShapeless>) RecipePageCrafting.recipeToDisplayAdapterMap.get(RecipeEntryCraftingShapeless.class);
				List<SlotGuidebook> slots = adapter.getSlots(recipe, 0, 0, 0);

				int maxAmount = materials.stream().mapToInt(stack -> stack.stackSize).min().orElse(0);

				for (int i = 0; i < maxAmount; i++) {
					for (SlotGuidebook slot : slots) {
						if (slot.symbol == null && slot.item == null) continue;
						List<ItemStack> resolved = new ArrayList<>();
						if (slot.symbol != null) {
							resolved = slot.symbol.resolve();
						} else {
							resolved.add(slot.item);
						}
						for (ItemStack material : materials) {
							Pair<Boolean, ItemStack> contains = InventoryWrapper.listContains(resolved, material, ItemStack::isItemEqual, true);
							if (contains.getLeft()) {
								ItemStack recipeStack = contains.getRight();
								ItemStack removedStack = w.removeUntil(recipeStack.itemID, recipeStack.getMetadata(), recipeStack.stackSize, recipeStack.getData(), false, false);
								if(removedStack == null) continue;
								if (inventorySlots.slots.get(slot.index + 1).getItemStack() == null) {
									inventorySlots.slots.get(slot.index + 1).set(removedStack);
								} else if (removedStack.isItemEqual(inventorySlots.slots.get(slot.index + 1).getItemStack())) {
									inventorySlots.slots.get(slot.index + 1).getItemStack().stackSize += removedStack.stackSize;
								}
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
		list.add(RecipeEntryCraftingShaped.class);
		list.add(RecipeEntryCraftingShapeless.class);
		return list;
	}
}
