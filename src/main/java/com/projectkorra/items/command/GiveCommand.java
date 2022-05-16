package com.projectkorra.items.command;

import java.util.List;

import com.strangeone101.holoitemsapi.CustomItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.configuration.ConfigManager;

public class GiveCommand extends PKICommand {

	public GiveCommand() {
		super("give", "/bending items give <item> [amount] [player]", "This command gives you or another player a bending item.", new String[] { "give", "g", "giv" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (correctLength(sender, args.size(), 0, 3)) {
			if (!hasPermission(sender)) {
				sender.sendMessage(ChatColor.RED + ConfigManager.languageConfig.get().getString("Item.Command.NoPermission"));
				return;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + ConfigManager.languageConfig.get().getString("Item.Command.PlayerOnly"));
				return;
			}
			if (args.size() == 0) {
				String s = ChatColor.GOLD + "Item Names: " + ChatColor.YELLOW;
				String s1 = "";
				int size = CustomItemRegistry.getCustomItems().size() > 10 ? 10 : CustomItemRegistry.getCustomItems().size();
				for (int i = 0; i < size; i++) {
					if (i == size - 2) {
						s1 = s1 + " and " + ((PKItem)CustomItemRegistry.getCustomItems().values().toArray()[i]).getInternalName();
					} else {
						s1 = s1 + ", " + ((PKItem)CustomItemRegistry.getCustomItems().values().toArray()[i]).getInternalName();
					}
				}
				s = s + s1.substring(1);
				
				sender.sendMessage(s);
				if (size > 10) {
					sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.GOLD + "/bending items list [page]" + ChatColor.YELLOW + " to list them all.");
				}
				return;
			} else if (args.size() >= 1) {
				Player player = (Player)sender;
				
				PKItem item = PKItem.getItemFromName(args.get(0));
				
				if (item == null) {
					sender.sendMessage(ChatColor.RED + ConfigManager.languageConfig.get().getString("Item.Give.ItemNotFound"));
					return;
				}
				
				ItemStack itemstack = item.buildStack(player);
				
				if (args.size() >= 2) {
					try {
						int amount = Integer.parseInt(args.get(1));
						itemstack.setAmount(amount);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + ConfigManager.languageConfig.get().getString("Item.Give.NotANumber"));
						return;
					}
				}
				if (args.size() >= 3) { 
					Player target = Bukkit.getPlayer(args.get(2));
					
					if (target == null) {
						sender.sendMessage(ChatColor.RED + ConfigManager.languageConfig.get().getString("Item.Command.PlayerNotFound"));
						return;
					}
					itemstack = item.buildStack(target);
					
					target.getInventory().addItem(itemstack);
					target.sendMessage(ChatColor.YELLOW + ConfigManager.languageConfig.get().getString("Item.Give.GiveToPlayer"));
				} else {
					player.getInventory().addItem(itemstack);
				}
			}
		}
	}
}
