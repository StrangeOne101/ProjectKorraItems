package com.projectkorra.items.event;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.PKItem;

public class PKItemEquipEvent extends PKItemEvent {
	
	private Player player;
	private ItemStack itemstack;
	
	public PKItemEquipEvent(Player player, PKItem item, ItemStack stack) {
		this.player = player;
		this.itemstack = stack;
		this.setItem(item);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public ItemStack getItemstack() {
		return itemstack;
	}

}
