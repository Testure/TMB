package turing.tmb.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.ContainerWorkbench;
import net.minecraft.core.player.inventory.InventoryCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerWorkbench.class, remap = false)
public class ContainerWorkbenchMixin {
	@Shadow
	public InventoryCrafting craftMatrix;

	@Inject(method = "onCraftGuiClosed", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/player/inventory/ContainerWorkbench;storeOrDropItem(Lnet/minecraft/core/entity/player/EntityPlayer;Lnet/minecraft/core/item/ItemStack;)V", shift = At.Shift.AFTER))
	public void fixDupe(EntityPlayer player, CallbackInfo ci, @Local int i) {
		craftMatrix.setInventorySlotContents(i, null);
	}
}
