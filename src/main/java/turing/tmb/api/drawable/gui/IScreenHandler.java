package turing.tmb.api.drawable.gui;

import net.minecraft.client.gui.GuiScreen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@FunctionalInterface
public interface IScreenHandler<T extends GuiScreen> extends Function<GuiScreen, IGuiProperties> {
	@Override
	@Nullable
	IGuiProperties apply(GuiScreen screen);
}
