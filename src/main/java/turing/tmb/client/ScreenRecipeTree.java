package turing.tmb.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.ItemElement;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.TooltipElement;
import net.minecraft.client.gui.options.OptionsButtonElement;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Scissor;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.util.collection.Pair;
import net.minecraft.core.util.helper.Color;
import net.minecraft.core.util.helper.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import turing.tmb.RecipeIngredient;
import turing.tmb.RecipeTreeIngredient;
import turing.tmb.TMB;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.ingredient.IIngredientRenderer;
import turing.tmb.api.ingredient.IIngredientType;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.RecipeIngredientRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;

public class ScreenRecipeTree extends Screen
{
    private static final int TOP_SPACING = 24;
    private static final int BUTTON_SPACING = 4;
    private static final int SEPARATOR_WIDTH = 8;
    private static final int PADDING = 8;
    private static final int PAGE_BUTTON_HEIGHT = 20;

    private static final int ACHIEVEMENT_CELL_WIDTH = 24;
    private static final int ACHIEVEMENT_CELL_HEIGHT = 24;

    private static final int ACHIEVEMENT_ICON_WIDTH = 26;
    private static final int ACHIEVEMENT_ICON_HEIGHT = 26;

    private static final int TOOLTIP_BOX_WIDTH_MIN = 120;
    private static final int TOOLTIP_OFF_X = 8;
    private static final int TOOLTIP_OFF_Y = -4;

    protected int mouseXOld;
    protected int mouseYOld;
    protected double oldShiftX;
    protected double oldShiftY;
    protected double targetShiftX;
    protected double targetShiftY;
    protected double currentShiftX;
    protected double currentShiftY;
    private boolean draggingViewport;
    private final TooltipElement tooltip;
    private ItemElement renderItem = null;
    Screen parent;

    private int top;
    private int bottom;

    private int viewportLeft;
    private int viewportTop;
    private int viewportRight;
    private int viewportBottom;
    private int viewportWidth;
    private int viewportHeight;

    private double viewportZoom = 1;

    private double shiftMinX;
    private double shiftMinY;
    private double shiftMaxX;
    private double shiftMaxY;

    private int pageListLeft;
    private int pageListRight;

    private float pageListScrollAmount = 0.0f;
    private Float oldPagesListScrollAmount;
    private int pagesListScrollRegionHeight;

    private Integer clickX, clickY;

	private final RecipeIngredient mainRecipeResult;
    private RecipeTreePage hoveredPage = null;
	private RecipeTreeIngredient hoveredTreeIngredient = null;

    private RecipeTreePage currentPage;

    private BGLayer[] layers;

    public ScreenRecipeTree(Screen parent, RecipeTreePage page, RecipeIngredient ingredient)
    {
        mouseXOld = 0;
        mouseYOld = 0;
        draggingViewport = false;
		currentPage = page;
		mainRecipeResult = ingredient;

        this.parent = parent;
        this.mc = Minecraft.getMinecraft();
        this.tooltip = new TooltipElement(mc);
        this.renderItem = new ItemElement(mc);

		layers = new BGLayer[currentPage.backgroundLayers()];
		for (int i = 0; i < layers.length; i++) {
			layers[i] = new BGLayer(i);
		}
    }

    @Override
	public void init()
    {
        buttons.clear();
        buttons.add(new OptionsButtonElement(1, width / 2 - 100, height - 20 - BUTTON_SPACING, 200, 20, I18n.getInstance().translateKey("gui.achievements.button.done")));

        lastTileX = Integer.MIN_VALUE;
        lastTileY = Integer.MIN_VALUE;

        top = TOP_SPACING;
        bottom = height - (BUTTON_SPACING + 20 + BUTTON_SPACING);

        pagesListScrollRegionHeight = bottom - top;
        pageListLeft = 0;
        pageListRight = width/4;

        viewportZoom = 1;

        viewportLeft = drawSidebar() ? pageListRight + SEPARATOR_WIDTH : 0;
        viewportTop = top;
        viewportBottom = bottom;
        viewportRight = width;

        viewportWidth = viewportRight - viewportLeft;
        viewportHeight = viewportBottom - viewportTop;

        int achMinX = Integer.MAX_VALUE;
        int achMinY = Integer.MAX_VALUE;
        int achMaxX = Integer.MIN_VALUE;
        int achMaxY = Integer.MIN_VALUE;

        for (RecipeTreeIngredient q : currentPage.getTreeIngredients()){
            if (q.getX() < achMinX){
                achMinX = q.getX();
            }
            if (q.getY() < achMinY){
                achMinY = q.getY();
            }
            if (q.getX() > achMaxX){
                achMaxX = q.getX();
            }
            if (q.getY() > achMaxY){
                achMaxY = q.getY();
            }
        }

        shiftMinX = achMinX * ACHIEVEMENT_CELL_WIDTH ;
        shiftMinY = achMinY * ACHIEVEMENT_CELL_HEIGHT;
        shiftMaxX = achMaxX * ACHIEVEMENT_CELL_WIDTH  + ACHIEVEMENT_CELL_WIDTH;
        shiftMaxY = achMaxY * ACHIEVEMENT_CELL_HEIGHT + ACHIEVEMENT_CELL_HEIGHT;

        shiftMinX -= (int) (viewportWidth /4d);
        shiftMinY -= (int) (viewportHeight/4d);
        shiftMaxX += (int) (viewportWidth /4d);
        shiftMaxY += (int) (viewportHeight/4d);

        // Centers the screen on the Open ContainerInventory achievement
        RecipeTreeIngredient root = currentPage.getTreeRoot();
        oldShiftX = targetShiftX = currentShiftX = root.getX() * ACHIEVEMENT_CELL_WIDTH + ACHIEVEMENT_CELL_WIDTH/2d;
        oldShiftY = targetShiftY = currentShiftY = root.getY() * ACHIEVEMENT_CELL_HEIGHT + ACHIEVEMENT_CELL_HEIGHT/2d;
    }

    @Override
	protected void buttonClicked(ButtonElement button) {
        if(button.id == 1) {
            mc.displayScreen(parent);
        }
        super.buttonClicked(button);
    }

    @Override
	public void keyPressed(char eventCharacter, int eventKey, int mx, int my)
    {
        if (eventKey == Keyboard.KEY_ESCAPE) {
            mc.displayScreen(parent);
        } else {
            super.keyPressed(eventCharacter, eventKey, mx, my);
        }

		if (eventKey == mc.gameSettings.keyShowRecipe.getKeyCode() || eventKey == mc.gameSettings.keyShowUsage.getKeyCode()) {
			if(hoveredTreeIngredient != null){
				if (eventKey == mc.gameSettings.keyShowUsage.getKeyCode()) {
					TMB.getRuntime().showRecipe(hoveredTreeIngredient.ingredient.ingredient, RecipeIngredientRole.INPUT);
				} else {
					TMB.getRuntime().showRecipe(hoveredTreeIngredient.ingredient.ingredient, RecipeIngredientRole.OUTPUT);
				}
			}
		}
    }
    @Override
    public void mouseClicked(int mx, int my, int buttonNum) {
        if (drawSidebar() && mx >= pageListLeft && mx <= (pageListRight - 6) && my >= top && my <= bottom) {
            int pagesListHeight = getTotalPagesListHeight();
            int pagesListY = top - (int) pageListScrollAmount;
            if (pagesListHeight < bottom - top) {
                pagesListY = top + (bottom - top - pagesListHeight) / 2;
            }
        }


        super.mouseClicked(mx, my, buttonNum);

        clickX = mx;
        clickY = my;
    }

    @Override
	public void render(int mx, int my, float partialTick)
    {
        if(Mouse.isButtonDown(0)) {
            if(mx >= viewportLeft && mx < viewportRight && my >= viewportTop && my < viewportBottom) {
                if(!draggingViewport) {
                    draggingViewport = true;
                } else {
                    targetShiftX -= (mx - mouseXOld) / viewportZoom;
                    targetShiftY -= (my - mouseYOld) / viewportZoom;
                    currentShiftX = oldShiftX = targetShiftX;
                    currentShiftY = oldShiftY = targetShiftY;
                }
                mouseXOld = mx;
                mouseYOld = my;
            }
            currentShiftX =  MathHelper.clamp(currentShiftX, shiftMinX, shiftMaxX);
            currentShiftY =  MathHelper.clamp(currentShiftY, shiftMinY, shiftMaxY);
        } else if (mc.controllerInput != null) {
            targetShiftX += mc.controllerInput.joyRight.getX() / viewportZoom * 4;
            targetShiftY += mc.controllerInput.joyRight.getY() / viewportZoom * 4;
            currentShiftX = oldShiftX = targetShiftX;
            currentShiftY = oldShiftY = targetShiftY;
            currentShiftX =  MathHelper.clamp(currentShiftX, shiftMinX, shiftMaxX);
            currentShiftY =  MathHelper.clamp(currentShiftY, shiftMinY, shiftMaxY);

            if (mc.controllerInput.buttonLeftTrigger.isPressed()) {
                viewportZoom -= 0.01d;
            } else if (mc.controllerInput.buttonRightTrigger.isPressed()) {
                viewportZoom += 0.01f;
            }
            viewportZoom = MathHelper.clamp(viewportZoom, 0.5d, 2d);

        } else {
            clickX = clickY = null;
            oldPagesListScrollAmount = null;
            draggingViewport = false;
        }

		if (mx >= viewportLeft && mx <= viewportRight && my >= viewportTop && my <= viewportBottom){
            final double change = (Mouse.getDWheel()/10d);
            viewportZoom = MathHelper.clamp(viewportZoom + change, 0.5d, 2);

            // Make zoom notch onto integer multiples
            if (change != 0) {
                final double[] notches = new double[]{0.25, 0.5, 1, 2, 4};
                for (double notch : notches){
                    if (Math.abs(viewportZoom - notch) < 0.05){
                        viewportZoom = notch;
                        break;
                    }
                }
            }
        }
        Mouse.getDWheel();

        renderBackground();

        renderAchievementsPanel(mx, my, partialTick);

        overlayBackground(0, width, 0, top, 0x404040);
        overlayBackground(0, width, bottom, height, 0x404040);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        super.render(mx, my, partialTick); // Draw Buttons
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        Lighting.disable();

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glShadeModel(GL11.GL_SMOOTH);

            byte fadeDist = 4;
            Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(0, 0);
			tessellator.addVertexWithUV(0, top + fadeDist, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(width, top + fadeDist, 0.0D, 1.0D, 1.0D);
			tessellator.setColorRGBA_I(0, 255);
			tessellator.addVertexWithUV(width, top, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(0, top, 0.0D, 0.0D, 0.0D);
			tessellator.draw();

			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(0, 255);
			tessellator.addVertexWithUV(0, bottom, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(width, bottom, 0.0D, 1.0D, 1.0D);
			tessellator.setColorRGBA_I(0, 0);
			tessellator.addVertexWithUV(width, bottom - fadeDist, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(0, bottom - fadeDist, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
            GL11.glEnable(GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
        }

        if (hoveredTreeIngredient != null){
            drawAchievementToolTip(hoveredTreeIngredient, mx, my);
        }

        renderLabels();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        hoveredPage = null;
    }

    @Override
	public void tick() {
        oldShiftX = targetShiftX;
        oldShiftY = targetShiftY;
        double xDiff = currentShiftX - targetShiftX;
        double yDiff = currentShiftY - targetShiftY;
        if(xDiff * xDiff + yDiff * yDiff < 4D) {
            targetShiftX += xDiff;
            targetShiftY += yDiff;
        } else {
            targetShiftX += xDiff * 0.85D;
            targetShiftY += yDiff * 0.85D;
        }
    }

    protected void renderLabels() {
        font.drawCenteredString(I18n.getInstance().translateKey("gui.tmb.recipeTree.label.title")/* + " " + viewportZoom + " X:" + currentShiftX + ", Y:" + currentShiftY*/, width/2, 5 , 0xFFFFFF);
    }


    protected void renderAchievementsPanel(int mouseX, int mouseY, float partialTick){
        double shiftX = MathHelper.lerp(oldShiftX, targetShiftX, partialTick);
        double shiftY = MathHelper.lerp(oldShiftY, targetShiftY, partialTick);
        shiftX =  MathHelper.clamp(shiftX, shiftMinX, shiftMaxX);
        shiftY =  MathHelper.clamp(shiftY, shiftMinY, shiftMaxY);

        zLevel = 0.0F;

        GL11.glDepthFunc(GL11.GL_GEQUAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, -200F);
        Scissor.enable(viewportLeft, viewportTop, viewportWidth, viewportHeight);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        drawRectDouble(viewportLeft, viewportTop, viewportRight, viewportBottom, 0xFF000000 | currentPage.backgroundColor()); // Ensures that the viewport always has a background of some kind

        GL11.glPushMatrix();
        drawBackgroundTiles(shiftX, shiftY);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL); // Responsible for culling the overdraw later on
        drawConnectingLines(mouseX, mouseY,shiftX, shiftY);

        Lighting.enableInventoryLight();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        hoveredTreeIngredient = drawAchievementIcons(mouseX, mouseY, shiftX, shiftY);

        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Scissor.disable();

        // drawRectDouble(viewportLeft + viewportWidth/2d - 2.5, viewportTop + viewportHeight/2d - 2.5, viewportLeft + viewportWidth/2d + 2.5, viewportTop + viewportHeight/2d + 2.5, 0xFFA0A0A0); // Debug cross-hair

        GL11.glPopMatrix();

        zLevel = 0.0F;
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private static final int TILE_WIDTH = 16;
    private static final int TILE_HEIGHT = 16;

    public int lastTileX = Integer.MIN_VALUE;
    public int lastTileY = Integer.MIN_VALUE;
    public int lastTilesWide = Integer.MIN_VALUE;
    public int lastTilesTall = Integer.MIN_VALUE;
    private void drawBackgroundTiles(double shiftX, double shiftY){
        double zoom = viewportZoom/* * 0.85*/;

        TextureRegistry.blockAtlas.bind();
        final int offset = 18 * TILE_WIDTH;
        int viewTileX = (MathHelper.floor(shiftX) + offset) / TILE_WIDTH;
        int viewTileY = (MathHelper.floor(shiftY) + offset) / TILE_HEIGHT;
        double remainderX = (shiftX + offset) % TILE_WIDTH;
        double remainderY = (shiftY + offset) % TILE_HEIGHT;
        Random random = new Random();

        int tilesWide = (int) (viewportWidth/(TILE_WIDTH * zoom) + 2);
        int tilesTall = (int) (viewportHeight/(TILE_HEIGHT * zoom) + 2);

        int orgX = -tilesWide/2 - 1;
        int orgY = -tilesTall/2 - 1;
        int endX = tilesWide/2 + 1;
        int endY = tilesTall/2 + 1;

        tilesWide = endX - orgX;
        tilesTall = endY - orgY;

        // Cache background, saves some render time which is nice
        if (viewTileX != lastTileX || viewTileY != lastTileY || tilesWide != lastTilesWide || tilesTall != lastTilesTall) {
            lastTileX = viewTileX;
            lastTileY = viewTileY;
            lastTilesWide = tilesWide;
            lastTilesTall = tilesTall;

            for (BGLayer layer : layers){
                layer.resize(tilesWide, tilesTall);
            }

            long worldSeed = mc.currentWorld == null ? 0 : mc.currentWorld.getRandomSeed();
            for (int _y = 0; _y < tilesTall; _y++){
                for (int _x = 0; _x < tilesWide; _x++) {
                    int tileX = orgX + _x + viewTileX;
                    int tileY = orgY + _y + viewTileY;
                    // Hopefully this is actually random enough :)
                    random.setSeed(worldSeed);
                    long l1 = random.nextLong();
                    random.setSeed(tileX);
                    long l2 = random.nextLong();
                    random.setSeed(tileY);
                    long l3 = random.nextLong();

                    long seed = Objects.hash(l1, l2, ~l3);

                    for (BGLayer layer : layers) {
                        random.setSeed(seed);
                        IconCoordinate fore = currentPage.getBackgroundTile(this, layer.id, random, tileX, tileY);
                        layer.put(fore, _x, _y);
                    }
                }
            }

            for (BGLayer layer : layers) {
                random.setSeed(worldSeed);
                currentPage.postProcessBackground(this, random, layer, orgX + viewTileX, orgY + viewTileY);
            }
        }



        for(int _y = 0; _y < tilesTall; _y++) {
            int tileY = orgY + _y + viewTileY;
            float brightness = 0.3F; //- ((float)(tileY) / 25F) * 0.3F;
            for(int _x = 0; _x < tilesWide; _x++) {
                int tileX = orgX + _x + viewTileX;

                for (int i = layers.length - 1; i >= 0; i--) {
                    BGLayer topLayer = getLayer(i);
                    IconCoordinate fore = topLayer.get(_x, _y);

                    IconCoordinate next = null;
                    if (i - 1 >= 0) {
                        BGLayer nextLayer = getLayer(i - 1);
                        next = nextLayer.get(_x, _y);
                    }


                    boolean bottom = false;
                    boolean top = false;
                    boolean left = false;
                    boolean right = false;
                    boolean topLeft = false;
                    boolean topRight = false;
                    boolean bottomLeft = false;
                    boolean bottomRight = false;


                    if (fore != null && next == null && i - 1 >= 0) {
                        BGLayer nextLayer = getLayer(i - 1);
                        top = nextLayer.get(_x, _y - 1) != null;
                        left = nextLayer.get(_x - 1, _y) != null;
                        right = nextLayer.get(_x + 1, _y) != null;
                        bottom = nextLayer.get(_x, _y + 1) != null;
                        topLeft = nextLayer.get(_x - 1, _y - 1) != null;
                        topRight = nextLayer.get(_x + 1, _y - 1) != null;
                        bottomLeft = nextLayer.get(_x - 1, _y + 1) != null;
                        bottomRight = nextLayer.get(_x + 1, _y + 1) != null;
                    }

                    double iconLeft = (viewportLeft + viewportWidth / 2d) + zoom * (((orgX + _x) * TILE_WIDTH) - remainderX);
                    double iconTop = (viewportTop + viewportHeight / 2d) + zoom * (((orgY + _y) * TILE_HEIGHT) - remainderY);
                    double iconWidth = TILE_WIDTH * zoom;
                    double iconHeight = TILE_HEIGHT * zoom;

                    if (fore != null) {
                        float shadowScale = (float) Math.pow(0.65f, i);
                        if (next != null) {
                            shadowScale *= 0.5f;
                        }
                        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
                        final double epsilon = 0.05;
                        drawGuiIconDouble(iconLeft - epsilon, iconTop - epsilon, iconWidth + epsilon * 2, iconHeight + epsilon * 2, fore);
                    }
                }
            }
        }
    }

    public BGLayer getLayer(int layer){
        if (layer < 0 || layer >= layers.length) return null;
        return layers[layer];
    }

    private double timeSin(double amplitude, long period){
        return Math.sin(((double)(System.currentTimeMillis() % period) / period) * Math.PI * 2D) * amplitude;
    }
    private void drawConnectingLines(int mouseX, int mouseY, double shiftX, double shiftY){
        double zoom = viewportZoom;

        for(RecipeTreeIngredient entry : currentPage.getTreeIngredients()) {
            RecipeIngredient ingredient = entry.ingredient;
			List<IIngredientList> list = new ArrayList<>();
			if(ingredient.recipe == null || ingredient.category == null) continue;
			ingredient.category.getIngredients(ingredient.recipe,ingredient.category.getRecipeLayout(), null, list);
			List<RecipeTreeIngredient> inputs = currentPage.getTreeIngredients();
			for (RecipeTreeIngredient child : inputs) {
				if(child == entry) continue;
				if(child.ingredient.recipe == null || child.ingredient.category == null) continue;
				double childX = (viewportLeft + viewportWidth/2d) + ((entry.getX() * ACHIEVEMENT_CELL_WIDTH - shiftX) + 11) * zoom;
				double childY = (viewportTop + viewportHeight/2d) + ((entry.getY() * ACHIEVEMENT_CELL_HEIGHT - shiftY) + 11) * zoom;
				double parentX = (viewportLeft + viewportWidth/2d) + ((child.getX() * ACHIEVEMENT_CELL_WIDTH - shiftX) + 11) * zoom;
				double parentY = (viewportTop + viewportHeight/2d) + ((child.getY() * ACHIEVEMENT_CELL_HEIGHT - shiftY) + 11) * zoom;
				boolean unlocked = false;
				boolean canUnlock = false;

				final double zoomOff = 11 * zoom;

				boolean isHovered = false;
				{
					double x = parentX - zoomOff;
					double y = parentY - zoomOff;
					if ((mouseX >= 0 && mouseY >= viewportTop && mouseX < width && mouseY < viewportBottom) && // In viewport and
						(mouseX >= x && mouseX <= x + 22 * zoom && mouseY >= y && mouseY <= y + 22 * zoom)) { // Hovering over achievement
						isHovered = true;
					}

					x = childX - zoomOff;
					y = childY - zoomOff;
					if ((mouseX >= 0 && mouseY >= viewportTop && mouseX < width && mouseY < viewportBottom) && // In viewport and
						(mouseX >= x && mouseX <= x + 22 * zoom && mouseY >= y && mouseY <= y + 22 * zoom)) { // Hovering over achievement
						isHovered = true;
					}
				}

				int color;
				if(unlocked) {
					color = 0xff << Color.SHIFT_ALPHA | (currentPage.lineColorUnlocked(isHovered) & 0xffffff);
				} else if (canUnlock) {
					int alpha = timeSin(1, 600) >= 0.6 ? 0x82 : 0xff;
					color = (alpha << Color.SHIFT_ALPHA) | (currentPage.lineColorCanUnlock(isHovered) & 0xffffff);
				} else {
					color = 0xff << Color.SHIFT_ALPHA | (currentPage.lineColorLocked(isHovered) & 0xffffff);
				}

				drawLineHorizontalDouble(childX, parentX, childY, color);
				drawLineVerticalDouble(parentX, childY, parentY, color);
			}
        }
    }

    private RecipeTreeIngredient drawAchievementIcons(int mouseX, int mouseY, double shiftX, double shiftY){
        double zoom = viewportZoom;

		RecipeTreeIngredient hoveredAchievment = null;
        for(RecipeTreeIngredient recipeTreeIngredient : currentPage.getTreeIngredients()) {
            RecipeIngredient ingredient = recipeTreeIngredient.ingredient;
            double achViewX = (viewportLeft + viewportWidth/2d) + (recipeTreeIngredient.getX() * ACHIEVEMENT_CELL_WIDTH - shiftX ) * zoom;
            double achViewY = (viewportTop + viewportHeight/2d) + (recipeTreeIngredient.getY() * ACHIEVEMENT_CELL_HEIGHT - shiftY) * zoom;
            if(achViewX < viewportLeft - ACHIEVEMENT_CELL_WIDTH * zoom || achViewY < viewportTop - ACHIEVEMENT_CELL_HEIGHT * zoom || achViewX > viewportRight || achViewY > viewportBottom) { // Continue if outside viewport
                continue;
            }
			float brightness = 0.50F;
			GL11.glColor4f(brightness, brightness, brightness, 1.0F);

            drawGuiIconDouble(achViewX - (ACHIEVEMENT_ICON_WIDTH - ACHIEVEMENT_CELL_WIDTH) * zoom, achViewY - (ACHIEVEMENT_ICON_HEIGHT - ACHIEVEMENT_CELL_HEIGHT) * zoom, ACHIEVEMENT_ICON_WIDTH * zoom, ACHIEVEMENT_ICON_HEIGHT * zoom, currentPage.drawIngredientBackground(ingredient));

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            ItemStack achievementItem = ingredient.ingredient.getItemStack().orElse(null);

            GL11.glTranslated(achViewX + 3 * zoom, achViewY + 3 * zoom, 0);
            GL11.glScaled(zoom, zoom, 1);
			IIngredientType<?> type = ingredient.ingredient.getType();
			IIngredientRenderer<Object> renderer = (IIngredientRenderer<Object>) type.getRenderer(TMB.getRuntime());
			new DrawableIngredient<>(ingredient.ingredient.getIngredient(), renderer).draw(TMB.getRuntime().getGuiHelper());
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            if((mouseX >= 0 && mouseY >= viewportTop && mouseX < width && mouseY < viewportBottom) && // In viewport and
                (mouseX >= achViewX && mouseX <= achViewX + 22 * zoom && mouseY >= achViewY && mouseY <= achViewY + 22 * zoom)) { // Hovering over achievement
                hoveredAchievment = recipeTreeIngredient;
            }
        }
        return hoveredAchievment;
    }
    private void drawAchievementToolTip(RecipeTreeIngredient treeIngredient, int mouseX, int mouseY){
		StringBuilder s = new StringBuilder(treeIngredient.ingredient.ingredient.getName());
		tooltip.render(s.toString(),mouseX,mouseY,8,-8);
    }
    public boolean drawSidebar(){
        return false;
    }
    private void scrollPagesList(float amount) {
        if(amount == 0.0f) return;

        pageListScrollAmount += amount;
        onScrollPagesList();
    }

    private void onScrollPagesList() {
        int totalPagesListHeight = getTotalPagesListHeight();
        if (pageListScrollAmount < 0 || pagesListScrollRegionHeight > totalPagesListHeight) pageListScrollAmount = 0;
        else if (pageListScrollAmount > totalPagesListHeight - pagesListScrollRegionHeight) pageListScrollAmount = totalPagesListHeight - pagesListScrollRegionHeight;
    }
    private int getTotalPagesListHeight() {
        return PAGE_BUTTON_HEIGHT * 0;//VintageQuesting.CHAPTERS.size();
    }

    private void overlayBackground(int minX, int maxX, int minY, int maxY, int color)
    {
        Tessellator tessellator = Tessellator.instance;
        mc.textureManager.loadTexture("/assets/minecraft/textures/gui/background.png").bind();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float scale = 32F;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(color);
        tessellator.addVertexWithUV(minX, maxY, 0.0D, (float) minX / scale, (float) maxY / scale);
        tessellator.addVertexWithUV(maxX, maxY, 0.0D, (float) maxX / scale, (float) maxY / scale);
        tessellator.setColorOpaque_I(color);
        tessellator.addVertexWithUV(maxX, minY, 0.0D, (float) maxX / scale, (float) minY / scale);
        tessellator.addVertexWithUV(minX, minY, 0.0D, (float) minX / scale, (float) minY / scale);
        tessellator.draw();
    }

    public static class BGLayer {
        private IconCoordinate[] data;
        private int width;
        private int height;
        public final int id;
        public BGLayer(int id) {
            this.id = id;
            data = new IconCoordinate[0];
            width = 0;
            height = 0;
        }
        public int getWidth() {
            return width;
        }
        public int getHeight() {
            return height;
        }
        public IconCoordinate[] getData() {
            return data;
        }
        protected void resize(int width, int height) {
            this.width = width;
            this.height = height;
            data = new IconCoordinate[width * height];
        }
        public void put(IconCoordinate coordinate, int x, int y){
            if (x < 0) return;
            if (y < 0) return;
            if (x >= width) return;
            if (y >= height) return;
            data[makeIndex(x, y)] = coordinate;
        }

        public IconCoordinate get(int x, int y){
            if (x < 0) return null;
            if (y < 0) return null;
            if (x >= width) return null;
            if (y >= height) return null;
            return data[makeIndex(x, y)];
        }

        private int makeIndex(int x, int y){
            return x % width + y * width;
        }
    }
}
