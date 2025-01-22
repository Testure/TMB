package turing.tmb.mixin.client;

import net.minecraft.client.gui.guidebook.mobs.GuidebookPageMob;
import net.minecraft.client.render.Font;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = GuidebookPageMob.class, remap = false)
public interface GuidebookPageMobMixin {
	@Invoker
	static String[] invokeCreateDescLines(Font font, String key) {
		throw new UnsupportedOperationException();
	}
}
