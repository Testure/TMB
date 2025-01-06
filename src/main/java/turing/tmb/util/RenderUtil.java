package turing.tmb.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import turing.tmb.TMB;
import turing.tmb.api.drawable.gui.IGuiHelper;

public class RenderUtil {
	public static void renderItemInGui(Minecraft mc, ItemStack stack, int x, int y, int scaleX, int scaleY, float brightness, float alpha) {
		GL11.glPushMatrix();
		Lighting.enableInventoryLight();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		BlockModel.setRenderBlocks(EntityRenderDispatcher.instance.itemRenderer.renderBlocksInstance);
		ItemModel model = ItemModelDispatcher.getInstance().getDispatch(stack);
		GL11.glTranslatef(x, y, 0);
		GL11.glScaled(scaleX, scaleY, 1);
		try {
			model.renderItemIntoGui(Tessellator.instance, mc.font, mc.textureManager, stack, 0, 0, brightness, alpha);
		} catch (Exception e) {
			TMB.LOGGER.warn(e.getMessage());
		}

		Lighting.disable();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}

	public static void renderItemSelected(IGuiHelper helper, int x, int y) {
		GL11.glPushMatrix();
		helper.drawRect(x, y, x + 16, y + 16, -2130706433);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glPopMatrix();
	}
}
