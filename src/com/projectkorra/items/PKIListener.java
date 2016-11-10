package com.projectkorra.items;

import org.bukkit.entity.Player;
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
		
		updateItems(inv, (Player) event.getPlayer());
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		
		updateItems(event.getPlayer().getInventory(), event.getPlayer());
	}
	
	@EventHandler
	public void onPickupItem(InventoryPickupItemEvent event) {
		if (event.isCancelled()) return;
		if (!(event.getInventory() instanceof PlayerInventory)) return;
		
		Player p = (Player) ((PlayerInventory)event.getInventory()).getHolder();
		
		if (PKItem.isPKItem(event.getItem().getItemStack())) {
			ItemStack pkitemstack = PKItemStack.loadFromItemStack(event.getItem().getItemStack());
			if (pkitemstack == null) {
				pkitemstack = PKItemStack.getDudItem(PKItem.findID(event.getItem().getItemStack().getItemMeta().getDisplayName()), p.hasPermission("bendingitems.admin"));
			}
			
			event.getItem().setItemStack(pkitemstack);
			
		}
	}
	
	public static void updateItems(Inventory inv, Player player) {
		for (int i = 0; i < inv.getContents().length; i++) {
			ItemStack stack = inv.getContents()[i];
			if (stack == null) continue;
			//If it is a PKItem that hasn't been updated since server start
			if (PKItem.isPKItem(stack)) { 
				ItemStack pkitemstack = PKItemStack.loadFromItemStack(stack);
				if (pkitemstack == null) {
					pkitemstack = PKItemStack.getDudItem(PKItem.findID(stack.getItemMeta().getDisplayName()), player.hasPermission("bendingitems.admin"));
				}
				inv.getContents()[i] = pkitemstack;
			}
		}
	}
}
