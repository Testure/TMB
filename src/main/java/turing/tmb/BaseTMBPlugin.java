package turing.tmb;

import net.minecraft.client.gui.Screen;
import turing.tmb.api.ITMBPlugin;
import turing.tmb.api.drawable.gui.IGuiHelper;
import turing.tmb.api.drawable.gui.IGuiProperties;
import turing.tmb.client.ScreenTMBRecipe;

public class BaseTMBPlugin implements ITMBPlugin {
	@Override
	public void registerExtraScreens(IGuiHelper helper) {
		helper.registerScreen(ScreenTMBRecipe.class, (s) -> new IGuiProperties() {
			@Override
			public Class<? extends Screen> screenClass() {
				return ScreenTMBRecipe.class;
			}

			@Override
			public int guiLeft() {
				return 0;
			}

			@Override
			public int guiTop() {
				return 0;
			}

			@Override
			public int guiXSize() {
				return 250;
			}

			@Override
			public int guiYSize() {
				return 180;
			}

			@Override
			public int screenWidth() {
				return 250;
			}

			@Override
			public int screenHeight() {
				return 180;
			}
		});
	}
}
