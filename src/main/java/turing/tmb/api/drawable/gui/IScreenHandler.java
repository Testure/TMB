package turing.tmb.api.drawable.gui;

import net.minecraft.client.gui.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@FunctionalInterface
public interface IScreenHandler<T extends Screen> extends Function<Screen, IGuiProperties> {
	@Override
	@Nullable
	IGuiProperties apply(Screen screen);
}
