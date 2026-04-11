package turing.tmb.util;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.collection.Pair;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class InventoryWrapper {

	public Container connected;

	public InventoryWrapper(Container inventory) {
		connected = inventory;
	}


	public ItemStack add(ItemStack stack) {
		if (stack == null || connected == null) return stack;

		int n = stack.stackSize;

		for (int i = 0; i < connected.getContainerSize(); i++) {
			ItemStack invStack = connected.getItem(i);
			if (invStack == null) {
				int amount = Math.min(stack.stackSize, stack.getMaxStackSize(connected));
				n -= amount;
				connected.setItem(i, stack.splitStack(amount));
				if (n <= 0) break;
			} else if (invStack.isItemEqual(stack)) {
				int remaining = Math.min(n, invStack.getMaxStackSize(connected) - invStack.stackSize);
				n -= remaining;
				invStack.stackSize += remaining;
				if (n <= 0) break;
			}
		}

		if (n <= 0) {
			return null;
		}

		return new ItemStack(stack.itemID, n, stack.getMetadata(), stack.getData());
	}


	public ItemStack add(int index, ItemStack stack) {
		if (stack == null || connected == null) return stack;

		ItemStack invStack = connected.getItem(index);
		if (invStack == null) {
			ItemStack split = stack.splitStack(Math.min(stack.stackSize, stack.getMaxStackSize(connected)));
			connected.setItem(index, split);
			return stack.stackSize <= 0 ? null : stack;
		} else if (invStack.isItemEqual(stack)) {
			int remaining = Math.min(stack.stackSize, invStack.getMaxStackSize(connected) - invStack.stackSize);
			ItemStack split = stack.splitStack(remaining);
			invStack.stackSize += split.stackSize;
			return stack.stackSize <= 0 ? null : stack;
		}
		return stack;
	}


	public @UnmodifiableView List<ItemStack> addAll(List<ItemStack> stacks) {
		ArrayList<ItemStack> newStacks = new ArrayList<>();

		for (ItemStack stack : stacks) {
			newStacks.add(add(stack));
		}

		return Collections.unmodifiableList(condenseItemList(newStacks));
	}


	public long getItemCapacity() {
		return connected != null ? (long) connected.getContainerSize() * connected.getMaxStackSize() : 0;
	}


	public long getStackCapacity() {
		return getItemCapacity() / 64;
	}


	public long getStackAmount() {
		return getAmount() / 64;
	}


	public long getAmount() {
		return collectAndCondenseStacks(connected).stream().filter(Objects::nonNull).mapToInt((S) -> S.stackSize).sum();
	}


	public ItemStack remove(int slot, long amount, boolean strict, boolean unlimited) {
		if (connected == null) return null;
		List<ItemStack> stacks = collectStacks(connected);
		if (slot >= stacks.size()) {
			return null;
		}
		ItemStack stack = stacks.get(slot);
		if (stack == null) return null;
		if (strict && amount > stack.stackSize) {
			return null;
		} else if (!strict) {
			amount = Math.min(amount, stack.stackSize);
			if (!unlimited) amount = Math.min(amount, stack.getItem().getItemStackLimit(stack));
			ItemStack splitStack = stack.splitStack((int) amount);
			connected.setItem(slot, stack);
			if (stack.stackSize <= 0) {
				connected.setItem(slot, null);
			}
			inventoryChanged();
			return splitStack;
		}
		return null;
	}


	public ItemStack remove(int slot, boolean strict, boolean unlimited) {
		List<ItemStack> stacks = collectStacks(connected);
		if (slot >= stacks.size()) {
			return null;
		}
		ItemStack stack = stacks.get(slot);
		if (stack == null) return null;
		return remove(slot, stack.getItem().getItemStackLimit(stack), strict, unlimited);
	}


	public ItemStack remove(int id, int meta, long amount, CompoundTag data, boolean strict, boolean unlimited) {
		int index = find(id, meta, data);
		if (index != -1) {
			return remove(index, amount, strict, unlimited);
		}
		return null;
	}

	public ItemStack removeUntil(int id, int meta, long amount, CompoundTag data, boolean strict, boolean unlimited) {
		List<ItemStack> stacks = new ArrayList<>();
		int actualAmount = 0;
		int index = find(id, meta, data);
		while (actualAmount < amount && index != -1) {
			ItemStack stack = remove(index, amount - actualAmount, strict, unlimited);
			if (stack != null) {
				stacks.add(stack);
				actualAmount += stack.stackSize;
			}
			index = find(id, meta, data);
		}
		stacks = condenseItemList(stacks);
		return stacks.isEmpty() ? null : stacks.get(0);
	}


	public boolean removeAll(List<ItemStack> stacks, boolean strict, boolean unlimited) {
		for (ItemStack stack : stacks) {
			ItemStack removed = remove(stack.itemID, stack.getMetadata(), stack.stackSize, stack.getData(), strict, unlimited);
			if (removed == null) {
				return false;
			}
		}
		return true;
	}


	public List<ItemStack> exportAll(List<ItemStack> stacks, boolean strict, boolean unlimited) {
		ArrayList<ItemStack> list = new ArrayList<>();
		for (ItemStack stack : stacks) {
			ItemStack removed = remove(stack.itemID, stack.getMetadata(), stack.stackSize, stack.getData(), strict, unlimited);
			if (removed != null) {
				list.add(removed);
			}
		}
		return list;
	}


	public boolean eject(World world, int x, int y, int z, int slot, long amount, boolean strict) {
		ItemStack content = remove(slot, amount, strict, false);
		if (content != null) {
			float f = world.rand.nextFloat() * 0.8F + 0.1F;
			float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
			float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
			EntityItem entityitem = new EntityItem(world, (float) x + f, (float) y + f1, (float) z + f2, content);
			float f3 = 0.05F;
			entityitem.xd = (float) world.rand.nextGaussian() * f3;
			entityitem.yd = (float) world.rand.nextGaussian() * f3 + 0.2F;
			entityitem.zd = (float) world.rand.nextGaussian() * f3;
			world.entityJoinedWorld(entityitem);
			inventoryChanged();
			return true;
		}
		return false;
	}


	public boolean eject(World world, int x, int y, int z, int id, int meta, CompoundTag data, long amount, boolean strict) {
		ItemStack content = remove(id, meta, amount, data, strict, false);
		if (content != null) {
			float f = world.rand.nextFloat() * 0.8F + 0.1F;
			float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
			float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
			EntityItem entityitem = new EntityItem(world, (float) x + f, (float) y + f1, (float) z + f2, content);
			float f3 = 0.05F;
			entityitem.xd = (float) world.rand.nextGaussian() * f3;
			entityitem.yd = (float) world.rand.nextGaussian() * f3 + 0.2F;
			entityitem.zd = (float) world.rand.nextGaussian() * f3;
			world.entityJoinedWorld(entityitem);
			inventoryChanged();
			return true;
		}
		return false;
	}


	public void ejectAll(World world, int x, int y, int z) {
		for (ItemStack content : getStacks()) {
			if (content == null) continue;
			eject(world, x, y, z, content.itemID, content.getMetadata(), content.getData(), content.stackSize, false);
		}
	}


	public boolean contains(int id, int meta, CompoundTag data) {
		List<ItemStack> stacks = getStacks();
		return stacks.stream().anyMatch(stack -> stack.itemID == id && stack.getMetadata() == meta);
	}


	public boolean containsAtLeast(int id, int meta, CompoundTag data, long amount) {
		List<ItemStack> stacks = getStacks();
		return stacks.stream().anyMatch((stack) -> stack.itemID == id && stack.getMetadata() == meta && stack.stackSize >= amount);
	}


	public boolean containsAtLeast(List<ItemStack> comparedTo) {
		List<ItemStack> items = getStacks();
		return items.stream().filter(Objects::nonNull)
			.anyMatch((stack) -> comparedTo.stream().filter(Objects::nonNull)
				.anyMatch((comparedToStack) -> stack.isItemEqual(comparedToStack) && stack.stackSize >= comparedToStack.stackSize));
	}

	public Pair<Boolean, List<ItemStack>> containsRecipe(List<RecipeSymbol> symbols) {
		List<ItemStack> items = getStacks();
		List<List<ItemStack>> comparing = symbols.stream().filter(Objects::nonNull).map(RecipeSymbol::resolve).collect(Collectors.toList());
		Set<ItemStack> foundItems = new HashSet<>();
		return Pair.of(comparing.stream().filter(Objects::nonNull).allMatch(list ->
			list.stream().anyMatch(stack -> {
				Pair<Boolean, ItemStack> pair = listContains(items, stack, ItemStack::isItemEqual, true);
				if(pair.getLeft()){
					foundItems.add(pair.getRight());
				}
				return pair.getLeft();
			})), foundItems.stream().filter(Objects::nonNull).collect(Collectors.toList()));
		/*return Pair.of(items.stream()
			.filter(Objects::nonNull)
			.allMatch((stack -> comparing.stream().anyMatch(list -> {
				Pair<Boolean, ItemStack> pair = listContains(list, stack, ItemStack::isItemEqual);
				foundItems.add(pair.getRight());
				return pair.getLeft();
			}))), foundItems.stream().filter(Objects::nonNull).collect(Collectors.toList()));*/
	}

	public ArrayList<ItemStack> returnMissing(ArrayList<ItemStack> stacks) {
		ArrayList<ItemStack> missing = new ArrayList<>();
		for (ItemStack stack : stacks) {
			long c = count(stack.itemID, stack.getMetadata(), stack.getData());
			if (c <= 0) {
				missing.add(stack.copy());
			} else if (c != stack.stackSize) {
				ItemStack copy = stack.copy();
				copy.stackSize -= (int) c;
				missing.add(stack.copy());
			}
		}
		return missing;
	}


	public long count(int id, int meta, CompoundTag data) {
		List<ItemStack> stacks = getStacks();
		return stacks.stream().filter((S) -> S.itemID == id && S.getMetadata() == meta).mapToInt((S) -> S.stackSize).sum();
	}


	public long count(int id) {
		List<ItemStack> stacks = getStacks();
		return stacks.stream().filter((S) -> S.itemID == id).mapToInt((S) -> S.stackSize).sum();
	}



	public int find(int id, int meta, CompoundTag data) {
		List<ItemStack> stacks = collectStacks(connected);
		for (int i = 0; i < stacks.size(); i++) {
			if (stacks.get(i) == null) continue;
			if (stacks.get(i).itemID == id && (stacks.get(i).getMetadata() == meta || meta == -1)) {
				if (stacks.get(i).getData().equals(data) || data == null) {
					return i;
				}
			}
		}
		return -1;
	}


	public ItemStack get(int index) {
		List<ItemStack> stacks = collectStacks(connected);
		if (index < 0 || index >= stacks.size()) {
			return null;
		}
		return stacks.get(index);
	}


	public ItemStack get(int id, int meta, CompoundTag data) {
		return get(find(id, meta, data));
	}


	public ItemStack getLast() {
		return getStacks().get(getStacks().size() - 1);
	}


	public void inventoryChanged() {

	}

	/**
	 * Unsupported in this class, will always throw {@link UnsupportedOperationException}!
	 */

	public void clear() {
		throw new UnsupportedOperationException();
	}


	public @UnmodifiableView List<ItemStack> getStacks() {
		return collectAndCondenseStacks(connected);
	}


	public boolean isEmpty() {
		return getStacks().isEmpty();
	}

	public static ArrayList<ItemStack> condenseItemList(List<ItemStack> list) {
		ArrayList<ItemStack> stacks = new ArrayList<>();
		for (ItemStack stack : list) {
			if (stack != null) {
				boolean found = false;
				for (ItemStack S : stacks) {
					if (S.isItemEqual(stack) && (S.getData().equals(stack.getData()))) {
						S.stackSize += stack.stackSize;
						found = true;
					}
				}
				if (!found) stacks.add(stack.copy());
			}
		}
		return stacks;
	}

	public static @UnmodifiableView List<ItemStack> collectStacks(Container inv) {
		if (inv == null) return Collections.emptyList();
		ArrayList<ItemStack> stacks = new ArrayList<>();

		for (int i = 0; i < inv.getContainerSize(); i++) {
			stacks.add(i, inv.getItem(i));
		}

		return Collections.unmodifiableList(stacks);
	}

	public static @UnmodifiableView List<ItemStack> collectAndCondenseStacks(Container inv) {
		return condenseItemList(collectStacks(inv));
	}

	public static <T> Pair<Boolean, T> listContains(List<T> list, T o, BiFunction<T, T, Boolean> equals, boolean returnFromList) {
		for (T obj : list) {
			if (equals.apply(o, obj)) {
				if(returnFromList) {
					return Pair.of(true, obj);
				} else {
					return Pair.of(true, o);
				}
			}
		}
		return Pair.of(false, null);
	}

}
