package com.projectkorra.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.projectkorra.items.attribute.Attribute.AttributeEvent;
import com.projectkorra.items.attribute.AttributeModification;
import com.projectkorra.items.configuration.ConfigManager;
import com.projectkorra.items.recipe.PKIRecipe;
import com.projectkorra.items.utils.GenericUtil;
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
			short id = PKItem.findID(event.getItem().getItemStack().getItemMeta().getDisplayName());
			if (!PKItem.isValidID(id)) {
				event.getItem().setItemStack(PKItem.getDudItem(id, false));
			}
		}
	}
	
	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent event) {
		if (event.isCancelled()) return;
		
		Player p = event.getPlayer();
		if (PKItem.isPKItem(event.getItem().getItemStack())) { 
			p.sendMessage(ChatColor.BLUE + "PKItem Picked Up."); //DEBUG
			short id = PKItem.findID(event.getItem().getItemStack().getItemMeta().getDisplayName());
			if (!PKItem.isValidID(id)) {
				event.getItem().setItemStack(PKItem.getDudItem(id, event.getPlayer().hasPermission("bendingitems.admin")));
				event.getPlayer().sendMessage(ChatColor.BLUE + "Found dud item with ID: " + GenericUtil.convertSignedShort(id));
			}
			else event.getPlayer().sendMessage(ChatColor.BLUE + "Updated PKItem: " + event.getItem().getItemStack().getItemMeta().getDisplayName());
		}
	}
	
	@EventHandler
	public void onAbilityStart(AbilityStartEvent event) {
		if (event.isCancelled()) return;
		//event.getAbility().getPlayer().sendMessage("Debug100");
		Map<AttributeModification, ItemStack> attributes = ItemUtils.getAttributesActive(event.getAbility().getPlayer());
		List<ItemStack> itemsToDamage = new ArrayList<ItemStack>();
		
		for (AttributeModification attrdata : attributes.keySet()) { //Sorted from high to low priority
			if (attrdata.getAttribute().getEvent() == AttributeEvent.ABILITY_START) {
				//event.getAbility().getPlayer().sendMessage("Debug101");
				//event.getAbility().getPlayer().sendMessage(event.getAbility().getName());
				if (attrdata.performModification((CoreAbility) event.getAbility())) { //Modify the ability from the attribute
					event.getAbility().getPlayer().sendMessage("Debug102");
					if (!itemsToDamage.contains(attributes.get(attrdata))) {
						itemsToDamage.add(attributes.get(attrdata));
						event.getAbility().getPlayer().sendMessage("Debug103");
					}
				}
			}
		}
		
		for (ItemStack stack : itemsToDamage) {
			PKItem item = PKItem.getPKItem(stack);
			item.damageItem(event.getAbility().getPlayer(), stack);
			event.getAbility().getPlayer().sendMessage("Debug104");
		}
	}
	
	public static void updateItems(Inventory inv, Player player) {
		for (int i = 0; i < inv.getStorageContents().length; i++) {
			ItemStack stack = inv.getContents()[i];
			if (stack == null) continue;
			//If it is a PKItem that hasn't been updated since server start
			if (PKItem.isPKItem(stack)) { 
				
				short id = PKItem.findID(stack.getItemMeta().getDisplayName());
				if (!PKItem.isValidID(id)) {
					inv.setItem(i, PKItem.getDudItem(id, player.hasPermission("bendingitems.admin")));
					player.sendMessage(ChatColor.BLUE + "Found dud item with ID: " + GenericUtil.convertSignedShort(id));
				}
				
				else player.sendMessage(ChatColor.BLUE + "Updated PKItem: " + stack.getItemMeta().getDisplayName());
			}
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		if (event.isCancelled()) return;
		
		if (event.getRecipe() instanceof PKIRecipe) {
			PKItem pkitem = ((PKIRecipe)event.getRecipe()).getItem();
			ItemStack stack = event.getCurrentItem().clone();
			PKItem.setOwner(stack, event.getWhoClicked().getUniqueId()); //Set the owner
			event.setCurrentItem(stack); //Update the item crafted
		}
	}
	
	@EventHandler
	public void preCraft(PrepareItemCraftEvent event) {
		if (event.getRecipe() instanceof PKIRecipe && event.getView().getPlayer() instanceof Player) {
			CraftingInventory inv = event.getInventory();
			PKItem pkitem = ((PKIRecipe)event.getRecipe()).getItem();
			inv.setResult(pkitem.buildItem((Player)event.getView().getPlayer()));
		}
	}
}
