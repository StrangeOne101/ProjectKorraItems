package com.projectkorra.items.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.items.PKIListener;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.configuration.ConfigManager;

import net.md_5.bungee.api.ChatColor;

public class ReloadCommand extends PKICommand {

	public ReloadCommand() {
		super("reload", "/bending items reload", "This command reloads Items configuration.", new String[] {"r"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (correctLength(sender, args.size(), 0, 0)) {
			if (!hasPermission(sender)) {
				//sender.sendMessage(Messages.NO_PERM);
				return;
			}
			new ConfigManager();
			//sender.sendMessage(Messages.CONFIG_RELOADED);
			
			ProjectKorraItems.errors.clear();
			
			for (Player p : Bukkit.getOnlinePlayers()) {
				for (String e : ProjectKorraItems.errors) {
					p.sendMessage(ChatColor.RED + "[PKItems] " + e);
				}
				//PKIListener.updateItems(p.getInventory(), p);
			}
		}
	}

}
