package turing.tmb.util;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.OptionBoolean;
import net.minecraft.client.option.OptionString;

public interface IKeybinds {
	KeyBinding toomanyblocks$getKeyHideTMB();

	OptionBoolean toomanyblocks$getIsTMBHidden();

	OptionString toomanyblocks$getLastTMBSearch();
}
