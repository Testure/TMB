package turing.tmb;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import turing.tmb.api.ITMBPlugin;
import turing.tmb.api.drawable.gui.IGuiHelper;
import turing.tmb.api.drawable.gui.IGuiProperties;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.client.ScreenTMBRecipe;

public class BaseTMBPlugin implements ITMBPlugin {
	public static InfoRecipeCategory infoCategory;

	@Override
	public void registerExtraScreens(IGuiHelper helper) {
		helper.registerScreen(ScreenTMBRecipe.class, (s) -> new IGuiProperties() {
			@Override
			public Class<? extends GuiScreen> screenClass() {
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

	@Override
	public void registerRecipeCategories(ITMBRuntime runtime) {
		infoCategory = runtime.getRecipeIndex().registerCategory(new InfoRecipeCategory());
	}
}
