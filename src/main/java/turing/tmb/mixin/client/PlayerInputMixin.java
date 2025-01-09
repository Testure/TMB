package turing.tmb.mixin.client;

import net.minecraft.client.input.PlayerInput;
import net.minecraft.client.option.GameSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turing.tmb.client.TMBRenderer;
import turing.tmb.util.IKeybinds;

@Mixin(value = PlayerInput.class, remap = false)
public class PlayerInputMixin {
	@Shadow
	@Final
	public GameSettings gameSettings;

	@Inject(method = "keyEvent", at = @At("TAIL"))
	public void checkKey(int keyCode, boolean pressed, CallbackInfo ci) {
		if (pressed && keyCode == ((IKeybinds) gameSettings).toomanyblocks$getKeyHideTMB().getKeyCode()) {
			TMBRenderer.show = !TMBRenderer.show;
		}
	}
}
