package turing.tmb.mixin.client;

import net.minecraft.client.gui.GuiInventoryCreative;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turing.tmb.client.TMBRenderer;

@Mixin(value = GuiInventoryCreative.class, remap = false)
public class ScreenInventoryCreativeMixin {
	@Shadow
	protected GuiTextField searchField;

	@Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
	public void fixTMBSearch(char eventCharacter, int eventKey, int mx, int my, CallbackInfo ci) {
		if (eventCharacter == 't' && !searchField.isFocused && TMBRenderer.search.isFocused) {
			ci.cancel();
			TMBRenderer.keyTyped(eventCharacter, eventKey, mx, my);
		}
	}
}
