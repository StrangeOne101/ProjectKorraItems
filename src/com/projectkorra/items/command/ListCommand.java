package com.projectkorra.items.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.configuration.ConfigManager;

public class ListCommand extends PKICommand {

	public ListCommand() {
		super("list", "/bending items list", "This command lists all bending items and their recipies.", new String[] {"l", "li", "list"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (this.correctLength(sender, args.size(), 0, 1)) {
			if (!hasPermission(sender)) {
				sender.sendMessage(ChatColor.RED + ConfigManager.languageConfig.get().getString("Item.Command.NoPermission"));
				return;
			}
			/*if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + ConfigManager.languageConfig.get().getString("Item.Command.PlayerOnly"));
				return;
			}*/
			
			try {
				int page = args.size() > 0 ? Integer.parseInt(args.get(0)) : 1;
				int maxpage = (PKItem.INSTANCE_MAP.values().size() - 1) / 10 + 1;
				if (page < 1) page = 1;
				else if (page > maxpage) page = maxpage;
				String s = ChatColor.GOLD + "Item Names (Page " + page + " of " + maxpage + "): " + ChatColor.YELLOW;
				String s1 = "";
				int max = (page + 1) * 10;
				max = max > PKItem.INSTANCE_MAP.values().size() ? PKItem.INSTANCE_MAP.values().size() : max;
				for (int i = (page - 1) * 10; i < max; i++) {
					if (i == max - 2) {
						s1 = s1 + " and " + ((PKItem)PKItem.INSTANCE_MAP.values().toArray()[i]).getName();
					} else {
						s1 = s1 + ", " + ((PKItem)PKItem.INSTANCE_MAP.values().toArray()[i]).getName();
					}
				}
				s = s + s1.substring(2);
				
				sender.sendMessage(s);
				if (maxpage > page) {
					sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.GOLD + "/bending items list " + (page + 2) + ChatColor.YELLOW + " to list the next page!");
				}
			}
			catch (NumberFormatException e) {
				
			}
			
			
			/*if (args.size() >= 0) {
				if (args.size() == 1) {
					for (String s : Messages.STATS_ALIAS) {
						if (args.get(0) == s) {
							show = true;
						}
					}
				}
				PKIDisplay d = new PKIDisplay(player, show);
				d.createInventory();
			}*/
		}
	}
}
