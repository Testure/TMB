package turing.tmb.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turing.tmb.TMB;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/window/GameWindow;updateWindowState()V", shift = At.Shift.BY))
	public void onStart(CallbackInfo ci) {
		TMB.onClientStart();
	}
}
