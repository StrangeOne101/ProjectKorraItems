package com.projectkorra.items.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.projectkorra.items.attribute.AttributeEvent;
import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitemsapi.itemevent.EventCache;
import com.strangeone101.holoitemsapi.itemevent.EventContext;
import com.strangeone101.holoitemsapi.itemevent.Position;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.PKItem;

import com.projectkorra.items.attribute.AttributeModification;


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
	/*public static Map<AttributeModification, ItemStack> getAttributesActive(Player player) {
		
		Map<AttributeModification, ItemStack> data = new HashMap<AttributeModification, ItemStack>();
		Set<PKItem> usedItems = new HashSet<PKItem>();
		
		for (int i = 0; i < player.getInventory().getArmorContents().length; i++) {
			ItemStack stack = player.getInventory().getArmorContents()[i];
			if (stack == null) continue;
			if (PKItem.isPKItem(stack) && PKItem.isValidItem(stack)) { //If it isn't a valid PKItem that can be used, don't use it.
				PKItem item = PKItem.getPKItem(stack);
				if (item.getUsage() == Usage.WEARABLE && !usedItems.contains(item) && item.getRequirements().meets(player)) { //If not used & a player can use it
					for (AttributeModification attr : item.getAttributes().values()) {
						data.put(attr, stack);
					}
					usedItems.add(item);
				}
			}
		}
		
		for (int i = 0; i < player.getInventory().getStorageContents().length; i++) {
			ItemStack stack = player.getInventory().getStorageContents()[i];
			if (stack == null) continue;
			if (PKItem.isPKItem(stack) && PKItem.isValidItem(stack)) { //If it isn't a PKItemStack, it's not a updated item. Only use updated items.
				PKItem item = PKItem.getPKItem(stack);
				if (item.getUsage() == Usage.INVENTORY && !usedItems.contains(item) && item.getRequirements().meets(player)) {
					for (AttributeModification attr : item.getAttributes().values()) {
						data.put(attr, stack);
					}
					usedItems.add(item);
				}
			}
		}
		
		for (ItemStack stack : new ItemStack[] {player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand()}) {
			if (stack == null) continue;
			if (PKItem.isPKItem(stack) && PKItem.isValidItem(stack)) { //If it isn't a PKItemStack, it's not a updated item. Only use updated items.
				PKItem item = PKItem.getPKItem(stack);
				if (item.getUsage() == Usage.HOLD && !usedItems.contains(item) && item.getRequirements().meets(player)) {
					//player.sendMessage("Debug001");
					for (AttributeModification attr : item.getAttributes().values()) {
						data.put(attr, stack);
					}
				}
			}
		}	
		
		/*Collections.sort(data, new Comparator<AttributeModification>() {
			@Override
			public int compare(AttributeModification o1, AttributeModification o2) {
				return o1.getAttribute().getPrefix().getPriority().getPower() - o2.getAttribute().getPrefix().getPriority().getPower();
			}
		});*/
		//return data;
	//}

	public static Map<AttributeModification, Triple<PKItem, Position, ItemStack>> getActive(Player player) {
		return getActive(player, null);
	}

	public static Map<AttributeModification, Triple<PKItem, Position, ItemStack>> getActive(Player player, AttributeEvent eventType) {
		List<EventContext> contexts = new ArrayList<>();
		Set<CustomItem> items = new HashSet<>();
		SortedSet<Triple<PKItem, Position, ItemStack>> sorted = new TreeSet<>(Comparator.comparingInt(p -> p.getMiddle().ordinal()));

		for (Map.Entry<CustomItem, Map<Player, Map<Integer, Pair<ItemStack, Position>>>> positionsEntries : EventCache.POSITIONS_BY_ITEM.entrySet()) {
			Map<Integer, Pair<ItemStack, Position>> playerSlotCache = (Map<Integer, Pair<ItemStack, Position>>)((Map)positionsEntries.getValue()).get(player);
			if (playerSlotCache == null || playerSlotCache.isEmpty())
				continue;
			PKItem customItem = (PKItem) positionsEntries.getKey();
			if (customItem.getPKIAttributes().isEmpty()) continue;

			Triple<PKItem, Position, ItemStack> triple = new ImmutableTriple<>(customItem, Position.INVENTORY, null);

			for (Pair<ItemStack, Position> pairs : playerSlotCache.values()) {
				if (customItem.getUsage().toConditions().matches(pairs.getRight()) && customItem.canUse(player, pairs.getLeft())) {
					if (triple.getMiddle().ordinal() > pairs.getRight().ordinal())  {
						triple = new ImmutableTriple<>(customItem, pairs.getRight(), pairs.getLeft());
					}
					//TODO contexts.add(new EventContext(player, customItem, pairs.getLeft(), pairs.getRight()));
					items.add(customItem);
				}
			}
			if (triple.getRight() != null) sorted.add(triple);
		}

		HashMap<AttributeModification, Triple<PKItem, Position, ItemStack>> sortedMap = new LinkedHashMap<>();
		AttributeEvent[] events = eventType == null ? AttributeEvent.values() : new AttributeEvent[] {eventType};
		for (Triple<PKItem, Position, ItemStack> trip : sorted) {
			for (AttributeEvent event : events) {
				trip.getLeft().getPKIAttributes().get(event).forEach((v) -> sortedMap.put(v, trip));
			}
		}
		return sortedMap;
	}
}
