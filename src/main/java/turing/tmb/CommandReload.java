package turing.tmb;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandReload implements CommandManager.CommandRegistry {
	@Override
	@SuppressWarnings("unchecked")
	public void register(CommandDispatcher<CommandSource> commandDispatcher) {
		commandDispatcher.register((LiteralArgumentBuilder) LiteralArgumentBuilder.literal("reload").executes((c) -> {
			TMB.reloadTMB();
			return 1;
		}));
	}
}
