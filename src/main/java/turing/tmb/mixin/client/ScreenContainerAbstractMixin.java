package turing.tmb.mixin.client;

import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turing.tmb.client.TMBRenderer;

@Mixin(value =  ScreenContainerAbstract.class, remap = false)
public class ScreenContainerAbstractMixin extends Screen {
	public ScreenContainerAbstractMixin() {
		super();
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void render(int mouseX, int mouseY, float pt, CallbackInfo ci) {
		TMBRenderer.renderHeader(mouseX, mouseY, width, height, mc, pt, null);
		TMBRenderer.renderItems(mouseX, mouseY, width, height, mc, pt, null);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
		TMBRenderer.onTick();
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"))
	public void mouseClicked(int mouseX, int mouseY, int buttonNum, CallbackInfo ci) {
		TMBRenderer.mouseClicked(mouseX, mouseY, width, height, mc);
	}

	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	public void keyPressed(char eventCharacter, int eventKey, int mx, int my, CallbackInfo ci) {
		TMBRenderer.keyTyped(eventCharacter, eventKey, mx, my);
		if (TMBRenderer.search.isFocused) {
			ci.cancel();
		}
	}
}
