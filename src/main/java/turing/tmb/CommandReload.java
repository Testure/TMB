package turing.tmb;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class CommandReload implements CommandManager.CommandRegistry {
	@Override
	public void register(CommandDispatcher<CommandSource> commandDispatcher) {
		commandDispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("reload").executes((c) -> {
			TMB.reloadTMB();
			return 1;
		}));
	}
}
