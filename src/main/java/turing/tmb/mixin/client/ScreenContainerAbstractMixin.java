package turing.tmb.mixin.client;

import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turing.tmb.TMB;
import turing.tmb.TypedIngredient;
import turing.tmb.api.recipe.RecipeIngredientRole;
import turing.tmb.client.TMBRenderer;

@Mixin(value =  ScreenContainerAbstract.class, remap = false)
public abstract class ScreenContainerAbstractMixin extends Screen {
	@Shadow
	protected abstract int getSlotId(int x, int y);

	@Shadow
	public MenuAbstract inventorySlots;

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
		if (TMB.shouldReplaceGuidebook) {
			if (eventKey == mc.gameSettings.keyShowUsage.getKeyCode()) {
				ci.cancel();
				int slotId = getSlotId(mx, my);
				if (slotId >= 0) {
					Slot slot = this.inventorySlots.slots.get(slotId);
					if (slot.hasItem()) {
						TMB.getRuntime().showRecipe(TypedIngredient.itemStackIngredient(slot.getItemStack()), RecipeIngredientRole.INPUT);
					}
				}
			}
			if (eventKey == mc.gameSettings.keyShowRecipe.getKeyCode()) {
				ci.cancel();
				int slotId = getSlotId(mx, my);
				if (slotId >= 0) {
					Slot slot = this.inventorySlots.slots.get(slotId);
					if (slot.hasItem()) {
						TMB.getRuntime().showRecipe(TypedIngredient.itemStackIngredient(slot.getItemStack()), RecipeIngredientRole.OUTPUT);
					}
				}
			}
		}
	}
}
