package turing.tmb;

import net.minecraft.client.input.InputDevice;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.OptionBoolean;
import net.minecraft.client.option.OptionString;
import org.lwjgl.input.Keyboard;

public class TMBOptions {

	public static KeyBinding keyHideTMB = new KeyBinding("key.tmb.hide").bind(InputDevice.keyboard, Keyboard.KEY_O);

	public static KeyBinding keyShowRecipeTree = new KeyBinding("key.tmb.showRecipeTree").bind(InputDevice.keyboard, Keyboard.KEY_T);

	public static KeyBinding keySetDefaultRecipe = new KeyBinding("key.tmb.setDefaultRecipe").bind(InputDevice.keyboard, Keyboard.KEY_D);

	public static KeyBinding keyAddFavourite = new KeyBinding("key.tmb.keyAddFavourite").bind(InputDevice.keyboard, Keyboard.KEY_A);

	public static KeyBinding keyFillRecipe = new KeyBinding("key.tmb.keyFillRecipe").bind(InputDevice.keyboard, Keyboard.KEY_F);

	public static OptionBoolean isTMBHidden = new OptionBoolean("isTMBHidden", false);

	public static OptionBoolean isRecipeViewEnabled = new OptionBoolean("isRecipeViewEnabled", true);

	public static OptionString lastTMBSearch = new OptionString("lastTMBSearch", "");

}
