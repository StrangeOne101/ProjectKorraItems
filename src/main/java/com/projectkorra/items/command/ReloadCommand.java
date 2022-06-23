package com.projectkorra.items.command;

import java.util.List;

import com.projectkorra.items.configuration.ConfigManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import com.projectkorra.items.ProjectKorraItems;



public class ReloadCommand extends PKICommand {

	public ReloadCommand() {
		super("reload", "/bending items reload", "This command reloads Items configuration.", new String[] {"r"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender)) {
			sender.sendMessage(ChatColor.RED + ChatColor.translateAlternateColorCodes('&', ConfigManager.languageConfig.get().getString("Command.NoPermission")));
			return;
		}

		ProjectKorraItems.plugin.reload();
		sender.sendMessage(ChatColor.YELLOW + "ProjectKorraItems reloaded!");

	}
}
