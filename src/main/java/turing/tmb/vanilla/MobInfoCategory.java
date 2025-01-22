package turing.tmb.vanilla;

import net.minecraft.client.gui.guidebook.mobs.MobInfoRegistry;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Scissor;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.Items;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import turing.tmb.RecipeLayoutBuilder;
import turing.tmb.TMB;
import turing.tmb.TypedIngredient;
import turing.tmb.api.ItemStackIngredientRenderer;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.drawable.IDrawable;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.recipe.ILookupContext;
import turing.tmb.api.recipe.IRecipeCategory;
import turing.tmb.api.recipe.IRecipeLayout;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.client.DrawableBlank;
import turing.tmb.client.DrawableIngredient;
import turing.tmb.mixin.client.GuidebookPageMobMixin;
import turing.tmb.util.IngredientList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MobInfoCategory implements IRecipeCategory<MobInfoRecipeTranslator> {
	private final IDrawable icon;
	private final IDrawable background;
	private final int xOffset = 8;
	private Mob mob;
	private boolean giveUp = false;

	public MobInfoCategory() {
		this.background = new DrawableBlank(180, 180);
		this.icon = new DrawableIngredient<>(Items.FOOD_PORKCHOP_RAW.getDefaultStack(), ItemStackIngredientRenderer.INSTANCE);
	}

	@Override
	public String getName() {
		return "guidebook.section.mob";
	}

	@Override
	public String getNamespace() {
		return "Minecraft";
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return icon;
	}

	private void drawHeart(ITMBRuntime runtime, int x, int y, boolean half) {
		runtime.getGuiHelper().drawGuiIcon(x, y, 9, 9, TextureRegistry.getTexture("minecraft:gui/hud/heart/container"));
		runtime.getGuiHelper().drawGuiIcon(x, y, 9, 9, TextureRegistry.getTexture("minecraft:gui/hud/heart/" + (half ? "half" : "full")));
	}

	private void drawHeart(ITMBRuntime runtime, int x, int y) {
		drawHeart(runtime, x, y, false);
	}

	private void drawMob(Mob mob, int x, int y) {
		float heightFactor = 27.777779F;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Scissor.enable(x + 9, y + 9, 52, 70);
		GL11.glEnable(32826);
		GL11.glEnable(2903);
		GL11.glEnable(2929);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)(x + 34), (float)(y + 44 + 32), 50.0F);
		float f1 = 30.0F;
		GL11.glScalef(-f1, f1, f1);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float f5 = (float)(x + 34);
		float f6 = (float)(y + 44 + 32) - heightFactor * mob.bbHeight;
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		Lighting.enableLight();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float)Math.atan(f6 / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
		mob.yBodyRot = (float)Math.atan(f5 / 40.0F) * 20.0F;
		mob.yRot = (float)Math.atan(f5 / 40.0F) * 40.0F;
		mob.xRot = -((float)Math.atan(f6 / 40.0F)) * 20.0F;
		mob.entityBrightness = 1.0F;
		GL11.glTranslatef(0.0F, mob.heightOffset, 0.0F);
		EntityRenderDispatcher.instance.viewLerpYaw = 180.0F;
		EntityRenderDispatcher.instance.renderEntityPreviewWithPosYaw(Tessellator.instance, mob, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
		GL11.glPopMatrix();
		Lighting.disable();
		GL11.glDisable(32826);
		GL11.glDisable(2929);
		Scissor.disable();
	}

	@Override
	public void drawRecipe(ITMBRuntime runtime, MobInfoRecipeTranslator recipe, IRecipeLayout layout, List<IIngredientList> ingredients, ILookupContext context) {
		String name = I18n.getInstance().translateKey(recipe.getOriginal().getNameTranslationKey());
		String[] lines = GuidebookPageMobMixin.invokeCreateDescLines(runtime.getGuiHelper().getFont(), recipe.getOriginal().getDescriptionTranslationKey());

		Lighting.disable();
		runtime.getGuiHelper().drawStringWithShadow(name, xOffset, 8, 0xFFFFFF);

		int yOffset = 26;
		if (recipe.getOriginal().getHealth() > 0) {
			runtime.getGuiHelper().drawStringWithShadow(I18n.getInstance().translateKey("guidebook.section.mob.health"), xOffset, 20, 0xFFFFFF);

			if (recipe.getOriginal().getHealth() > 40) {

				drawHeart(runtime, xOffset, 26);

				runtime.getGuiHelper().drawStringWithShadow("x " + recipe.getOriginal().getHealth() / 2, xOffset + 12, 26, 0xFFFFFF);
			} else {
				for (int i = 0; i < recipe.getOriginal().getHealth() / 2; i++) {
					if (i % 10 == 0) {
						yOffset += 4;
					}

					drawHeart(runtime, xOffset + i % 10 * 8, yOffset);
				}

				if (recipe.getOriginal().getHealth() % 2 == 1) {
					drawHeart(runtime, xOffset + recipe.getOriginal().getHealth() / 2 % 10 * 8, 26, true);
				}
			}
		}

		if (recipe.getOriginal().getScore() > 0) {
			yOffset += 11;
			runtime.getGuiHelper().drawStringWithShadow(I18n.getInstance().translateKeyAndFormat("guidebook.section.mob.score", recipe.getOriginal().getScore()), xOffset, yOffset, 0xFFFFFF);
		}

		yOffset = 84;
		for (String line : lines) {
			runtime.getGuiHelper().drawStringWithShadow(line, 8, yOffset, 0xFFFFFF);
			yOffset += 10;
		}

		if (recipe.getOriginal().getDrops() != null && recipe.getOriginal().getDrops().length > 0) {
			for (int i = 0; i < recipe.getOriginal().getDrops().length; i++) {
				ingredients.add(new IngredientList(TypedIngredient.itemStackIngredient(recipe.getOriginal().getDrops()[i].getStack())));
			}
		}

		if (mob == null && !giveUp) {
			Class<? extends Entity> mobClass = recipe.getOriginal().getEntityClass();
			try {
				mob = (Mob)mobClass.getConstructor(World.class).newInstance(runtime.getGuiHelper().getMinecraft().currentWorld);
				mob.setSkinVariant(0);
			} catch (Exception e) {
				giveUp = true;
				TMB.LOGGER.error("Failed to create example instance of mob '{}'!", mobClass.getSimpleName(), e);
			}
		}

		if (mob != null) {
			drawMob(mob, 2, 2);
		}
	}

	@Override
	public List<String> getTooltips(MobInfoRecipeTranslator recipe, int mouseX, int mouseY) {
		if (recipe.getOriginal().getDrops() != null && recipe.getOriginal().getDrops().length > 0) {
			List<String> tooltips = new ArrayList<>();

			if (mouseY >= 56 && mouseY < 56 + 18 && mouseX >= xOffset) {
				for (int i = 0; i < 3; i++) {
					if (mouseX >= xOffset + (18 * i) && mouseX < (xOffset + (18 * (i + 1)))) {
						if (i < recipe.getOriginal().getDrops().length) {
							MobInfoRegistry.MobDrop drop = recipe.getOriginal().getDrops()[i];

							if (drop.getMinAmount() != drop.getMaxAmount()) {
								tooltips.add(TextFormatting.formatted(drop.getMinAmount() + "-" + drop.getMaxAmount(), TextFormatting.LIGHT_GRAY));
							}
							tooltips.add(TextFormatting.formatted(String.valueOf(drop.getChance()), TextFormatting.GRAY));

							break;
						}
					}
				}
			}

			return tooltips;
		}
		return Collections.emptyList();
	}

	@Override
	public IRecipeLayout getRecipeLayout() {
		return new RecipeLayoutBuilder()
			.addOutputSlot(0, VanillaTypes.ITEM_STACK).setPosition(xOffset, 56).build()
			.addOutputSlot(1, VanillaTypes.ITEM_STACK).setPosition(xOffset + 18, 56).build()
			.addOutputSlot(2, VanillaTypes.ITEM_STACK).setPosition(xOffset + 18 + 18, 56).build()
			.build();
	}
}
