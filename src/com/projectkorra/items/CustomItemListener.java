package com.projectkorra.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface CustomItemListener {
	
	public class ChangeEvent {

		public ChangeEvent(Player player, ItemStack itemstack, boolean active) {
			this.isActive = active;
			this.player = player;
			this.itemstack = itemstack;
		}
		
		private boolean isActive;
		private Player player;
		private ItemStack itemstack;
		
		public boolean isActive() {
			return isActive;
		}
		
		public Player getPlayer() {
			return player;
		}
		
		public ItemStack getItemStack() {
			return itemstack;
		}
	}
	
	public void onChange(ChangeEvent event);
	
	public enum UseType {};
}
