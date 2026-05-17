package turing.tmb.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.LightIndexHelper;
import turing.tmb.TMB;
import turing.tmb.api.drawable.gui.IGuiHelper;

public class RenderUtil {
	public static void renderItemInGui(Minecraft mc, ItemStack stack, int x, int y, int scaleX, int scaleY, float brightness, float alpha) {
		GLRenderer.pushFrame();
		Lighting.enableInventoryLight();
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.DEPTH_TEST);

		ItemModel model = ItemModelDispatcher.getInstance().getDispatch(stack);
		GLRenderer.modelM4f().translate(x, y, 0);
		GLRenderer.modelM4f().scale(scaleX, scaleY, 1);
		try {
			model.renderGui(GLRenderer.getTessellator(), null, stack,0,0, LightIndexHelper.lightIndex2i(15,15), 1);
		} catch (Exception e) {
			TMB.LOGGER.warn(e.getMessage());
		}

		Lighting.disable();
		GLRenderer.disableState(State.DEPTH_TEST);
		GLRenderer.popFrame();
	}

	public static void renderItemSelected(IGuiHelper helper, int x, int y) {
		GLRenderer.pushFrame();
		helper.drawRect(x, y, x + 16, y + 16, -2130706433);
		GLRenderer.setColor4f(1F, 1F, 1F, 1F);
		GLRenderer.popFrame();
	}
}
