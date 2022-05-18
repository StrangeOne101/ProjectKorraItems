package com.projectkorra.items.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.projectkorra.projectkorra.command.PKCommand;

public class BaseCommand extends PKCommand {

	public BaseCommand() {
		super("items", "/bending items [...]", "Base command for the Items side plugin", new String[] {"items"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (args.size() == 0 || args.get(0).equalsIgnoreCase("help")) {
			//sender.sendMessage(ChatColor.RED + "/bending items equip " + ChatColor.YELLOW +  "Equip an item.");
			sender.sendMessage(ChatColor.RED + "/bending items give <Item> [Amount] [Player] " + ChatColor.YELLOW + "Give an item.");
			sender.sendMessage(ChatColor.RED + "/bending items list " + ChatColor.YELLOW + "List all items.");
			sender.sendMessage(ChatColor.RED + "/bending items stats " + ChatColor.YELLOW +  "List item stats.");
			
			return;
		}
		
		for (PKICommand command : PKICommand.instances.values()) {
			if (Arrays.asList(command.getAliases()).contains(args.get(0).toLowerCase())) {
				command.execute(sender, args.subList(1, args.size()));
			}
		}
	}

	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() < 1) return List.of("give", "list", "stats");

		for (PKICommand command : PKICommand.instances.values()) {
			if (Arrays.asList(command.getAliases()).contains(args.get(0).toLowerCase())) {
				return command.getTabCompletion(sender, args.subList(1, args.size()));
			}
		}
		return new ArrayList<>();
	}
}
