package turing.tmb.mixin.client;

import net.minecraft.client.gui.TextFieldElement;
import net.minecraft.client.gui.container.ScreenInventoryCreative;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ScreenInventoryCreative.class, remap = false)
public interface ScreenInventoryCreativeAccessor {
	@Accessor
	TextFieldElement getSearchField();
}
