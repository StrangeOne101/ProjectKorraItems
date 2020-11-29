package com.projectkorra.items.command;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.attribute.Attribute;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.attribute.AttributeBuilder;
import com.projectkorra.items.attribute.AttributeModification;
import com.projectkorra.items.configuration.ConfigManager;
import com.projectkorra.items.utils.GenericUtil;
import com.projectkorra.items.utils.ItemUtils;

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
					sender.sendMessage(ChatColor.BLUE + "PKItem.findID(item) = " + (GenericUtil.convertSignedShort(PKItem.findID(stack.getItemMeta().getDisplayName()))));
				}
			} else if (args.get(0).equalsIgnoreCase("getowner")) {
				if (!PKItem.isPKItem(stack)) {
					sender.sendMessage(ChatColor.BLUE + "isPKItem(item) = false");
				/*} else if (!(stack instanceof PKItemStack)) {
					sender.sendMessage(ChatColor.BLUE + "Not a PKItemStack???");*/
				} else {
					sender.sendMessage(ChatColor.BLUE + "PKItemStack.getOwner() = " + PKItem.getOwner(stack) + " (" + Bukkit.getOfflinePlayer(PKItem.getOwner(stack)).getName() + ")");
				}
			} else if (args.get(0).equalsIgnoreCase("getdurability")) {
				if (!PKItem.isPKItem(stack)) {
					sender.sendMessage(ChatColor.BLUE + "isPKItem(item) = false");
				/*} else if (!(stack instanceof PKItemStack)) {
					sender.sendMessage(ChatColor.BLUE + "Not a PKItemStack???");*/
				} else {
					sender.sendMessage(ChatColor.BLUE + "PKItemStack.getPKIDurability() = " + PKItem.getDurability(stack));
				}
			} else if (args.get(0).equalsIgnoreCase("getitems")) {
				for (PKItem item : PKItem.INSTANCE_MAP.values()) {
					sender.sendMessage(ChatColor.BLUE + item.getName() + ": " + (GenericUtil.convertSignedShort(item.getID())));
				}
			} else if (args.get(0).equalsIgnoreCase("getidhard")) {
				if (!stack.hasItemMeta()) sender.sendMessage(ChatColor.BLUE + "No item meta found!");
				else if (stack.getItemMeta().getDisplayName() == null) sender.sendMessage(ChatColor.BLUE + "No displayname found!");
				else if (!stack.getItemMeta().getDisplayName().contains("\u00A7k")) sender.sendMessage(ChatColor.BLUE + "No magic character found!");
				else {
					String refined = stack.getItemMeta().getDisplayName().split("\u00A7k")[stack.getItemMeta().getDisplayName().split("\u00A7k").length - 1];
					if (refined.charAt(0) != '\u00A7' && refined.charAt(2) != '\u00A7') sender.sendMessage(ChatColor.BLUE + "Format invalid!");
					else {
						try {
							int b = Integer.valueOf("" + refined.charAt(1) + refined.charAt(3) + refined.charAt(5) + refined.charAt(7) , 16);
							sender.sendMessage(ChatColor.BLUE + "ID is fine! (" + b + ")");
						} catch (NumberFormatException e) {sender.sendMessage(ChatColor.BLUE + "Number format exception: " + refined.charAt(1) + refined.charAt(3));}
					}
				}
			} else if (args.get(0).equalsIgnoreCase("register")) {
				//AttributeOld.registerDefaultAttributes();
				sender.sendMessage(ChatColor.BLUE + "Complete!");
			} else if (args.get(0).equalsIgnoreCase("hexid")) {
				short id = (short) new Random().nextInt(30000);
				
				if (args.size() > 1) {
					id = Short.parseShort(args.get(1));
				}
				
				String hexed = PKItem.hideID(id).replace('\u00A7', '&');
				sender.sendMessage(ChatColor.BLUE + "ID " + id + " becomes '" + hexed + "'");
			} else if (args.get(0).equalsIgnoreCase("unhexid")) {
				sender.sendMessage(ChatColor.BLUE + "Unhexed ID is '" + PKItem.findID(args.get(1).replace('&', '\u00A7')) + "'");
			} else if (args.get(0).equalsIgnoreCase("listattr")) {
				sender.sendMessage("Found " + ChatColor.BLUE + AttributeBuilder.attributes.keySet().size() + " attributes overall");
				int page = 1;
				
				if (args.size() > 1) page = Integer.parseInt(args.get(1));
				
				for (int i = (page - 1) * 10; i <= (page - 1) * 10 + 10 && i < AttributeBuilder.attributes.size(); i++) {
					sender.sendMessage(ChatColor.BLUE + AttributeBuilder.attributes.keySet().toArray()[i].toString());
				}				
			} else if (args.get(0).equalsIgnoreCase("testitem")) {
				if (!PKItem.isPKItem(stack)) {
					sender.sendMessage(ChatColor.BLUE + "isPKItem(item) = false");
					return;
				}
				
				PKItem item = PKItem.getPKItem(stack);
				
				if (item == null) {
					sender.sendMessage(ChatColor.BLUE + "PKItem is null");
					return;
				}
				
				sender.sendMessage(ChatColor.BLUE + "ID: " + item.getName());
				sender.sendMessage(ChatColor.BLUE + "Secret ID: " + GenericUtil.convertSignedShort(item.getID()));
				sender.sendMessage(ChatColor.BLUE + "Max Durability: " + item.getMaxDurability());
				sender.sendMessage(ChatColor.BLUE + "Requirements: " + item.getRequirements().toString());
				sender.sendMessage(ChatColor.BLUE + "Usage: " + item.getUsage());
				sender.sendMessage(ChatColor.BLUE + "Player locked: " + item.isPlayedLocked());
				sender.sendMessage(ChatColor.BLUE + "Attributes: " + item.getAttributes().size());
			} else if (args.get(0).equalsIgnoreCase("active")) {
				Map<AttributeModification, ItemStack> items = ItemUtils.getAttributesActive(player);
				for (AttributeModification mod : items.keySet()) {
					ItemStack stack_ = items.get(mod);
					PKItem item = PKItem.getPKItem(stack_);
					
					sender.sendMessage(ChatColor.BLUE + item.getName() + " (" + item.getUsage() + ") - " + mod.getAttribute().getAttributeName() + ": " + mod.getModifier() + " " + mod.getValue());
				}
			} else if (args.get(0).equalsIgnoreCase("dump")) {
				List<String> lines = new ArrayList<>();
				for (String attrS : AttributeBuilder.attributes.keySet()) {
					Attribute attr = Attribute.getAttribute(attrS);
					lines.add(attr.getAttributeName() + " - " + attr.getPrefix().getPrefix());
				}

				try {
					Files.write(new File(ProjectKorraItems.plugin.getDataFolder(), "attributes.debug").toPath(), lines, StandardCharsets.UTF_8);
					sender.sendMessage(ChatColor.BLUE + "Dumped all attributes");
				} catch (IOException e) {
					e.printStackTrace();
				}


			}
		}

	}

}
