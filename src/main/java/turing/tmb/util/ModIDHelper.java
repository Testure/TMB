package turing.tmb.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.item.ItemStack;

import java.util.Optional;

public class ModIDHelper {
	public static String getModIDForItem(ItemStack stack) {
		if (stack == null) return "minecraft";
		String name = stack.getItemKey();
		if (name.length() < 5) return "minecraft";
		name = name.substring(5);
		if (name.contains(".")) name = name.substring(0, name.indexOf("."));

		if (!FabricLoader.getInstance().getModContainer(name).isPresent()) return "minecraft";
		return name;
	}

	public static String getNameForItem(ItemStack stack) {
		String name = stack.getItemKey().contains(":") ? stack.getItemKey().split(":")[1] : stack.getItemKey();
		if (name == null) {
			name = stack.getItemKey();
		}
		return name;
	}

	public static String getModNameForDisplay(String modid) {
		if (modid.equals("minecraft")) {
			return "Minecraft";
		} else {
			Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(modid);
			if (container.isPresent()) {
				return container.get().getMetadata().getName();
			}
		}
		return modid;
	}
}
