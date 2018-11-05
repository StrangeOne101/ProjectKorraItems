package com.projectkorra.items;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerManager {

	public static Map<Player, Short> activeItems = new HashMap<Player, Short>();
	
	/**
	 * Updates the player's held item when they change slot.
	 * 
	 * @param player
	 */
	public static void updateHeldItem(Player player) {
		if (activeItems.containsKey(player) && !PKItem.isPKItem(player.getInventory().getItemInMainHand())) {
			activeItems.remove(player);
			return;
		} 
		if (PKItem.isValidItem(player.getInventory().getItemInMainHand())) {
			ItemStack stack = player.getInventory().getItemInMainHand();
			PKItem item = PKItem.getPKItem(stack);
			if (item.canUse(player, stack)) {
				activeItems.put(player, item.getID());
			}
		}
	}
}
