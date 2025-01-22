package turing.tmb.mixin.client;

import net.minecraft.client.gui.guidebook.mobs.MobInfoRegistry;
import net.minecraft.core.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = MobInfoRegistry.class, remap = false)
public interface MobInfoRegistryAccessor {
	@Accessor
	static Map<Class<? extends Entity>, MobInfoRegistry.MobInfo> getMobInfoMap() {
		throw new UnsupportedOperationException();
	}
}
