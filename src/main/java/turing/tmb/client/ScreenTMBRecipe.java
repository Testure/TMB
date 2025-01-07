package turing.tmb.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTooltip;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.collection.Pair;
import net.minecraft.core.util.helper.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import turing.tmb.TMB;
import turing.tmb.TooltipBuilder;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.drawable.builder.ITooltipBuilder;
import turing.tmb.api.ingredient.IIngredientRenderer;
import turing.tmb.api.ingredient.IIngredientType;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.*;
import turing.tmb.util.IngredientList;
import turing.tmb.util.RenderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class ScreenTMBRecipe extends GuiScreen {
	private int xSize = 180;
	private int ySize = 180;
	private final GuiTooltip tooltipElement = new GuiTooltip(Minecraft.getMinecraft(this.getClass()));
	private ILookupContext lookupContext = null;
	private List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> recipeList = null;
	private int selectedTab = 0;
	private int currentRecipePage = 0;
	private int recipesPerPage = 0;
	private int recipePages = 0;
	private int tabPages = 0;
	private int currentTabPage = 0;
	private int tabsPerPage = 7;
	private final List<IRecipeCategory<?>> tabList = new ArrayList<>();
	private final List<Pair<ITypedIngredient<?>, Pair<Integer, Integer>>> drawnIngredients = new ArrayList<>();
	private final GuiButton rightButton = new GuiButton(0, 0, 0, ">");
	private final GuiButton leftButton = new GuiButton(1, 0, 0, "<");
	private final GuiButton tabLeftButton = new GuiButton(2, 0, 0, "<");
	private final GuiButton tabRightButton = new GuiButton(3, 0, 0, ">");
	private String tooltip = "";

	public ScreenTMBRecipe(GuiScreen parent) {
		super(parent);
		rightButton.width = 12;
		rightButton.height = 12;
		leftButton.width = 12;
		leftButton.height = 12;
		tabLeftButton.width = 16;
		tabLeftButton.height = 16;
		tabRightButton.width = 16;
		tabRightButton.height = 16;
		leftButton.setListener(this::recipePageLeft);
		rightButton.setListener(this::recipePageRight);
		tabLeftButton.setListener(this::tabPageLeft);
		tabRightButton.setListener(this::tabPageRight);
	}

	public static void show(List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> recipeList, ILookupContext context, @Nullable GuiScreen lastScreen) {
		Minecraft mc = Minecraft.getMinecraft(ScreenTMBRecipe.class);
		ScreenTMBRecipe screen = new ScreenTMBRecipe(lastScreen != null ? lastScreen : mc.currentScreen);
		mc.displayGuiScreen(screen);
		screen.showRecipes(recipeList, context);
	}

	private void showRecipes(List<Pair<IRecipeCategory<?>, IRecipeTranslator<?>>> recipeList, ILookupContext context) {
		this.recipeList = recipeList;
		this.lookupContext = context;
		this.tabList.clear();

		for (Pair<IRecipeCategory<?>, IRecipeTranslator<?>> pair : recipeList) {
			if (!tabList.contains(pair.getLeft())) {
				tabList.add(pair.getLeft());
			}
		}

		tabPages = (int) Math.ceil((double) tabList.size() / tabsPerPage);

		changeTab(0);
	}

	@Override
	public void init() {
		super.init();
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onClosed() {
		super.onClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean pausesGame() {
		return false;
	}

	@Override
	public void mouseClicked(int mx, int my, int buttonNum) {
		super.mouseClicked(mx, my, buttonNum);
		if (buttonNum == 0) {
			if (mx >= (this.width - this.xSize) / 2 && mx < ((this.width - this.xSize) / 2) + this.xSize) {
				if (my >= (this.height - this.ySize) / 2 && my < ((this.height - this.ySize) / 2) + 12) {
					TMB.getRuntime().showAllRecipes();
					return;
				}
			}

			if (recipePages > 1) {
				buttonClick(leftButton, mx, my);
				buttonClick(rightButton, mx, my);
			}
			if (tabPages > 1) {
				buttonClick(tabLeftButton, mx, my);
				buttonClick(tabRightButton, mx, my);
			}

			for (int i = 0; i < tabsPerPage; i++) {
				if ((this.currentTabPage * this.tabsPerPage + i) >= tabList.size()) break;
				int x = ((this.width - this.xSize) / 2) + 24 * i + 6;
				int y = ((this.height - this.ySize) / 2) - 21;
				if (i != selectedTab) y += 2;

				if (mx >= x && mx < x + 24 && my >= y && my < y + 24) {
					if (i != selectedTab) {
						mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
						changeTab(i);
						break;
					}
				}
			}
		}
	}

	private void buttonClick(GuiButton button, int mx, int my) {
		if (button.mouseClicked(mc, mx, my)) {
			button.listener.listen(button);
			mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
		}
	}

	private void recipePageLeft(GuiButton b) {
		currentRecipePage = Math.max(currentRecipePage - 1, 0);
	}

	private void recipePageRight(GuiButton b) {
		currentRecipePage = Math.min(currentRecipePage + 1, recipePages - 1);
	}

	private void tabPageLeft(GuiButton b) {
		currentTabPage = Math.max(currentTabPage - 1, 0);
		changeTab(selectedTab);
	}

	private void tabPageRight(GuiButton b) {
		currentTabPage = Math.min(currentTabPage + 1, tabPages - 1);
		changeTab(selectedTab);
	}

	private void changeTab(int tab) {
		this.selectedTab = tab;
		this.currentRecipePage = 0;
		int actualIndex = this.currentTabPage * this.tabsPerPage + tab;
		if (actualIndex >= tabList.size()) {
			actualIndex = tabList.size() - 1;
			selectedTab = actualIndex - (this.currentTabPage * this.tabsPerPage);
		}
		this.recipesPerPage = howManyRecipesCanIFit(tabList.get(actualIndex));

		int recipeCount = 0;
		for (Pair<IRecipeCategory<?>, IRecipeTranslator<?>> pair : recipeList) {
			if (pair.getLeft().hashCode() == tabList.get(actualIndex).hashCode()) {
				recipeCount++;
			}
		}
		this.recipePages = (int) Math.ceil((double) recipeCount / this.recipesPerPage);
	}

	private int howManyRecipesCanIFit(IRecipeCategory<?> category) {
		return (ySize - 18) / (category.getBackground().getHeight() + 4);
	}

	private void scroll(int direction) {
		int change = MathHelper.clamp(-direction, -1, 1);
		int mul = 1;

		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
			mul += 10;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			mul += 100;
		}

		change *= mul;

		currentRecipePage += change;
		if (currentRecipePage < 0) currentRecipePage = 0;
		if (currentRecipePage >= recipePages) currentRecipePage = recipePages - 1;
	}

	@Override
	public void drawScreen(int mx, int my, float partialTick) {
		drawWorldBackground();
		GL11.glEnable(3042);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		TMB.getRuntime().getGuiHelper().getCycleTimer().onDraw();
		drawnIngredients.clear();

		tooltip = "";
		TooltipBuilder tooltipBuilder = new TooltipBuilder();
		boolean isCtrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
		boolean isShift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

		GL11.glPushMatrix();
		for (int i = 0; i < tabsPerPage; i++) {
			int index = this.currentTabPage * this.tabsPerPage + i;
			if (index >= tabList.size()) break;
			if (i != selectedTab) {
				renderTab(i, mx, my, false, tabList.get(index));
			}
		}
		GL11.glPopMatrix();

		int texture = this.mc.renderEngine.getTexture("/assets/minecraft/textures/gui/guidebook/guidebook.png");
		this.mc.renderEngine.bindTexture(texture);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, (double) y, 0, 0, this.xSize, this.ySize, 158, 220);

		GL11.glPushMatrix();

		if (mx >= x && my >= y && mx < x + this.xSize && my < y + this.ySize) {
			scroll(Mouse.getDWheel());
		}

		int in = this.currentTabPage * this.tabsPerPage + selectedTab;
		if (tabList.size() > in) {
			IRecipeCategory<IRecipeTranslator<Object>> category = (IRecipeCategory<IRecipeTranslator<Object>>) tabList.get(in);
			renderTab(selectedTab, mx, my, true, category);
			renderCatalysts(category, mx, my, tooltipBuilder, isCtrl, isShift);
			int X = x + 4;
			int Y = y + 14;
			GL11.glPushMatrix();
			GL11.glTranslatef(x + 4, y + 14, 0);
			IRecipeLayout layout = category.getRecipeLayout();
			List<IRecipeTranslator<?>> recipes = recipeList.stream().filter((p) -> p.getLeft().hashCode() == category.hashCode()).map(Pair::getRight).collect(Collectors.toList());
			for (int i = 0; i < this.recipesPerPage; i++) {
				List<IIngredientList> ingredients = new ArrayList<>();
				int index = this.currentRecipePage * this.recipesPerPage + i;
				if (index >= recipes.size()) break;
				IRecipeTranslator<Object> recipe = (IRecipeTranslator<Object>) recipes.get(index);
				category.drawRecipe(TMB.getRuntime(), recipe, layout, ingredients, lookupContext);
				for (int I = 0; I < layout.getSlots().size(); I++) {
					IRecipeSlot<Object, ?> slot = (IRecipeSlot<Object, ?>) layout.getSlots().get(I);
					GL11.glPushMatrix();
					GL11.glTranslatef(slot.getX(), slot.getY(), 0);
					X += slot.getX();
					Y += slot.getY();
					slot.draw(TMB.getRuntime().getGuiHelper());
					if (ingredients.size() > I) {
						GL11.glTranslatef(1, 1, 0);
						X++;
						Y++;
						IIngredientList list = ingredients.get(I);
						ITypedIngredient<?> ingredient = TMB.getRuntime().getGuiHelper().getCycleTimer().getCycledItem(list.getIngredients());
						if (lookupContext != null && lookupContext.getRole() != RecipeIngredientRole.OUTPUT) {
							if (list.getIngredients().stream().anyMatch((ing) -> ing.hashCode() == lookupContext.getIngredient().hashCode())) {
								ingredient = lookupContext.getIngredient();
							}
						}
						if (ingredient != null) {
							IIngredientType<?> type = ingredient.getType();
							IIngredientRenderer<Object> renderer = (IIngredientRenderer<Object>) type.getRenderer(TMB.getRuntime());
							new DrawableIngredient<>(ingredient.getIngredient(), renderer).draw(TMB.getRuntime().getGuiHelper());
							drawnIngredients.add(Pair.of(ingredient, Pair.of(X, Y)));
							if (mx >= X && mx < X + 18 && my >= Y && my < Y + 18) {
								int mouseX = mx - ((this.width - this.xSize) / 2) - 4;
								int mouseY = my - ((this.height - this.ySize) / 2) - 14 - ((category.getBackground().getHeight() + 4) * i);
								renderer.getTooltip(tooltipBuilder, ingredient.getIngredient(), isCtrl, isShift);
								slot.getTooltips(tooltipBuilder, ingredient.getIngredient(), mouseX, mouseY, isCtrl, isShift);
								tooltipBuilder.addAll(category.getTooltips(recipe, mouseX, mouseY));
								if (list instanceof IngredientList && ((IngredientList) list).itemGroup != null) {
									tooltipBuilder.add(TextFormatting.formatted(I18n.getInstance().translateKeyAndFormat("tmb.tooltip.itemGroup", ((IngredientList) list).itemGroup), TextFormatting.LIGHT_GRAY));
								}
							}
						}
						X--;
						Y--;
					}
					X -= slot.getX();
					Y -= slot.getY();
					GL11.glPopMatrix();
				}
				GL11.glTranslatef(0, category.getBackground().getHeight() + 4, 0);
				Y += category.getBackground().getHeight() + 4;
			}
			GL11.glPopMatrix();

			GL11.glTranslatef(x, y + 4, 0);
			String text = I18n.getInstance().translateKey(category.getName());
			int textX = ((xSize / 2) - mc.fontRenderer.getStringWidth(text) / 2);
			mc.fontRenderer.renderString(text, textX + 1, 1, 0xFFFFFF, true);
			mc.fontRenderer.renderString(text, textX, 0, 0xFFFFFF, false);
		}
		GL11.glPopMatrix();

		if (!tooltipBuilder.getLines().isEmpty()) {
			StringBuilder builder = new StringBuilder();
			tooltipBuilder.getLines().forEach((s) -> builder.append(s).append("\n"));
			tooltip = builder.toString();
		}

		if (recipePages > 1) {
			leftButton.xPosition = x + 6;
			leftButton.yPosition = y + this.ySize - 18;
			rightButton.xPosition = x + this.xSize - 18;
			rightButton.yPosition = y + this.ySize - 18;
			rightButton.drawButton(mc, mx, my);
			leftButton.drawButton(mc, mx, my);

			String pageText = "Page " + (currentRecipePage + 1) + "/" + recipePages;
			mc.fontRenderer.renderString(pageText, (((this.width - this.xSize) / 2) + (this.xSize / 2)) - mc.fontRenderer.getStringWidth(pageText) / 2, ((this.height - this.ySize) / 2) + this.ySize - 16, 0xFFFFFF, false);
		}

		if (tabPages > 1) {
			tabRightButton.yPosition = y - 17;
			tabLeftButton.yPosition = y - 17;
			tabLeftButton.xPosition = x - 16;
			tabRightButton.xPosition = x + this.xSize;

			tabLeftButton.drawButton(mc, mx, my);
			tabRightButton.drawButton(mc, mx, my);
		}

		for (Pair<ITypedIngredient<?>, Pair<Integer, Integer>> drawn : drawnIngredients) {
			if (mx >= drawn.getRight().getLeft() && my >= drawn.getRight().getRight() && mx < drawn.getRight().getLeft() + 16 && my < drawn.getRight().getRight() + 16) {
				RenderUtil.renderItemSelected(TMB.getRuntime().getGuiHelper(), drawn.getRight().getLeft(), drawn.getRight().getRight());
			}
		}

		if (!tooltip.isEmpty()) {
			GL11.glPushMatrix();
			tooltipElement.render(tooltip, mx, my, 8, -8);
			GL11.glPopMatrix();
		}
	}

	@SuppressWarnings("unchecked")
	private void renderCatalysts(IRecipeCategory<?> category, int mx, int my, ITooltipBuilder tooltipBuilder, boolean isCtrl, boolean isShift) {
		List<ITypedIngredient<?>> catalysts = TMB.getRuntime().getRecipeIndex().getCatalystsForCategory(category);

		GL11.glEnable(3042);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		for (int i = 0; i < catalysts.size(); i++) {
			int texture = this.mc.renderEngine.getTexture("/assets/tmb/textures/gui/gui_vanilla.png");
			this.mc.renderEngine.bindTexture(texture);

			ITypedIngredient<Object> ingredient = (ITypedIngredient<Object>) catalysts.get(i);
			int x = ((this.width - this.xSize) / 2) - 20;
			int y = ((this.height - this.ySize) / 2) + (4 + (26 * i));

			this.drawTexturedModalRect(x, y, 0, 0, 22, 22);

			new DrawableIngredient<>(ingredient.getIngredient(), ingredient.getType().getRenderer(TMB.getRuntime())).draw(TMB.getRuntime().getGuiHelper(), x + 3, y + 3);
			drawnIngredients.add(Pair.of(ingredient, Pair.of(x + 3, y + 3)));

			if (mx >= x && mx < x + 22 && my >= y && my < y + 22) {
				ingredient.getType().getRenderer(TMB.getRuntime()).getTooltip(tooltipBuilder, ingredient.getIngredient(), isCtrl, isShift);
			}
		}
	}

	private void renderTab(int x, int mx, int my, boolean selected, IRecipeCategory<?> category) {
		GL11.glEnable(3042);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int texture = this.mc.renderEngine.getTexture("/assets/tmb/textures/gui/tab_" + (!selected ? "un" : "") + "selected.png");
		this.mc.renderEngine.bindTexture(texture);
		int y = ((this.height - this.ySize) / 2) - 21;
		if (!selected) y += 2;
		x = ((this.width - this.xSize) / 2) + 24 * x;
		this.drawTexturedModalRect(x + 6, (double) y, 0, 0, 24, 24, 256, 256);
		if (category.getIcon() != null) {
			category.getIcon().draw(TMB.getRuntime().getGuiHelper(), x + 10, y + 4);
		}
		if (mx >= x && my >= y && mx < x + 24 && my < y + 24) {
			tooltip = I18n.getInstance().translateKey(category.getName()) + "\n" + TextFormatting.formatted(category.getNamespace(), TextFormatting.BLUE);
		}
	}

	@Override
	public void keyTyped(char eventCharacter, int eventKey, int mx, int my) {
		if (eventKey == Keyboard.KEY_BACK) {
			mc.displayGuiScreen(getParentScreen());
		} else if (eventKey == Keyboard.KEY_ESCAPE || eventKey == mc.gameSettings.keyInventory.getKeyCode()) {
			GuiScreen s = getParentScreen();
			while (s instanceof ScreenTMBRecipe) {
				s = s.getParentScreen();
			}
			mc.displayGuiScreen(s);
		}
		if (eventKey == mc.gameSettings.keyShowRecipe.getKeyCode() || eventKey == mc.gameSettings.keyShowUsage.getKeyCode()) {
			for (Pair<ITypedIngredient<?>, Pair<Integer, Integer>> drawn : drawnIngredients) {
				if (mx >= drawn.getRight().getLeft() && my >= drawn.getRight().getRight() && mx < drawn.getRight().getLeft() + 16 && my < drawn.getRight().getRight() + 16) {
					if (eventKey == mc.gameSettings.keyShowUsage.getKeyCode()) {
						TMB.getRuntime().showRecipe(drawn.getLeft(), RecipeIngredientRole.INPUT);
					} else {
						TMB.getRuntime().showRecipe(drawn.getLeft(), RecipeIngredientRole.OUTPUT);
					}
					break;
				}
			}
		}
	}
}
