package com.projectkorra.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.projectkorra.items.attribute.AttributeData;
import com.projectkorra.items.configuration.ConfigManager;
import com.projectkorra.items.utils.ItemUtils;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.AbilityStartEvent;

import net.md_5.bungee.api.ChatColor;

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
	public void onPlayerLogin(final PlayerLoginEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				updateItems(event.getPlayer().getInventory(), event.getPlayer());
			}
		}.runTaskLater(ProjectKorraItems.plugin, 1L);
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) return;
		
		if (PKItem.isPKItem(event.getItemInHand())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + ConfigManager.languageConfig.get().getString("Item.Use.Place"));
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.isCancelled()) return;
		
		if ((event.getInventory() instanceof AnvilInventory || event.getInventory() instanceof EnchantingInventory) && PKItem.isPKItem(event.getCurrentItem())) {
			event.setCancelled(true);
			String s = event.getInventory() instanceof AnvilInventory ? "Item.Use.Anvil" : "Item.Use.Enchant";
			event.getWhoClicked().sendMessage(ChatColor.RED + ConfigManager.languageConfig.get().getString(s));
			return;
		}
	}
	
	@EventHandler
	public void onPickupItem(InventoryPickupItemEvent event) {
		if (event.isCancelled()) return;
		
		if (PKItem.isPKItem(event.getItem().getItemStack())) {
			ItemStack pkitemstack = PKItemStack.loadFromItemStack(event.getItem().getItemStack());
			if (pkitemstack == null) {
				pkitemstack = PKItemStack.getDudItem(PKItem.findID(event.getItem().getItemStack().getItemMeta().getDisplayName()), false);
			}
			
			event.getItem().setItemStack(pkitemstack);
			//event.getItem().getItemStack().setType(pkitemstack.getType());
			//event.getItem().getItemStack().setItemMeta(pkitemstack.getItemMeta());
		}
	}
	
	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent event) {
		if (event.isCancelled()) return;
		
		Player p = event.getPlayer();
		
		if (PKItem.isPKItem(event.getItem().getItemStack())) {
			p.sendMessage(ChatColor.BLUE + "PKItem Picked Up."); //DEBUG
			ItemStack pkitemstack = PKItemStack.loadFromItemStack(event.getItem().getItemStack());
			if (pkitemstack == null) {
				p.sendMessage(ChatColor.BLUE + "Making dud item"); //DEBUG
				
				pkitemstack = PKItemStack.getDudItem(PKItem.findID(event.getItem().getItemStack().getItemMeta().getDisplayName()), p.hasPermission("bendingitems.admin"));
			}
			
			event.getItem().setItemStack(pkitemstack);
			//event.getItem().getItemStack().setType(pkitemstack.getType());
			//event.getItem().getItemStack().setItemMeta(pkitemstack.getItemMeta());
		}
	}
	
	@EventHandler
	public void onAbilityStart(AbilityStartEvent event) {
		if (event.isCancelled()) return;
		event.getAbility().getPlayer().sendMessage("Debug100");
		List<AttributeData> attributes = ItemUtils.getAttributesActive(event.getAbility().getPlayer());
		List<PKItemStack> itemsToDamage = new ArrayList<PKItemStack>();
		
		for (AttributeData attrdata : attributes) { //Sorted from high to low priority
			if (attrdata.getAttribute().isHandleOnAbilityStart()) {
				event.getAbility().getPlayer().sendMessage("Debug101");
				if (attrdata.modifyAbility((CoreAbility) event.getAbility())) { //Modify the ability from the attribute
					event.getAbility().getPlayer().sendMessage("Debug102");
					if (!itemsToDamage.contains(attrdata.getStack())) {
						itemsToDamage.add(attrdata.getStack());
						event.getAbility().getPlayer().sendMessage("Debug103");
					}
				}
			}
		}
		
		for (PKItemStack stack : itemsToDamage) {
			stack.damageItem(event.getAbility().getPlayer());
			event.getAbility().getPlayer().sendMessage("Debug104");
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
				if (pkitemstack instanceof PKItemStack) player.sendMessage(ChatColor.BLUE + "Overrid Itemstack: " + stack.getItemMeta().getDisplayName());
				else player.sendMessage(ChatColor.BLUE + "Did not become PKItemstack: " + stack.getItemMeta().getDisplayName());
				inv.getContents()[i] = pkitemstack;
			}
		}
	}
}
