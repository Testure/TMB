package turing.tmb.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import turing.tmb.SearchQuery;
import turing.tmb.TMB;
import turing.tmb.TooltipBuilder;
import turing.tmb.api.ISearchQuery;
import turing.tmb.api.drawable.gui.IGuiProperties;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.RecipeIngredientRole;
import turing.tmb.api.runtime.IIngredientIndex;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.util.IKeybinds;
import turing.tmb.util.RenderUtil;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class TMBRenderer {
	public static int currentPage = 0;
	public static int pages = 0;
	public static boolean show = true;
	protected static boolean initialized;
	protected static final GuiButton leftButton = new GuiButton(0, 0, 0, 16, 16, "<");
	protected static final GuiButton rightButton = new GuiButton(1, 0, 0, 16, 16, ">");
	public static GuiTextField search;
	protected static GuiTooltip tooltip;

	public static void init(Minecraft mc) {
		initialized = true;

		tooltip = new GuiTooltip(mc);
		search = new GuiTextField(null, Minecraft.getMinecraft(TMBRenderer.class).fontRenderer, 0, 0, 120, 20, ((IKeybinds) Minecraft.getMinecraft(TMBRenderer.class).gameSettings).toomanyblocks$getLastTMBSearch().value, "Search...");
		show = !((IKeybinds) Minecraft.getMinecraft(TMBRenderer.class).gameSettings).toomanyblocks$getIsTMBHidden().value;
	}

	public static void renderHeader(int mouseX, int mouseY, int width, int height, Minecraft mc, float pt, @Nullable IGuiProperties properties) {
		if (!show || !initialized) return;
		int w = (int) (width / 3.5F);
		GuiScreen currentScreen = mc.currentScreen;
		if (currentScreen instanceof GuiContainer) {
			w = Math.min(((width / 2) - ((GuiContainer) (currentScreen)).xSize / 2) - 16, w);
		} else if (properties != null) {
			w = Math.min(((width / 2) - properties.guiXSize() / 2) - 16, w);
		}
		int startX = width - w;
		int startY = 4;
		String str = "Page " + (currentPage + 1) + " / " + (pages + 1);

		leftButton.xPosition = startX;
		leftButton.yPosition = startY;
		rightButton.xPosition = Math.min(startX + (18 * (w / 18)), width - 18);
		rightButton.yPosition = startY;
		mc.fontRenderer.renderString(str, ((((leftButton.xPosition + leftButton.width) + (rightButton.xPosition + rightButton.width)) / 2) - mc.fontRenderer.getStringWidth(str) / 2) - 4, startY + 4, 0xFFFFFF, false);
		leftButton.drawButton(mc, mouseX, mouseY);
		rightButton.drawButton(mc, mouseX, mouseY);

		search.yPosition = height - search.height - 4;
		search.xPosition = width / 2 - search.width / 2;
		search.drawTextBox();
	}

	public static void onTick() {
		if (!show) return;
		if (search.isFocused) {
			search.updateCursorCounter();
		}
		scroll(Mouse.getDWheel());
	}

	public static void mouseClicked(int mouseX, int mouseY, int width, int height, Minecraft mc) {
		if (!show) return;
		boolean left = leftButton.mouseClicked(mc, mouseX, mouseY);
		boolean right = rightButton.mouseClicked(mc, mouseX, mouseY);
		if (left || right) {
			mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
			int change = 1;
			if (left) {
				change = -1;
			}
			currentPage += change;
			if (currentPage < 0) currentPage = pages;
			if (currentPage > pages) currentPage = 0;
		}
		search.mouseClicked(mouseX, mouseY, 1);
	}

	public static void keyTyped(char c, int key, int mouseX, int mouseY) {
		if (search.isFocused && show) {
			search.textboxKeyTyped(c, key);
		}
	}

	private static void scroll(int direction) {
		int change = MathHelper.clamp(-direction, -1, 1);
		currentPage += change;
		if (currentPage < 0) currentPage = pages;
		if (currentPage > pages) currentPage = 0;
	}

	@SuppressWarnings("unchecked")
	public static void renderItems(int mouseX, int mouseY, int width, int height, Minecraft mc, float pt, @Nullable IGuiProperties properties) {
		if (!initialized) {
			init(mc);
			return;
		}
		if (!show) {
			return;
		}

		GuiScreen currentScreen = mc.currentScreen;
		ITMBRuntime runtime = TMB.getRuntime();
		Collection<ITypedIngredient<?>> toDisplay = getToDisplay(runtime);

		int startX = (int) (width / 3.5F);

		if (currentScreen instanceof GuiContainer) {
			startX = Math.min(((width / 2) - ((GuiContainer) (currentScreen)).xSize / 2) - 18, startX);
		} else if (properties != null) {
			startX = Math.min(((width / 2) - properties.guiXSize() / 2) - 18, startX);
		}

		int itemsX = (startX / 18);
		int itemsY = Math.min((height / 18) - 1, ((height - 28) / 18));
		int xOffset = 0;
		int yOffset = 1;

		int itemsPerPage = itemsX * itemsY;
		if (itemsPerPage <= 0) return;
		pages = toDisplay.size() / itemsPerPage;

		if (currentPage > pages) currentPage = pages;

		List<ITypedIngredient<?>> pageList = toDisplay.stream().skip((long) itemsPerPage * currentPage).limit(itemsPerPage).collect(Collectors.toList());

		ITypedIngredient<?> hoveredItem = null;
		TooltipBuilder tooltipBuilder = new TooltipBuilder();
		boolean isCtrl = Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
		boolean isShift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

		int i = 0;
		loop: for (int y = 0; y < itemsY; y++) {
			for (int x = 0; x < itemsX; x++) {
				if (i >= pageList.size()) break loop;
				ITypedIngredient<Object> ingredient = (ITypedIngredient<Object>) pageList.get(i);
				int xOff = width - startX + (xOffset * 16) + (2 * xOffset);
				int yOff = 8 + (yOffset * 16) + (2 * yOffset);

				ingredient.getType().getRenderer(runtime).render(runtime.getGuiHelper(), ingredient.getIngredient(), xOff, yOff);

				if (mouseX >= xOff && mouseX < xOff + 16 && mouseY >= yOff && mouseY < yOff + 16) {
					hoveredItem = ingredient;
					ingredient.getType().getRenderer(runtime).getTooltip(tooltipBuilder, hoveredItem.getIngredient(), isCtrl, isShift);
					RenderUtil.renderItemSelected(runtime.getGuiHelper(), xOff, yOff);
				}

				i++;
				xOffset++;
				if (xOffset >= itemsX) {
					xOffset = 0;
				}
			}
			yOffset++;
		}

		if (hoveredItem != null) {
			if (!tooltipBuilder.getLines().isEmpty()) {
				StringBuilder builder = new StringBuilder();
				tooltipBuilder.getLines().forEach(str -> builder.append(str).append("\n"));
				GL11.glPushMatrix();
				tooltip.render(builder.toString(), mouseX, mouseY, 8, -8);
				GL11.glPopMatrix();
			}

			if (mc.gameSettings.keyShowRecipe.isPressed()) {
				runtime.showRecipe(hoveredItem, RecipeIngredientRole.OUTPUT);
			} else if (mc.gameSettings.keyShowUsage.isPressed()) {
				runtime.showRecipe(hoveredItem, RecipeIngredientRole.INPUT);
			}
		}
	}

	private static Collection<ITypedIngredient<?>> getToDisplay(ITMBRuntime runtime) {
		IIngredientIndex index = runtime.getIngredientIndex();

		String s = search.getText();
		String modid = "";
		String text;

		if (s.contains("@") && !s.substring(s.indexOf("@") + 1).isEmpty()) {
			String modSearch = s.substring(s.indexOf("@") + 1).toLowerCase();
			if (modSearch.contains(" ")) modSearch = modSearch.substring(0, modSearch.indexOf(" "));
			s = s.replace(" @" + modSearch, "").replace("@" + modSearch, "");
			if (!s.isEmpty() && s.charAt(0) == ' ') s = s.substring(1);
			modid = modSearch;
		}

		text = s;
		ISearchQuery query = null;

		if (!modid.isEmpty()) {
			query = new SearchQuery(modid, text);
		} else if (!text.isEmpty()) {
			query = SearchQuery.textSearch(text);
		}

		Collection<ITypedIngredient<?>> toDisplay;

		if (query != null) {
			toDisplay = index.getFilteredIngredients(query);
		} else {
			toDisplay = index.getAllVisibleIngredients();
		}
		return toDisplay;
	}
}
