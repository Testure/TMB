package turing.tmb;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.Objects;

public class CommandReload extends Command {
	public CommandReload(String name, String... alts) {
		super(name, alts);
	}

	@Override
	public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
		if(strings.length < 1) {
			return false;
		}
		if(strings.length == 1 && Objects.equals(strings[0], "reload")){
			commandSender.sendMessage("TMB reloaded!");
			TMB.reloadTMB();
			return true;
		}
		return false;
	}

	@Override
	public boolean opRequired(String[] strings) {
		return true;
	}

	@Override
	public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
		commandSender.sendMessage("/tmb reload");
	}
}
