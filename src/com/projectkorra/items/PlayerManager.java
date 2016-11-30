package com.projectkorra.items;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class PlayerManager {

	public static Map<Player, Byte> activeItems = new HashMap<Player, Byte>();
	
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
		if (PKItem.isPKItem(player.getInventory().getItemInMainHand()) && player.getInventory().getItemInMainHand() instanceof PKItemStack) {
			PKItemStack stack = (PKItemStack) player.getInventory().getItemInMainHand();
			if (stack.canUse(player)) {
				activeItems.put(player, stack.getItem().getID());
			}
		}
	}
}
