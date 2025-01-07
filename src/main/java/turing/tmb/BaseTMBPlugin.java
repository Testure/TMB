package turing.tmb;

import net.minecraft.client.gui.Screen;
import net.minecraft.core.item.Items;
import turing.tmb.api.ITMBPlugin;
import turing.tmb.api.drawable.gui.IGuiHelper;
import turing.tmb.api.drawable.gui.IGuiProperties;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.client.ScreenTMBRecipe;

public class BaseTMBPlugin implements ITMBPlugin {
	private InfoRecipeCategory infoCategory;

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

	@Override
	public void registerRecipes(ITMBRuntime runtime) {
		runtime.getRecipeIndex().registerRecipe(infoCategory, new IngredientInfo(TypedIngredient.itemStackIngredient(Items.AMMO_ARROW.getDefaultStack()), "Lorem ipsum odor amet, consectetuer adipiscing elit. Maecenas purus duis dapibus ridiculus neque imperdiet praesent. Nec nam faucibus nam dui curae aenean. Montes metus interdum lacus vitae malesuada morbi molestie egestas a. Volutpat etiam at dis ipsum enim felis ultricies. Arcu aliquam massa enim vulputate ligula lectus, bibendum vulputate. Velit amet quis lectus curabitur conubia lobortis. Himenaeos dignissim maximus fusce posuere platea quisque curae congue.", false), InfoRecipeTranslator::new);
	}

	@Override
	public void registerRecipeCategories(ITMBRuntime runtime) {
		infoCategory = runtime.getRecipeIndex().registerCategory(new InfoRecipeCategory());
	}
}
