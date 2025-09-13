package com.projectkorra.items;

import com.projectkorra.items.api.CustomItem;
import com.projectkorra.items.api.CustomItemRegistry;
import com.projectkorra.items.api.HoloItemsAPI;
import com.projectkorra.items.api.Properties;
import com.projectkorra.items.api.itemevent.ActiveConditions;
import com.projectkorra.items.api.util.UUIDTagType;
import com.projectkorra.items.attribute.AttributeEvent;
import com.projectkorra.items.attribute.AttributeModification;
import com.projectkorra.items.attribute.Requirements;
import com.projectkorra.items.configuration.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PKItem extends CustomItem implements ItemProperties {

	protected Usage usage;
	protected Requirements requirements;
	protected Map<AttributeEvent, Set<AttributeModification>> attributes = new HashMap<>();
	protected String file = "(Unknown)";

	
	public PKItem(String name, Material material) {
		super(name, material);

		this.usage = Usage.NONE;
		this.requirements = new Requirements(); //Blank requirements

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
		if (!requirements.meets(player)) {
			return false;
		}
		
		if (isPlayedLocked() && (getOwner(stack) == null || !getOwner(stack).equals(player.getUniqueId()))) {
			return false;
		}
		
		return true;
	}
	
	public void addPKIAttribute(AttributeModification attributeMod) {
		if (!attributes.containsKey(attributeMod.getAttributeTrait().getEvent())) {
			attributes.put(attributeMod.getAttributeTrait().getEvent(), new HashSet<>());
		}
		attributes.get(attributeMod.getAttributeTrait().getEvent()).add(attributeMod);
	}
	
	public Map<AttributeEvent, Set<AttributeModification>> getPKIAttributes() {
		return attributes;
	}
	
	public void setRequirements(Requirements req) {
		this.requirements = req;
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
		return requirements;
	}
	
	public boolean isPlayedLocked() {
		return this.getProperties().contains(Properties.OWNER);
	}

	public String getFileLocation() {
		return file;
	}

	public PKItem setFileLocation(String file) {
		this.file = file;
		return this;
	}

	public enum Usage {
		CONSUMABLE, WEARABLE, HOLD, INVENTORY, NONE;

		private static Usage[] VALID = {CONSUMABLE, WEARABLE, HOLD, INVENTORY};
		
		
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
			} else if (string.equalsIgnoreCase("wear") || string.equalsIgnoreCase("wearable") || string.equalsIgnoreCase("equipable") ||
					string.equalsIgnoreCase("equip")) {
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
			switch (this) {
				case CONSUMABLE: return "Consumable";
				case WEARABLE: return "Wearable";
				case HOLD: return "Hold";
				case INVENTORY: return "Inventory";
				case NONE:
				default: return "None";
			}
		}

		public static Usage[] getRealValues() {
			return VALID;
		}

		public ActiveConditions toConditions() {
			switch (this) {
				case HOLD: return ActiveConditions.HELD;
				case CONSUMABLE:
				case INVENTORY: return ActiveConditions.INVENTORY;
				case WEARABLE: return ActiveConditions.EQUIPED;
				case NONE:
				default: return ActiveConditions.NONE;
			}
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
