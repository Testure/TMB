package turing.tmb.mixin.client;

import net.minecraft.client.gui.guidebook.mobs.MobPage;
import net.minecraft.client.render.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = MobPage.class, remap = false)
public interface GuidebookPageMobMixin {
	@Invoker
	static String[] invokeCreateDescLines(FontRenderer font, String key) {
		throw new UnsupportedOperationException();
	}
}
