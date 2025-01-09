package turing.tmb.util;

import net.minecraft.client.option.BooleanOption;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StringOption;

public interface IKeybinds {
	KeyBinding toomanyblocks$getKeyHideTMB();

	BooleanOption toomanyblocks$getIsTMBHidden();

	StringOption toomanyblocks$getLastTMBSearch();
}
