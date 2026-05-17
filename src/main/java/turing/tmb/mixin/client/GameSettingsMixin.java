package turing.tmb.mixin.client;

import net.minecraft.client.option.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turing.tmb.TMBOptions;
import turing.tmb.client.TMBRenderer;

@Mixin(value = GameSettings.class, remap = false)
public class GameSettingsMixin {
	@Inject(method = "saveOptions", at = @At("HEAD"))
	private static void saveTMBState(CallbackInfo ci) {
		if (TMBRenderer.search != null) {
			TMBOptions.isTMBHidden.set(!TMBRenderer.show);
			TMBOptions.lastTMBSearch.set(TMBRenderer.search.getText());
		}
	}
}
