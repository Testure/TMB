package turing.tmb.mixin.client;

import net.minecraft.client.gui.guidebook.mobs.GuidebookPageMob;
import net.minecraft.client.render.font.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = GuidebookPageMob.class, remap = false)
public interface GuidebookPageMobMixin {
	@Invoker
	static String[] invokeCreateDescLines(FontRenderer font, String key) {
		throw new UnsupportedOperationException();
	}
}
