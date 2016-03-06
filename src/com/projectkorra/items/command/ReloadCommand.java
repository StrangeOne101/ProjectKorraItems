package com.projectkorra.items.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.projectkorra.items.ConfigManager;
import com.projectkorra.items.Messages;
import com.projectkorra.projectkorra.command.PKCommand;

public class ReloadCommand extends PKCommand {

	public ReloadCommand() {
		super("reload", "/b reload", "This command reloads items configuration.", Messages.RELOAD_ALIAS);
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (correctLength(sender, args.size(), 0, 0)) {
			if (hasPermission(sender)) {
				new ConfigManager();
				sender.sendMessage(Messages.CONFIG_RELOADED);
			}
			sender.sendMessage(Messages.NO_PERM);
		}
	}

}
