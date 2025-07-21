package turing.tmb.mixin.client;

import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.ScreenPause;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turing.tmb.TMB;

@Mixin(value = ScreenPause.class,remap = false)
public class ScreenPauseMixin {

	@Inject(method = "buttonClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;changeWorld(Lnet/minecraft/client/world/WorldClient;)V"))
	protected void buttonClicked(ButtonElement button, CallbackInfo ci) {
		TMB.saveData();
	}

}
