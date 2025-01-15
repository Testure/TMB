package turing.tmb.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.gui.GuiInventoryCreative;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turing.tmb.TMB;
import turing.tmb.client.TMBRenderer;
import turing.tmb.util.IKeybinds;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
	@Shadow
	public GameSettings gameSettings;

	@Shadow
	public GuiScreen currentScreen;

	@Unique
	private static int debounce = 0;

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/window/GameWindow;updateWindowState()V", shift = At.Shift.BY))
	public void onStart(CallbackInfo ci) {
		TMB.onClientStart();
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;next()Z", shift = At.Shift.AFTER))
	public void checkKeybind(CallbackInfo ci) {
		if (debounce > 0) debounce--;
		if (debounce <= 0) {
			if (((IKeybinds) gameSettings).toomanyblocks$getKeyHideTMB().isPressed()) {
				if (!TMBRenderer.search.isFocused) {
					if (currentScreen instanceof GuiInventoryCreative) {
						if (((ScreenInventoryCreativeAccessor) currentScreen).getSearchField().isFocused) {
							return;
						}
					}
					debounce = 10;
					TMBRenderer.show = !TMBRenderer.show;
				}
			}
		}
	}
}
