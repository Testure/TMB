package turing.tmb.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turing.tmb.api.drawable.gui.IGuiProperties;
import turing.tmb.client.TMBRenderer;
import turing.tmb.util.GuiHelper;

@Mixin(value = GuiScreen.class, remap = false)
public class ScreenMixin {
	@Shadow
	protected Minecraft mc;

	@Shadow
	public int height;

	@Shadow
	public int width;

	@Inject(method = "drawScreen", at = @At("TAIL"))
	public void render(int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		GuiScreen t = (GuiScreen) (Object) this;
		if (GuiHelper.extraScreens.containsKey(t.getClass().getCanonicalName())) {
			IGuiProperties properties = GuiHelper.extraScreens.get(t.getClass().getCanonicalName()).apply(t);
			TMBRenderer.renderHeader(mouseX, mouseY, width, height, mc, partialTick, properties);
			TMBRenderer.renderItems(mouseX, mouseY, width, height, mc, partialTick, properties);
		}
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
		GuiScreen t = (GuiScreen) (Object) this;
		if (GuiHelper.extraScreens.containsKey(t.getClass().getCanonicalName())) {
			TMBRenderer.onTick();
		}
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"))
	public void mouseClicked(int mouseX, int mouseY, int buttonNum, CallbackInfo ci) {
		GuiScreen t = (GuiScreen) (Object) this;
		if (GuiHelper.extraScreens.containsKey(t.getClass().getCanonicalName())) {
			TMBRenderer.mouseClicked(mouseX, mouseY, width, height, mc);
		}
	}

	@Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
	public void keyPressed(char eventCharacter, int eventKey, int mx, int my, CallbackInfo ci) {
		GuiScreen t = (GuiScreen) (Object) this;
		if (GuiHelper.extraScreens.containsKey(t.getClass().getCanonicalName())) {
			TMBRenderer.keyTyped(eventCharacter, eventKey, mx, my);
			if (TMBRenderer.search.isFocused) {
				ci.cancel();
			}
		}
	}
}
