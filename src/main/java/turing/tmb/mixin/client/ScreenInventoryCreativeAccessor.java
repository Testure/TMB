package turing.tmb.mixin.client;

import net.minecraft.client.gui.GuiInventoryCreative;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiInventoryCreative.class, remap = false)
public interface ScreenInventoryCreativeAccessor {
	@Accessor
	GuiTextField getSearchField();
}
