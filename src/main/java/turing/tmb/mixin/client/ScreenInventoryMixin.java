package turing.tmb.mixin.client;

import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.container.ScreenInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turing.tmb.client.TMBRenderer;

@Mixin(value =  ScreenInventory.class, remap = false)
public abstract class ScreenInventoryMixin extends Screen {
	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
		TMBRenderer.onTick();
	}
}
