package com.projectkorra.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.projectkorra.items.attribute.AttributeEvent;
import com.projectkorra.items.event.PKItemDamageEvent;

import com.projectkorra.projectkorra.event.AbilityDamageEntityEvent;
import com.strangeone101.holoitemsapi.itemevent.Position;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.attribute.AttributeModification;

import com.projectkorra.items.utils.ItemUtils;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.AbilityStartEvent;


public class PKIListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPermission("bendingitems.admin")) {
			ProjectKorraItems.errors.forEach(error -> event.getPlayer().sendMessage(ChatColor.RED + "[PKItems] " + error));
		}
	}

	@EventHandler
	public void onAbilityStart(AbilityStartEvent event) {
		if (event.isCancelled()) return;
		//event.getAbility().getPlayer().sendMessage("Debug100");
		Map<AttributeModification, Triple<PKItem, Position, ItemStack>> attributes = ItemUtils.getActive(event.getAbility().getPlayer(), AttributeEvent.ABILITY_START);
		List<ItemStack> itemsToDamage = new ArrayList<ItemStack>();

		for (AttributeModification attrdata : attributes.keySet()) { //Sorted from high to low priority
			//event.getAbility().getPlayer().sendMessage("Debug101");
			//event.getAbility().getPlayer().sendMessage(event.getAbility().getName());
			if (attrdata.performModification((CoreAbility) event.getAbility())) { //Modify the ability from the attribute
				//event.getAbility().getPlayer().sendMessage("Debug102");
				if (!itemsToDamage.contains(attributes.get(attrdata).getRight())) {
					itemsToDamage.add(attributes.get(attrdata).getRight());
					//event.getAbility().getPlayer().sendMessage("Debug103");
				}
			}
		}

		for (ItemStack stack : itemsToDamage) {
			PKItem item = PKItem.getPKItem(stack);
			PKItemDamageEvent damageevent = new PKItemDamageEvent(item, stack, event.getAbility().getPlayer());
			Bukkit.getServer().getPluginManager().callEvent(damageevent);
			if (!damageevent.isCancelled()) {
				item.damageItem(stack, 1, event.getAbility().getPlayer());
			}
			//event.getAbility().getPlayer().sendMessage("Debug104");
		}
	}

	@EventHandler
	public void onAbilityDamage(AbilityDamageEntityEvent event) {
		if (event.isCancelled()) return;
		//event.getAbility().getPlayer().sendMessage("Debug100");
		Map<AttributeModification, Triple<PKItem, Position, ItemStack>> attributes = ItemUtils.getActive(event.getAbility().getPlayer(), AttributeEvent.DAMAGE_RECEIVED);
		Map<ItemStack, Integer> itemsToDamage = new HashMap<>();

		double oldDamage = event.getDamage();

		for (AttributeModification attrdata : attributes.keySet()) { //Sorted from high to low priority
			//event.getAbility().getPlayer().sendMessage("Debug101");
			//event.getAbility().getPlayer().sendMessage(event.getAbility().getName());
			if (attrdata.performDamageModification((CoreAbility) event.getAbility(), event)) { //Modify the ability from the attribute
				ItemStack stack = attributes.get(attrdata).getRight();
				if (!itemsToDamage.containsKey(stack)) {
					itemsToDamage.put(stack, 0);
					//event.getAbility().getPlayer().sendMessage("Debug103");
				}
				int diff = (int) Math.round(oldDamage - event.getDamage());
				int old = itemsToDamage.get(stack);
				itemsToDamage.put(stack, old + (diff == 0 ? 1 : diff));
				oldDamage = event.getDamage();
			}
		}

		for (ItemStack stack : itemsToDamage.keySet()) {
			PKItem item = PKItem.getPKItem(stack);
			PKItemDamageEvent damageevent = new PKItemDamageEvent(item, stack, event.getAbility().getPlayer());
			Bukkit.getServer().getPluginManager().callEvent(damageevent);
			if (!damageevent.isCancelled()) {
				item.damageItem(stack, itemsToDamage.get(stack), event.getAbility().getPlayer());
			}
			//event.getAbility().getPlayer().sendMessage("Debug104");
		}
	}
}
