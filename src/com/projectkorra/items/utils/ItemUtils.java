package com.projectkorra.items.utils;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.PKItem.Usage;
import com.projectkorra.items.PKItemStack;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.ARCHIVE.AttributeList;
import com.projectkorra.items.attribute.Attribute;
import com.projectkorra.items.attribute.AttributeData;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ItemUtils {
	
	/**
	 * A map of player names that holds their current bending potion effects.
	 **/
	//public static final ConcurrentHashMap<String, ConcurrentHashMap<String, Attribute>> currentBendingEffects = new ConcurrentHashMap<String, ConcurrentHashMap<String, Attribute>>();


	/**
	 * <b>NO LONGER USED.</b>
	 * Returns a list of all of the players effectable items.
	 * 
	 * @param player the player with the items
	 * @return a list of items
	 */
	@Deprecated
	public static ArrayList<ItemStack> getPlayerEquipment(Player player) {
		ArrayList<ItemStack> istacks = new ArrayList<ItemStack>();
		for (ItemStack istack : player.getInventory().getArmorContents())
			istacks.add(istack);
		istacks.add(player.getItemInHand());
		return istacks;
	}

	/**
	 * <b>NO LONGER USED.</b>
	 * Returns a list of the players armor and in hand item, ONLY if the item is
	 * usable for the specific player. If the item has the "HoldOnly",
	 * "WearOnly", or "RequireElement" stat, then this method may ignore some
	 * items.
	 * 
	 * @param player the player that has equipment
	 * @return a list of the equipment
	 */
	/*@Deprecated
	public static ArrayList<ItemStack> getPlayerValidEquipment(Player player) {
		if (player == null) {
			return new ArrayList<ItemStack>();
		}
		
		ArrayList<ItemStack> equipment = getPlayerEquipment(player);

		/*
		 * Get any inventory items that contain the "AllowFromInventory" stat.
		 */
		/*PlayerInventory playerInv = player.getInventory();
		for (ItemStack invItem : playerInv) {
			if (invItem == null || invItem.getType() == Material.AIR)
				continue;

			PKItem citem = PKItem.getCustomItem(invItem);
			if (citem != null && !equipment.contains(invItem) && citem.getBooleanAttributeValue("AllowFromInventory")) {
				equipment.add(invItem);
			}
		}

		for (int i = 0; i < equipment.size(); i++) {
			ItemStack istack = equipment.get(i);
			PKItem citem = PKItem.getCustomItem(istack);
			if (citem == null)
				continue;

			boolean keepItem = true;
			if (!hasValidCharges(istack)) {
				keepItem = false;
			} else if (citem.getBooleanAttributeValue("HoldOnly") && !istack.equals(player.getItemInHand())) {
				keepItem = false;
			} else if (citem.getBooleanAttributeValue("WearOnly") && istack.equals(player.getItemInHand())) {
				keepItem = false;
			} else if (!AttributeUtils.hasRequiredElement(player, citem)) {
				keepItem = false;
			} else if (!AttributeUtils.hasRequiredWorld(player, citem)) {
				keepItem = false;
			} else if (!AttributeUtils.hasRequiredPermission(player, citem)) {
				keepItem = false;
			}

			if (!keepItem) {
				equipment.remove(i);
				i--;
				continue;
			}
		}
		return equipment;
	}*/

	/**
	 * <b>NO LONGER NEEDED.</b>
	 * Checks if an item has a usable amount of charges, or doesn't require
	 * charges at all. 
	 * 
	 * @param item the custom item
	 * @return true if the item has a usable amount of charges, or no charges
	 */
	@Deprecated
	public static boolean hasValidCharges(ItemStack item) {
		boolean validCharges = true;
		try {
			for (String line : item.getItemMeta().getLore()) {
				if (line.startsWith(AttributeList.CHARGES_STR) || line.startsWith(AttributeList.CLICK_CHARGES_STR) || line.startsWith(AttributeList.SNEAK_CHARGES_STR)) {
					String tmpStr = line.substring(line.indexOf(": ") + 1, line.length()).trim();
					int value = Integer.parseInt(tmpStr);
					if (value <= 0)
						validCharges = false;
					else {
						validCharges = true;
						break;
					}
				}
			}
		}
		catch (Exception e) {
		}
		return validCharges;
	}

	/**
	 * A quick way to set the lore of an item
	 * 
	 * @param istack the item that will have its lore set
	 * @param lore the strings that make up the lore
	 * @return true if the lore was set correctly
	 */
	@Deprecated
	public static boolean setLore(ItemStack istack, List<String> lore) {
		if (istack == null || lore == null)
			return false;
		ItemMeta meta = istack.getItemMeta();
		if (meta == null)
			return false;
		meta.setLore(lore);
		istack.setItemMeta(meta);
		return true;
	}
	
	/**
	 * OnActionEffects are PotionEffects and BendingAffects that get added to the players Attribute
	 * map for a limited amount of time.
	 * 
	 * @param player the player receiving the stat modifications
	 * @param type the type of action that caused this to trigger
	 */
	/*public static void updateOnActionEffects(Player player, Action type) {
		if (player == null)
			return;

		ArrayList<ItemStack> istacks = ItemUtils.getPlayerValidEquipment(player);
		String[] validAttribs = null;
		if (type == Action.LEFT_CLICK)
			validAttribs = new String[] { "Effects", "ClickEffects" };
		else if (type == Action.RIGHT_CLICK)
			validAttribs = new String[] { "Effects", "ClickEffects" };
		else if (type == Action.SHIFT)
			validAttribs = new String[] { "Effects", "SneakEffects" };
		else if (type == Action.CONSUME)
			validAttribs = new String[] { "Effects", "ConsumeEffects" };
		else
			validAttribs = new String[] { "Effects" };

		boolean effectAdded = false;
		for (ItemStack istack : istacks) {
			PKItem citem = PKItem.getCustomItem(istack);
			if (citem == null)
				continue;

			for (Attribute att : citem.getAttributes())
				for (String allowedEff : validAttribs)
					if (att.getName().equalsIgnoreCase(allowedEff)) {
						ArrayList<PotionEffect> potEffects = AttributeUtils.parsePotionEffects(att);
						ArrayList<Attribute> bendEffects = AttributeUtils.parseBendingEffects(att);

						for (PotionEffect pot : potEffects)
							player.addPotionEffect(pot, true);
						effectAdded = true;

						for (Attribute effect : bendEffects) {
							if (!currentBendingEffects.containsKey(player.getName()))
								currentBendingEffects.put(player.getName(), new ConcurrentHashMap<String, Attribute>());
							effect.setTime(System.currentTimeMillis());
							ConcurrentHashMap<String, Attribute> playerEffList = currentBendingEffects.get(player.getName());
							playerEffList.put(effect.getName(), effect);
						}
					}
		}
		if (effectAdded)
			AttributeUtils.decreaseCharges(player, type);
	}*/
	
	/**
	 * Handles the specific stat "WaterSource" and in the future "MetalSource". These stats cause
	 * specific temporary items to spawn inside of the players inventory.
	 * 
	 * @param player the player with the WaterSource stat
	 * @param attrib the name of the stat "WaterSource" or "MetalSource"
	 * @param istack the ItemStack that will temporarily spawn
	 */
	/*public static void handleItemSource(Player player, String attrib, ItemStack istack) {
		ConcurrentHashMap<String, Double> attribs = AttributeUtils.getSimplePlayerAttributeMap(player);
		if (attribs.containsKey(attrib) && attribs.get(attrib) == 1) {
			final PlayerInventory inv = player.getInventory();
			int slot = -1;
			for (int i = 9; i < inv.getSize(); i++) {
				if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
					slot = i;
					break;
				}
			}
			if (slot < 0)
				slot = inv.first(Material.AIR);
			if (slot >= 0) {
				inv.setItem(slot, istack);
				player.updateInventory();
			} else
				return;

			final int fslot = slot;
			new BukkitRunnable() {
				public void run() {
					inv.setItem(fslot, new ItemStack(Material.AIR));
				}
			}.runTaskLater(ProjectKorraItems.plugin, 10);
		}
	}*/
	
	/**
	 * Gets a list of all attributes that are active for the provided player
	 * 
	 * @param player The player to check
	 * @return A sorted list of all attributes. From highest priority to lowest
	 */
	public static List<AttributeData> getAttributesActive(Player player) {
		
		List<AttributeData> data = new ArrayList<AttributeData>();
		Set<Byte> usedItems = new HashSet<Byte>();
		
		for (int i = 0; i < player.getInventory().getArmorContents().length; i++) {
			ItemStack stack = player.getInventory().getArmorContents()[i];
			if (stack == null) continue;
			if (PKItem.isPKItem(stack) && stack instanceof PKItemStack) { //If it isn't a PKItemStack, it's not a updated item. Only use updated items.
				PKItem item = PKItem.getPKItem(stack);
				if (item.getUsage() == Usage.WEARABLE && !usedItems.contains(item.getID())) {
					for (Attribute attr : item.getAttributes().keySet()) {
						data.add(new AttributeData(attr, (PKItemStack)stack, item.getAttributes().get(attr)));
					}
					usedItems.add(item.getID());
				}
			}
		}
		
		for (int i = 0; i < player.getInventory().getStorageContents().length; i++) {
			ItemStack stack = player.getInventory().getStorageContents()[i];
			if (stack == null) continue;
			if (PKItem.isPKItem(stack) && stack instanceof PKItemStack) { //If it isn't a PKItemStack, it's not a updated item. Only use updated items.
				PKItem item = PKItem.getPKItem(stack);
				if (item.getUsage() == Usage.PRESENT && !usedItems.contains(item.getID())) {
					for (Attribute attr : item.getAttributes().keySet()) {
						data.add(new AttributeData(attr, (PKItemStack)stack, item.getAttributes().get(attr)));
					}
					usedItems.add(item.getID());
				}
			}
		}
		
		for (ItemStack stack : new ItemStack[] {player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand()}) {
			if (stack == null) continue;
			if (PKItem.isPKItem(stack) && stack instanceof PKItemStack) { //If it isn't a PKItemStack, it's not a updated item. Only use updated items.
				PKItem item = PKItem.getPKItem(stack);
				if (item.getUsage() == Usage.HOLD && !usedItems.contains(item.getID())) {
					player.sendMessage("Debug001");
					for (Attribute attr : item.getAttributes().keySet()) {
						data.add(new AttributeData(attr, (PKItemStack)stack, item.getAttributes().get(attr)));
					}
					usedItems.add(item.getID());
				}
			}
		}	
		
		Collections.sort(data, new Comparator<AttributeData>() {
			@Override
			public int compare(AttributeData o1, AttributeData o2) {
				return o1.getAttribute().getPriority().power - o2.getAttribute().getPriority().power;
			}
		});
		return data;
	}
}
