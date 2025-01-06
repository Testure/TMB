package turing.tmb.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.util.collection.Pair;
import org.lwjgl.opengl.GL11;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.drawable.gui.IGuiHelper;
import turing.tmb.api.drawable.gui.IScreenHandler;
import turing.tmb.api.ingredient.IIngredientRenderer;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.runtime.ITMBRuntime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiHelper implements IGuiHelper {
	private final Minecraft mc = Minecraft.getMinecraft();
	private final ITMBRuntime runtime;
	private final CycleTimer cycleTimer = new CycleTimer(0);
	public static final Map<String, IScreenHandler<?>> extraScreens = new HashMap<>();
	public static final List<String> screenBlacklist = new ArrayList<>();

	public GuiHelper(ITMBRuntime runtime) {
		this.runtime = runtime;
	}

	@Override
	public CycleTimer getCycleTimer() {
		return cycleTimer;
	}

	@Override
	public ITMBRuntime getRuntime() {
		return runtime;
	}

	@Override
	public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int uvWidth, int uvHeight) {
		float uScale = 0.00390625F;
		float vScale = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + (double)0.0F, y + (double)height, 0, (double)((float)(u + 0) * uScale), (double)((float)(v + uvHeight) * vScale));
		tessellator.addVertexWithUV(x + (double)width, y + (double)height, 0, (double)((float)(u + uvWidth) * uScale), (double)((float)(v + uvHeight) * vScale));
		tessellator.addVertexWithUV(x + (double)width, y + (double)0.0F, 0, (double)((float)(u + uvWidth) * uScale), (double)((float)(v + 0) * vScale));
		tessellator.addVertexWithUV(x + (double)0.0F, y + (double)0.0F, 0, (double)((float)(u + 0) * uScale), (double)((float)(v + 0) * vScale));
		tessellator.draw();
	}

	@Override
	public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
		float scale = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, 0, (float)(u) * scale, (float)(v + height) * scale);
		tessellator.addVertexWithUV(x + width, y + height, 0, (float)(u + width) * scale, (float)(v + height) * scale);
		tessellator.addVertexWithUV(x + width, y, 0, (float)(u + width) * scale, (float)(v) * scale);
		tessellator.addVertexWithUV(x, y, 0, (float)(u) * scale, (float)(v) * scale);
		tessellator.draw();
	}

	@Override
	public void drawRect(int minX, int minY, int maxX, int maxY, int color) {
		if (minX < maxX) {
			int temp = minX;
			minX = maxX;
			maxX = temp;
		}

		if (minY < maxY) {
			int temp = minY;
			minY = maxY;
			maxY = temp;
		}

		float a = (float)(color >> 24 & 255) / 255.0F;
		float r = (float)(color >> 16 & 255) / 255.0F;
		float g = (float)(color >> 8 & 255) / 255.0F;
		float b = (float)(color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.instance;
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glColor4f(r, g, b, a);
		tessellator.startDrawingQuads();
		tessellator.addVertex(minX, maxY, 0);
		tessellator.addVertex(maxX, maxY, 0);
		tessellator.addVertex(maxX, minY, 0);
		tessellator.addVertex(minX, minY, 0);
		tessellator.draw();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void drawIngredients(IIngredientList ingredients) {
		ITypedIngredient<?> ingredient = getCycleTimer().getCycledItem(ingredients.getIngredients());
		if (ingredient != null) {
			((IIngredientRenderer<Object>) ingredient.getType().getRenderer(getRuntime())).render(this, ingredient.getIngredient());
		}
	}

	@Override
	public void drawIngredients(ITypedIngredient<?>... ingredients) {
		drawIngredients(new IngredientList(ingredients));
	}

	@Override
	public <T extends Screen> void registerScreen(Class<? extends T> clazz, IScreenHandler<T> handler) {
		extraScreens.put(clazz.getCanonicalName(), handler);
	}

	@Override
	public <T extends Screen> void blacklistScreen(Class<? extends T> clazz) {
		screenBlacklist.add(clazz.getCanonicalName());
	}

	@Override
	public Minecraft getMinecraft() {
		return mc;
	}
}
