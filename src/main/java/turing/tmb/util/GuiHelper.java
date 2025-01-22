package turing.tmb.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.render.Font;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.meta.gui.GuiTextureProperties;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.util.helper.MathHelper;
import org.lwjgl.opengl.GL11;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.drawable.gui.IGuiHelper;
import turing.tmb.api.drawable.gui.IScreenHandler;
import turing.tmb.api.ingredient.IIngredientRenderer;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.runtime.ITMBRuntime;

import java.util.*;

public class GuiHelper implements IGuiHelper {
	private final Minecraft mc = Minecraft.getMinecraft();
	private final ITMBRuntime runtime;
	private final CycleTimer cycleTimer = new CycleTimer(0);
	public static final Map<String, IScreenHandler<?>> extraScreens = new HashMap<>();
	public static final List<String> screenBlacklist = new ArrayList<>();

	public GuiHelper(ITMBRuntime runtime) {
		this.runtime = runtime;
	}

	public void clear() {
		extraScreens.clear();
		screenBlacklist.clear();
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
	public void drawGuiIcon(int x, int y, int width, int height, IconCoordinate coordinate) {
		drawGuiIconDouble(x, y, width, height, coordinate);
	}

	public void drawGuiIconDouble(double x, double y, double width, double height, IconCoordinate coordinate) {
		coordinate.parentAtlas.bind();
		Tessellator t = Tessellator.instance;
		if (coordinate.hasMeta("gui")) {
			GuiTextureProperties properties = Objects.requireNonNull(coordinate.getMeta("gui", GuiTextureProperties.class));
			double uScale;
			double vScale;
			double remainderY;
			double remainderX;
			int repeatsX;
			int repeatsY;
			switch (properties.type) {
				case "stretch":
					t.startDrawingQuads();
					t.addVertexWithUV(x, y + height, 0, coordinate.getIconUMin(), coordinate.getIconVMax());
					t.addVertexWithUV(x + width, y + height, 0, coordinate.getIconUMax(), coordinate.getIconVMax());
					t.addVertexWithUV(x + width, y, 0, coordinate.getIconUMax(), coordinate.getIconVMin());
					t.addVertexWithUV(x, y, 0, coordinate.getIconUMin(), coordinate.getIconVMin());
					t.draw();
					break;
				case "tile":
					repeatsY = MathHelper.floor(height / (double)properties.height);
					remainderY = height % (double)properties.height;
					repeatsX = MathHelper.floor(width / (double)properties.width);
					remainderX = width % (double)properties.width;
					uScale = (double)coordinate.width / (double)properties.width;
					vScale = (double)coordinate.height / (double)properties.height;

					for(int i = 0; i < repeatsX; ++i) {
						for(int j = 0; j < repeatsY; ++j) {
							this.drawIconTextureDouble(x + (double)(properties.width * i), y + (double)(properties.height * j), x + (double)(properties.width * (i + 1)), y + (double)(properties.height * (j + 1)), (double)0.0F, (double)0.0F, (double)properties.width * uScale, (double)properties.height * vScale, coordinate);
						}
					}

					for(int j = 0; j < repeatsY; ++j) {
						this.drawIconTextureDouble(x + (double)(properties.width * repeatsX), y + (double)(properties.height * j), x + (double)(properties.width * repeatsX) + remainderX, y + (double)(properties.height * (j + 1)), (double)0.0F, (double)0.0F, remainderX * uScale, (double)properties.height * vScale, coordinate);
					}

					for(int i = 0; i < repeatsX; ++i) {
						this.drawIconTextureDouble(x + (double)(properties.width * i), y + (double)(properties.height * repeatsY), x + (double)(properties.width * (i + 1)), y + (double)(properties.height * repeatsY) + remainderY, (double)0.0F, (double)0.0F, (double)properties.width * uScale, remainderY * vScale, coordinate);
					}

					this.drawIconTextureDouble(x + (double)(properties.width * repeatsX), y + (double)(properties.height * repeatsY), x + (double)(properties.width * repeatsX) + remainderX, y + (double)(properties.height * repeatsY) + remainderY, (double)0.0F, (double)0.0F, remainderX * uScale, remainderY * vScale, coordinate);
					break;
				case "nine_slice":
					int innerWidth = properties.width - (properties.border.left + properties.border.right);
					int innerHeight = properties.height - (properties.border.top + properties.border.bottom);
					int innerLeft = properties.border.left;
					int innerTop = properties.border.top;
					int innerRight = properties.width - properties.border.right;
					int innerBottom = properties.height - properties.border.bottom;
					uScale = (double)coordinate.width / (double)properties.width;
					vScale = (double)coordinate.height / (double)properties.height;
					double leftHeight = height - (double)(properties.border.top + properties.border.bottom);
					repeatsY = MathHelper.floor(leftHeight / (double)innerHeight);
					remainderY = leftHeight % (double)innerHeight;
					double topWidth = width - (double)(properties.border.left + properties.border.right);
					repeatsX = MathHelper.floor(topWidth / (double)innerWidth);
					remainderX = topWidth % (double)innerWidth;
					if (properties.stretchInner) {
						this.drawIconTextureDouble(x + (double)innerLeft, y + (double)innerTop, x + (double)innerLeft + width - (double)properties.border.right - (double)properties.border.left, y + (double)innerTop + height - (double)properties.border.bottom - (double)properties.border.top, (double)innerLeft * uScale, (double)innerTop * vScale, (double)innerRight * uScale, (double)innerBottom * vScale, coordinate);
					} else {
						for(int j = 0; j < repeatsY; ++j) {
							this.drawIconTextureDouble(x + (double)innerLeft + (double)(repeatsX * innerWidth), y + (double)innerTop + (double)(j * innerHeight), x + (double)innerLeft + (double)(repeatsX * innerWidth) + remainderX, y + (double)innerTop + (double)(j * innerHeight) + (double)innerHeight, (double)innerLeft * uScale, (double)innerTop * vScale, ((double)innerLeft + remainderX) * uScale, (double)(innerTop + innerHeight) * vScale, coordinate);
						}

						for(int i = 0; i < repeatsX; ++i) {
							this.drawIconTextureDouble(x + (double)innerLeft + (double)(i * innerWidth), y + (double)innerTop + (double)(repeatsY * innerHeight), x + (double)innerLeft + (double)(i * innerWidth) + (double)innerWidth, y + (double)innerTop + (double)(repeatsY * innerHeight) + remainderY, (double)innerLeft * uScale, (double)innerTop * vScale, (double)(innerLeft + innerWidth) * uScale, ((double)innerTop + remainderY) * vScale, coordinate);
						}

						this.drawIconTextureDouble(x + (double)innerLeft + (double)(repeatsX * innerWidth), y + (double)innerTop + (double)(repeatsY * innerHeight), x + (double)innerLeft + (double)(repeatsX * innerWidth) + remainderX, y + (double)innerTop + (double)(repeatsY * innerHeight) + remainderY, (double)innerLeft * uScale, (double)innerTop * vScale, ((double)innerLeft + remainderX) * uScale, ((double)innerTop + remainderY) * vScale, coordinate);

						for(int i = 0; i < repeatsX; ++i) {
							for(int j = 0; j < repeatsY; ++j) {
								double xMin = x + (double)innerLeft + (double)(i * innerWidth);
								double yMin = y + (double)innerTop + (double)(j * innerHeight);
								this.drawIconTextureDouble(xMin, yMin, xMin + (double)innerWidth, yMin + (double)innerHeight, (double)innerLeft * uScale, (double)innerTop * vScale, (double)(innerLeft + innerWidth) * uScale, (double)(innerTop + innerHeight) * vScale, coordinate);
							}
						}
					}

					for(int i = 0; i < repeatsY; ++i) {
						this.drawIconTextureDouble(x, y + (double)innerTop + (double)(i * innerHeight), x + (double)innerLeft, y + (double)innerTop + (double)(i * innerHeight) + (double)innerHeight, (double)0.0F, (double)innerTop * vScale, (double)innerLeft * uScale, (double)(innerTop + innerHeight) * vScale, coordinate);
						this.drawIconTextureDouble(x + width - (double)properties.border.right, y + (double)innerTop + (double)(i * innerHeight), x + width, y + (double)innerTop + (double)(i * innerHeight) + (double)innerHeight, (double)innerRight * uScale, (double)innerTop * vScale, (double)(innerRight + properties.border.right) * uScale, (double)(innerTop + innerHeight) * vScale, coordinate);
					}

					this.drawIconTextureDouble(x, y + (double)innerTop + (double)(repeatsY * innerHeight), x + (double)innerLeft, y + (double)innerTop + (double)(repeatsY * innerHeight) + remainderY, (double)0.0F, (double)innerTop * vScale, (double)innerLeft * uScale, ((double)innerTop + remainderY) * vScale, coordinate);
					this.drawIconTextureDouble(x + width - (double)properties.border.right, y + (double)innerTop + (double)(repeatsY * innerHeight), x + width, y + (double)innerTop + (double)(repeatsY * innerHeight) + remainderY, (double)innerRight * uScale, (double)innerTop * vScale, (double)(innerRight + properties.border.right) * uScale, ((double)innerTop + remainderY) * vScale, coordinate);

					for(int i = 0; i < repeatsX; ++i) {
						this.drawIconTextureDouble(x + (double)innerLeft + (double)(i * innerWidth), y, x + (double)innerLeft + (double)(i * innerWidth) + (double)innerWidth, y + (double)properties.border.top, (double)innerLeft * uScale, (double)0.0F, (double)(innerLeft + innerWidth) * uScale, (double)properties.border.top * vScale, coordinate);
						this.drawIconTextureDouble(x + (double)innerLeft + (double)(i * innerWidth), y + height - (double)properties.border.bottom, x + (double)innerLeft + (double)(i * innerWidth) + (double)innerWidth, y + height, (double)innerLeft * uScale, (double)innerBottom * vScale, (double)(innerLeft + innerWidth) * uScale, (double)(innerBottom + properties.border.bottom) * vScale, coordinate);
					}

					this.drawIconTextureDouble(x + (double)innerLeft + (double)(repeatsX * innerWidth), y, x + (double)innerLeft + (double)(repeatsX * innerWidth) + remainderX, y + (double)properties.border.top, (double)innerLeft * uScale, (double)0.0F, ((double)innerLeft + remainderX) * uScale, (double)properties.border.top * vScale, coordinate);
					this.drawIconTextureDouble(x + (double)innerLeft + (double)(repeatsX * innerWidth), y + height - (double)properties.border.bottom, x + (double)innerLeft + (double)(repeatsX * innerWidth) + remainderX, y + height, (double)innerLeft * uScale, (double)innerBottom * vScale, ((double)innerLeft + remainderX) * uScale, (double)(innerBottom + properties.border.bottom) * vScale, coordinate);
					this.drawIconTextureDouble(x, y, x + (double)properties.border.left, y + (double)properties.border.top, (double)0.0F, (double)0.0F, (double)properties.border.left * uScale, (double)properties.border.top * vScale, coordinate);
					this.drawIconTextureDouble(x, y + height - (double)properties.border.bottom, x + (double)properties.border.left, y + height, (double)0.0F, (double)innerBottom * vScale, (double)properties.border.left * uScale, (double)(innerBottom + properties.border.bottom) * vScale, coordinate);
					this.drawIconTextureDouble(x + width - (double)properties.border.right, y, x + width, y + (double)properties.border.top, (double)innerRight * uScale, (double)0.0F, (double)(innerRight + properties.border.right) * uScale, (double)properties.border.top * uScale, coordinate);
					this.drawIconTextureDouble(x + width - (double)properties.border.right, y + height - (double)properties.border.bottom, x + width, y + height, (double)innerRight * uScale, (double)innerBottom * vScale, (double)(innerRight + properties.border.right) * uScale, (double)(innerBottom + properties.border.bottom) * vScale, coordinate);
			}
		} else {
			this.drawIconTextureDouble(x, y, x + width, y + height, (double)0.0F, (double)0.0F, (double)coordinate.width, (double)coordinate.height, coordinate);
		}

	}

	public void drawIconTextureDouble(double x0, double y0, double x1, double y1, double u0, double v0, double u1, double v1, IconCoordinate coordinate) {
		coordinate.parentAtlas.bind();
		double realU0 = coordinate.getSubIconU(u0 / (double)coordinate.width);
		double realU1 = coordinate.getSubIconU(u1 / (double)coordinate.width);
		double realV0 = coordinate.getSubIconV(v0 / (double)coordinate.height);
		double realV1 = coordinate.getSubIconV(v1 / (double)coordinate.height);
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.addVertexWithUV(x0, y1, 0, realU0, realV1);
		t.addVertexWithUV(x1, y1, 0, realU1, realV1);
		t.addVertexWithUV(x1, y0, 0, realU1, realV0);
		t.addVertexWithUV(x0, y0, 0, realU0, realV0);
		t.draw();
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
	public void drawString(String s, int x, int y, int color, boolean isShadow) {
		getFont().drawString(s, x, y, color, isShadow);
	}

	@Override
	public void drawStringWithShadow(String s, int x, int y, int color) {
		getFont().drawStringWithShadow(s, x, y, color);
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

	@Override
	public Font getFont() {
		return getMinecraft().font;
	}
}
