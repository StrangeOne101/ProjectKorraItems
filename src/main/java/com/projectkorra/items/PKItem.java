package com.projectkorra.items;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.projectkorra.items.event.PKItemDamageEvent;
import com.projectkorra.items.utils.ItemUtils;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.AbilityStartEvent;
import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitemsapi.CustomItemRegistry;
import com.strangeone101.holoitemsapi.HoloItemsAPI;
import com.strangeone101.holoitemsapi.Keys;
import com.strangeone101.holoitemsapi.Properties;
import com.strangeone101.holoitemsapi.itemevent.ActiveConditions;
import com.strangeone101.holoitemsapi.itemevent.EventContext;
import com.strangeone101.holoitemsapi.itemevent.ItemEvent;
import com.strangeone101.holoitemsapi.util.UUIDTagType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.projectkorra.items.attribute.Attribute;
import com.projectkorra.items.attribute.AttributeModification;
import com.projectkorra.items.attribute.Requirements;
import com.projectkorra.items.configuration.ConfigManager;
import com.projectkorra.items.utils.GenericUtil;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PKItem extends CustomItem {

	protected Usage usage;
	protected Requirements req = new Requirements();
	protected Map<Attribute, AttributeModification> attributes = new HashMap<Attribute, AttributeModification>();
	
	public PKItem(String name, Material material) {
		super(name, material);

		this.usage = Usage.NONE;
		this.req = new Requirements(); //Blank requirements

		this.addVariable("owner", data -> {
			if (!data.has(HoloItemsAPI.getKeys().CUSTOM_ITEM_OWNER, PersistentDataType.STRING)) return "(None)";
			return data.get(HoloItemsAPI.getKeys().CUSTOM_ITEM_OWNER_NAME, PersistentDataType.STRING);
		});

		this.addVariable("durability", data -> {
			if (!data.has(HoloItemsAPI.getKeys().CUSTOM_ITEM_DURABILITY, PersistentDataType.INTEGER)) return "";
			return data.get(HoloItemsAPI.getKeys().CUSTOM_ITEM_DURABILITY, PersistentDataType.INTEGER) + "";
		});

		this.addVariable("maxdurability", data -> {
			if (!data.has(HoloItemsAPI.getKeys().CUSTOM_ITEM_DURABILITY, PersistentDataType.INTEGER)) return "";
			return getMaxDurability() + "";
		});

		this.addVariable("durabilitypercentage", data -> {
			if (!data.has(HoloItemsAPI.getKeys().CUSTOM_ITEM_DURABILITY, PersistentDataType.INTEGER)) return "";

			int durability = data.get(HoloItemsAPI.getKeys().CUSTOM_ITEM_DURABILITY, PersistentDataType.INTEGER);

			DecimalFormat dc = new DecimalFormat();
			dc.setMaximumFractionDigits(2);
			dc.setMaximumIntegerDigits(3);
			dc.setMinimumFractionDigits(0);
			dc.setMinimumIntegerDigits(1);

			return dc.format(((double)durability / (double)getMaxDurability()) * 100);
		});

		this.addVariable("durabilitycolor", data -> {
			if (!data.has(HoloItemsAPI.getKeys().CUSTOM_ITEM_DURABILITY, PersistentDataType.INTEGER)) return "";
			int durability = data.get(HoloItemsAPI.getKeys().CUSTOM_ITEM_DURABILITY, PersistentDataType.INTEGER);
			return "" + getDurabilityColor(durability);
		});
	}

	@Override
	public ItemStack buildStack(Player player) {
		ItemStack stack = super.buildStack(player);

		ItemMeta meta = stack.getItemMeta();
		PersistentDataContainer data = meta.getPersistentDataContainer();

		List<String> newLore = new ArrayList<String>();

		//TODO: Get attributes and turn them into a lore
		//jk, we are no longer doing that

		if (isPlayedLocked()) {
			String s = replaceVariables(ConfigManager.languageConfig.get().getString("Lore.SelfOwned"), data);
			UUID owner = getOwner(stack);
			if (owner != null && !owner.equals(player.getUniqueId())) {
				s = replaceVariables(ConfigManager.languageConfig.get().getString("Lore.PlayerOwned"), data);
			}

			if (!s.equals("")) {
				newLore.add("");
				newLore.add(ChatColor.BLUE + ChatColor.translateAlternateColorCodes('&', s));
			}

		}

		for (Usage u : Usage.getRealValues()) {
			String s = ConfigManager.languageConfig.get().getString("Lore.Usage." + u.toString());
			if (getUsage() == u && s != null && !s.equals("")) {
				newLore.add("");
				newLore.add(ChatColor.RED + ChatColor.translateAlternateColorCodes('&', s));
				break;
			}
		}

		//Remove blank line if nothing after it
		if (newLore.size() > 1 && newLore.get(newLore.size() - 1).equals("")) newLore.remove(newLore.size() - 1);

		List<String> newNewLore = getLore().stream().map(s -> replaceVariables(s, data)).collect(Collectors.toList());
		newNewLore.addAll(newLore);

		meta.setLore(newNewLore);
		stack.setItemMeta(meta);

		return stack;
	}

	public ChatColor getDurabilityColor(int durability) {
		double percentage = (double)durability / (double)getMaxDurability();

		if (percentage >= 0.90D) { return ChatColor.DARK_GREEN; }
		else if (percentage >= 0.60D) { return ChatColor.GREEN; }
		else if (percentage >= 0.40D) { return ChatColor.YELLOW; }
		else if (percentage > 0.25D) { return ChatColor.GOLD; }
		else if (percentage > 0.05D) { return ChatColor.RED; }
		else return ChatColor.DARK_RED;
	}
	
	/***
	 * Updates the ItemStack provided with all the new stats/attributes
	 * loaded from the config. E.g. if the item type is changed from 
	 * REDSTONE to GLOWSTONE, this updates it without throwing away
	 * the old item.
	 * 
	 * @param stack The stack to update
	 * @return The updated ItemStack
	 */
	@Override
	public ItemStack updateStack(ItemStack stack, Player player) {
		stack = super.updateStack(stack, player);

		ItemMeta meta = stack.getItemMeta();
		PersistentDataContainer data = meta.getPersistentDataContainer();

		List<String> newLore = new ArrayList<String>();
		
		//TODO: Get attributes and turn them into a lore
		//jk, we are no longer doing that
		
		if (isPlayedLocked()) {
			String s = replaceVariables(ConfigManager.languageConfig.get().getString("Lore.SelfOwned"), data);
			UUID owner = getOwner(stack);
			if (owner != null && !owner.equals(player.getUniqueId())) {
				s = replaceVariables(ConfigManager.languageConfig.get().getString("Lore.PlayerOwned"), data);
			}

			if (!s.equals("")) {
				newLore.add("");
				newLore.add(ChatColor.BLUE + ChatColor.translateAlternateColorCodes('&', s));
			}

		}
		
		for (Usage u : Usage.getRealValues()) {
			String s = ConfigManager.languageConfig.get().getString("Lore.Usage." + u.toString());
			if (getUsage() == u && s != null && !s.equals("")) {
				newLore.add("");
				newLore.add(ChatColor.RED + ChatColor.translateAlternateColorCodes('&', s));
				break;
			}
		}

		
		
		
		//Remove blank line if nothing after it
		if (newLore.size() > 1 && newLore.get(newLore.size() - 1).equals("")) newLore.remove(newLore.size() - 1);

		List<String> newNewLore = getLore().stream().map(s -> replaceVariables(s, data)).collect(Collectors.toList());
		newNewLore.addAll(newLore);
		
		meta.setLore(newNewLore);
		stack.setItemMeta(meta);

		return stack;
	}
	
	/**
	 * <b>USE {@link PKItemStack.canUse} instead!</b>
	 */
	/*@Deprecated
	public boolean canUse(Player player) {
		if (!this.req.meets(player)) {
			return false;
		}
		
		return true;
	}*/
	
	/**
	 * Gets the PKItem from the itemstack provided. Will return <code>null</code>
	 * if the itemstack provided is not a PKItem.
	 * 
	 * @param itemstack The itemstack that the PKItem is gotten from
	 * @return The PKItem found or <code>null</code> if not
	 * */
	public static PKItem getPKItem(ItemStack itemstack) {
		if (itemstack == null || !itemstack.hasItemMeta()) return null;
		return (PKItem) CustomItemRegistry.getCustomItem(itemstack);
	}

	/**
	 * Gets the PKItem from the string provided. This must be
	 * the name of the item provided in the config (not the
	 * display name)
	 *
	 * @param name The name of the item
	 * @return The PKItem or <code>null</code> if it's not a
	 * valid PKItem
	 * */
	public static PKItem getItemFromName(String name) {
		return (PKItem) CustomItemRegistry.getCustomItem(name);
	}

	public UUID getOwner(ItemStack stack) {
		return stack.getItemMeta().getPersistentDataContainer().get(HoloItemsAPI.getKeys().CUSTOM_ITEM_OWNER, UUIDTagType.TYPE);
	}
	
	/**
	 * Gets whether the provided player can use this item 
	 * or not. It checks the item requirements and the owner
	 * and possibly more in future.
	 * 
	 * @param player The player being tested
	 * @param stack The ItemStack being tested
	 * @return If the player can use the item
	 */
	public boolean canUse(Player player, ItemStack stack) {
		if (!req.meets(player)) {
			return false;
		}
		
		if (isPlayedLocked() && (getOwner(stack) == null || !getOwner(stack).equals(player.getUniqueId()))) {
			return false;
		}
		
		return true;
	}
	
	public void addPKIAttribute(Attribute attribute, AttributeModification value) {
		attributes.put(attribute, value);
	}
	
	public Map<Attribute, AttributeModification> getPKIAttributes() {
		return attributes;
	}
	
	public void setRequirements(Requirements req) {
		this.req = req;
	}
	
	public PKItem setUsage(Usage usage) {
		this.usage = usage;
		return this;
	}
	
	public PKItem setPlayerLocked(boolean bool) {
		if (bool) {
			this.addProperty(Properties.OWNER);
		} else {
			this.getProperties().remove(Properties.OWNER);
		}
		return this;
	}
	
	public Usage getUsage() {
		return usage;
	}

	public Requirements getRequirements() {
		return req;
	}
	
	public boolean isPlayedLocked() {
		return this.getProperties().contains(Properties.OWNER);
	}
	
	public enum Usage {
		CONSUMABLE, WEARABLE, HOLD, INVENTORY, NONE;

		private static Usage[] REAL = {CONSUMABLE, WEARABLE, HOLD, INVENTORY};
		
		
		/***
		 * Parses the provided string and returns what usage type 
		 * it is
		 * 
		 * @param string The string that should be parsed
		 * @return The Usage type or <code>null</code> if not found
		 */
		public static Usage getUsage(String string) {
			string = string.replaceAll(" ", "_"); 
			if (string.equalsIgnoreCase("consumable") || string.equalsIgnoreCase("consume") || string.equalsIgnoreCase("potion")) {
				return CONSUMABLE;
			} else if (string.equalsIgnoreCase("wear") || string.equalsIgnoreCase("wearable")) {
				return WEARABLE;
			} else if (string.equalsIgnoreCase("hold") || string.equalsIgnoreCase("hand")) {
				return HOLD;
			} else if (string.equalsIgnoreCase("present") || string.equalsIgnoreCase("contain") || 
					string.equalsIgnoreCase("ininventory") || string.equalsIgnoreCase("in_inventory") ||
					string.equalsIgnoreCase("inventory") || string.equalsIgnoreCase("hold_in_inventory")) {
				return INVENTORY;
			} else return NONE;
		}

		public String toString() {
			return switch (this) {
				case CONSUMABLE -> "Consumable";
				case WEARABLE -> "Wearable";
				case HOLD -> "Hold";
				case INVENTORY -> "Inventory";
				case NONE -> "None";
			};
		}

		public static Usage[] getRealValues() {
			return REAL;
		}

		public ActiveConditions toConditions() {
			return switch (this) {
				case HOLD -> ActiveConditions.HELD;
				case CONSUMABLE, INVENTORY -> ActiveConditions.INVENTORY;
				case WEARABLE -> ActiveConditions.EQUIPED;
				case NONE -> ActiveConditions.NONE;
			};
		}
	}

	@Override
	public CustomItem register() {
		return super.register();
	}

	/**
	 * Returns whether or the item can be used
	 *
	 * @return
	 */
	public static boolean isPKItem(ItemStack stack) {
		return getPKItem(stack) != null;
	}
}
