package com.projectkorra.items.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.PKItemStack;
import com.projectkorra.items.configuration.ConfigManager;

public class TestCommand extends PKICommand {

	public TestCommand() {
		super("test", "/bending items test <ispkitem/getid/getowner/getdurability/getitems>", "Allows the user to debug", new String[] {"test"});
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender)) {
			sender.sendMessage(ChatColor.RED + ConfigManager.languageConfig.get().getString("Item.Command.NoPermission"));
			return;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + ConfigManager.languageConfig.get().getString("Item.Command.PlayerOnly"));
			return;
		}
		
		Player player = (Player) sender;
		ItemStack stack = player.getInventory().getItemInMainHand();
		
		if (args.size() > 0) {
			if (args.get(0).equalsIgnoreCase("ispkitem")) {
				sender.sendMessage(ChatColor.BLUE + "isPKItem(item) = " + PKItem.isPKItem(stack));
			} else if (args.get(0).equalsIgnoreCase("getid")) {
				if (!PKItem.isPKItem(stack)) {
					sender.sendMessage(ChatColor.BLUE + "isPKItem(item) = false");
				} else {
					sender.sendMessage(ChatColor.BLUE + "PKItem.findID(item) = " + (((int)PKItem.findID(stack.getItemMeta().getDisplayName())) + 128));
				}
			} else if (args.get(0).equalsIgnoreCase("getowner")) {
				if (!PKItem.isPKItem(stack)) {
					sender.sendMessage(ChatColor.BLUE + "isPKItem(item) = false");
				} else if (!(stack instanceof PKItemStack)) {
					sender.sendMessage(ChatColor.BLUE + "Not a PKItemStack???");
				} else {
					sender.sendMessage(ChatColor.BLUE + "PKItemStack.getOwner() = " + ((PKItemStack)stack).getOwner() + " (" + Bukkit.getOfflinePlayer(((PKItemStack)stack).getOwner()).getName() + ")");
				}
			} else if (args.get(0).equalsIgnoreCase("getdurability")) {
				if (!PKItem.isPKItem(stack)) {
					sender.sendMessage(ChatColor.BLUE + "isPKItem(item) = false");
				} else if (!(stack instanceof PKItemStack)) {
					sender.sendMessage(ChatColor.BLUE + "Not a PKItemStack???");
				} else {
					sender.sendMessage(ChatColor.BLUE + "PKItemStack.getPKIDurability() = " + ((PKItemStack)stack).getPKIDurability());
				}
			} else if (args.get(0).equalsIgnoreCase("getitems")) {
				for (PKItem item : PKItem.INSTANCE_MAP.values()) {
					sender.sendMessage(ChatColor.BLUE + item.getName() + ": " + (((int)item.getID()) + 128));
				}
			}  else if (args.get(0).equalsIgnoreCase("getidhard")) {
				if (!stack.hasItemMeta()) sender.sendMessage(ChatColor.BLUE + "No item meta found!");
				else if (stack.getItemMeta().getDisplayName() == null) sender.sendMessage(ChatColor.BLUE + "No displayname found!");
				else if (!stack.getItemMeta().getDisplayName().contains("§k")) sender.sendMessage(ChatColor.BLUE + "No magic character found!");
				else {
					String refined = stack.getItemMeta().getDisplayName().split("§k")[stack.getItemMeta().getDisplayName().split("§k").length - 1];
					if (refined.charAt(0) != '§' && refined.charAt(2) != '§') sender.sendMessage(ChatColor.BLUE + "Format invalid!");
					else {
						try {
							Integer.valueOf("" + refined.charAt(1) + refined.charAt(3), 16);
							sender.sendMessage(ChatColor.BLUE + "ID is fine!");
						} catch (NumberFormatException e) {sender.sendMessage(ChatColor.BLUE + "Number format exception: " + refined.charAt(1) + refined.charAt(3));}
					}
				}
			}
		}

	}

}
