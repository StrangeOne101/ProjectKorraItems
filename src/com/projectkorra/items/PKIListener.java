package com.projectkorra.items;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PKIListener implements Listener
{
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.isCancelled()) return;
		
		//Don't want to fire this event for when the player opens their inventory
		if (event.getInventory() instanceof PlayerInventory) return; 
		
		Inventory inv = event.getInventory();
		
		updateItems(inv);
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		
		updateItems(event.getPlayer().getInventory());
	}
	
	@EventHandler
	public void onPickupItem(InventoryPickupItemEvent event) {
		if (event.isCancelled()) return;
		
		if (PKItem.isPKItem(event.getItem().getItemStack()) && !(event.getItem().getItemStack() instanceof PKItemStack)) {
			event.getItem().setItemStack(PKItemStack.loadFromItemStack(event.getItem().getItemStack()));
			
		}
	}
	
	private void updateItems(Inventory inv) {
		for (int i = 0; i < inv.getContents().length; i++) {
			ItemStack stack = inv.getContents()[i];
			
			//If it is a PKItem that hasn't been updated since server start
			if (PKItem.isPKItem(stack) && !(stack instanceof PKItemStack)) { 
				PKItemStack pkitemstack = PKItemStack.loadFromItemStack(stack);
				inv.getContents()[i] = pkitemstack;
			}
		}
	}
}
