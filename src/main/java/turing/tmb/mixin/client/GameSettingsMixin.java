package turing.tmb.mixin.client;

import net.minecraft.client.input.InputDevice;
import net.minecraft.client.option.BooleanOption;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StringOption;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turing.tmb.client.TMBRenderer;
import turing.tmb.util.IKeybinds;

@Mixin(value = GameSettings.class, remap = false)
public class GameSettingsMixin implements IKeybinds {
	@Unique
	public KeyBinding keyHideTMB = new KeyBinding("key.tmb.hide").bind(InputDevice.keyboard, Keyboard.KEY_O);

	@Unique
	public BooleanOption isTMBHidden = new BooleanOption((GameSettings) ((Object) this), "isTMBHidden", false);

	@Unique
	public StringOption lastTMBSearch = new StringOption((GameSettings) ((Object) this), "lastTMBSearch", "");

	@Override
	public KeyBinding toomanyblocks$getKeyHideTMB() {
		return keyHideTMB;
	}

	@Override
	public BooleanOption toomanyblocks$getIsTMBHidden() {
		return isTMBHidden;
	}

	@Override
	public StringOption toomanyblocks$getLastTMBSearch() {
		return lastTMBSearch;
	}

	@Inject(method = "saveOptions", at = @At("HEAD"))
	public void saveTMBState(CallbackInfo ci) {
		if (TMBRenderer.search != null) {
			isTMBHidden.set(!TMBRenderer.show);
			lastTMBSearch.set(TMBRenderer.search.getText());
		}
	}
}
