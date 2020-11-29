package com.projectkorra.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.projectkorra.items.attribute.Attribute;
import com.projectkorra.items.attribute.AttributeModification;
import com.projectkorra.items.attribute.Requirements;
import com.projectkorra.items.configuration.ConfigManager;
import com.projectkorra.items.utils.GenericUtil;
import com.projectkorra.items.utils.NBTReflectionUtil;

public class PKItem {

	public static Map<Short, PKItem> INSTANCE_MAP = new HashMap<Short, PKItem>();
	public static List<String> DISABLED = new ArrayList<String>();
	
	protected String name;
	protected String displayName;
	protected List<String> lore;
	protected MaterialData material;
	protected short durability;
	protected short ID;
	protected Usage usage;
	protected boolean glow = false;
	protected Requirements req = new Requirements();
	protected boolean playedLocked = false;
	protected boolean ignoreBreakMessage = false;
	protected Map<Attribute, AttributeModification> attributes = new HashMap<Attribute, AttributeModification>();
	
	public PKItem(String name, short ID) {
		if (INSTANCE_MAP.containsKey(ID)) {
			String duplicateID = ConfigManager.languageConfig.get().getString("Item.Load.DuplicateID");
			duplicateID = duplicateID.replace("{item1}", INSTANCE_MAP.get(ID).name).replace("{item2}", name).replace("{id}", ID + "");
			ProjectKorraItems.createError(duplicateID);
			
			return;
		}
		
		this.name = name;
		this.ID = ID;
		this.displayName = ChatColor.YELLOW + name;
		this.lore = new ArrayList<String>();
		this.material = new MaterialData(Material.STICK);
		this.durability = -1;
		this.usage = Usage.HOLD;
		this.req = new Requirements(); //Blank requirements
		
		INSTANCE_MAP.put(ID, this);
		ConfigManager.itemsConfig.get().set(name + ".SecretID", GenericUtil.convertSignedShort(ID));
		ConfigManager.itemsConfig.save();
	}
	
	/**
	 * Creates a fresh itemstack of the PKItem.
	 * 
	 * @return A new ItemStack of the PKItem
	 */
	@SuppressWarnings("deprecation")
	public ItemStack buildItem() {
		ItemStack stack = updateStack(new ItemStack(this.material.getItemType(), 1, this.material.getData()));
		if (getMaxDurability() > -1) {
			stack = setDurability(stack, getMaxDurability());
		}
		return stack;
	}
	
	/**
	 * Creates a fresh itemstack of the PKItem.
	 * 
	 * @param amount The amount
	 * @return A new ItemStack of the PKItem
	 */
	@SuppressWarnings("deprecation")
	public ItemStack buildItem(int amount) {
		ItemStack stack = updateStack(new ItemStack(this.material.getItemType(), amount, this.material.getData()));
		if (getMaxDurability() > -1) {
			stack = setDurability(stack, getMaxDurability());
		}
		return stack;
	}
	
	/**
	 * Creates a fresh itemstack of the PKItem for a player.
	 * 
	 * @param player The player building the itme
	 * @return A new ItemStack of the PKItem
	 */
	public ItemStack buildItem(Player player) {
		ItemStack stack = buildItem();
		stack = setOwner(stack, player.getUniqueId());
		return stack;
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
	@SuppressWarnings("deprecation")
	public ItemStack updateStack(ItemStack stack) {
		if (stack.getType() != material.getItemType() || stack.getDurability() != material.getData()) {
			stack.setType(material.getItemType());
			stack.setDurability(material.getData());
		}
		
		ItemMeta meta = stack.getItemMeta();
		
		if (this.glow) {
			meta.addEnchant(Enchantment.LUCK, 1, false);
	        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.displayName) + hideID(this.ID));
		
		List<String> newLore = new ArrayList<String>();
		
		for (String l : lore) {
			newLore.add(ChatColor.translateAlternateColorCodes('&', l));
		}
		
		//TODO: Get attributes and turn them into a lore
		//jk, we are no longer doing that
		
		if (playedLocked) {
			if (NBTReflectionUtil.hasKey(stack, "PKI_Owner")) {
				String owner = Bukkit.getOfflinePlayer(getOwner(stack)).getName();
				
				newLore.add(ChatColor.BLUE + ConfigManager.languageConfig.get().getString("Item.Lore.Owner").replace("{owner}", owner));
			}
		}
		
		newLore.add("");
		
		if (this.durability > -1) {
			
			short currDurability = (short) this.durability;
			
			if (NBTReflectionUtil.hasKey(stack, "PKI_Durability")) {
				currDurability = getDurability(stack);
			}
			
			newLore.add(ChatColor.BLUE + ConfigManager.languageConfig.get().getString("Item.Lore.Durability")
					.replace("{durability}", currDurability + "").replace("{maxdurability}", durability + "")); //First durability 
		}
		if (usage == Usage.CONSUMABLE) {
			newLore.add(ChatColor.RED + ConfigManager.languageConfig.get().getString("Item.Lore.Usage.Consumable"));
		}
		else if (usage == Usage.WEARABLE) {
			newLore.add(ChatColor.RED + ConfigManager.languageConfig.get().getString("Item.Lore.Usage.Wearable"));
		}
		else if (usage == Usage.HOLD) {
			newLore.add(ChatColor.RED + ConfigManager.languageConfig.get().getString("Item.Lore.Usage.Hold"));
		}
		
		
		
		//Remove blank line if nothing after it
		if (newLore.size() > 1 && newLore.get(newLore.size() - 1).equals("")) newLore.remove(newLore.size() - 1); 
		
		meta.setLore(newLore);
		stack.setItemMeta(meta);
		
		setHashcode(stack, generateHashcode(stack)); //Updates the hashcode in the NBT so we know if it needs updating or not
		
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
	 * Creates an invisible string for the ID of the PKItem to be hidden in
	 * 
	 * @param id The ID of the PKItem
	 * @return The ID as an invisible string (to itemstacks)
	 * */
	public static String hideID(short id) {
		String hex = String.format("%02X ", GenericUtil.convertSignedShort(id)).trim();
		while (hex.length() < 4) hex = "0" + hex;
		return "\u00A7k\u00A7k\u00A7" + hex.charAt(0) + "\u00A7" + hex.charAt(1) + "\u00A7" + hex.charAt(2) + "\u00A7" + hex.charAt(3);
	}
	
	
	/**
	 * Gets the ID from a display name of any PK Item. 
	 * 
	 * @param name The displayname/string that holds the ID.
	 * @return The ID of the item.
	 * @throws NumberFormatException if there is no ID found.
	 * */
	public static short findID(String name) {
		String refined = name.split("\u00A7k\u00A7k")[name.split("\u00A7k\u00A7k").length - 1]; //Everything post last "§k"
		int b = Integer.valueOf("" + refined.charAt(1) + refined.charAt(3) + refined.charAt(5) + refined.charAt(7) , 16);
		return GenericUtil.convertUnsignedShort(b);
	}
	
	/**
	 * Gets the PKItem from the itemstack provided. Will return <code>null</code>
	 * if the itemstack provided is not a PKItem.
	 * 
	 * @param itemstack The itemstack that the PKItem is gotten from
	 * @return The PKItem found or <code>null</code> if not
	 * */
	public static PKItem getPKItem(ItemStack itemstack) {
		if (itemstack == null || !itemstack.hasItemMeta() || !isPKItem(itemstack)) return null;
		short b = findID(itemstack.getItemMeta().getDisplayName());
		
		if (INSTANCE_MAP.containsKey(b)) return INSTANCE_MAP.get(b);
		return null;
	}
	
	/**
	 * Checks an item to see if it contains a hidden ID/is a PKItem.
	 * 
	 * @param itemstack The ItemStack being checked
	 * @return If the item is a PKItem.
	 * */
	public static boolean isPKItem(ItemStack itemstack) {
		if (!itemstack.hasItemMeta()) return false;
		String name = itemstack.getItemMeta().getDisplayName();
		if (name == null || !name.matches(".*\u00A7k\u00A7k\u00A7[0-9a-fA-F]\u00A7[0-9a-fA-F]\u00A7[0-9a-fA-F]\u00A7[0-9a-fA-F]")) return false;

		try {
			return PKItem.INSTANCE_MAP.containsKey(findID(name));
		} catch (NumberFormatException | IndexOutOfBoundsException e) { return false; }
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
		for (PKItem item : INSTANCE_MAP.values()) {
			if (item.getName().equalsIgnoreCase(name)) {
				return item;
			}
		}
		return null;
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
		
		if (isPlayedLocked() && !getOwner(stack).equals(player.getUniqueId())) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Damages the item and breaks it if necessary by the amount provided.
	 * 
	 * @param player The player damaging the item
	 * @param stack The ItemStack being damaged
	 * @param amount The amount to damage it by
	 * */
	public ItemStack damageItem(Player player, ItemStack stack, int amount) {
		if (getDurability(stack) > -1) { //Items with no durability (-1) don't get checked.
			stack = setDurability(stack, (short) (getDurability(stack) - amount)); //Setting it the long way so we update the NBT too.
			
			if (getDurability(stack) <= 0) {
				stack.setType(Material.AIR); //Setting it to air will remove the itemstack from the inventory. Saves us having to worry about that stuff.

				if (!ignoreBreakMessage && player != null) {
					player.sendMessage(ChatColor.RED + ConfigManager.languageConfig.get().getString("Items.Use.Break").replace("{item}", getDisplayName()));
				}
				return stack;
			} else {
				return this.updateStack(stack); //Updates the durability on the lore.
			}
		}
		return stack;
	}
	
	/**
	 * Damages the item and breaks it if necessary.
	 * 
	 * @param player The player damaging the item
	 * @param stack The ItemStack being damaged
	 */
	public ItemStack damageItem(Player player, ItemStack stack) {
		return this.damageItem(player, stack, 1);
	}
	
	public void addAttribute(Attribute attribute, AttributeModification value) {
		attributes.put(attribute, value);
	}
	
	public Map<Attribute, AttributeModification> getAttributes() {
		return attributes;
	}
	
	public void setRequirements(Requirements req) {
		this.req = req;
	}
	
	public PKItem setDisplayName(String displayName) {
		if (displayName != null && displayName != "") this.displayName = displayName;
		return this;
	}
	
	public PKItem setMaxDurability(short durability) {
		this.durability = durability;
		return this;
	}
	
	public PKItem setLore(List<String> lore) {
		this.lore = lore;
		return this;
	}
	
	public PKItem setMaterial(MaterialData material) {
		this.material = material;
		return this;
	}
	
	public PKItem setMaterial(Material material) {
		this.material = new MaterialData(material);
		return this;
	}
	
	public PKItem setGlow(boolean glow) {
		this.glow = glow;
		return this;
	}
	
	public PKItem setUsage(Usage usage) {
		this.usage = usage;
		return this;
	}
	
	public PKItem setPlayerLocked(boolean bool) {
		this.playedLocked = bool;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public short getID() {
		return ID;
	}
	
	public Usage getUsage() {
		return usage;
	}
	
	public List<String> getLore() {
		return lore;
	}
	
	public MaterialData getMaterial() {
		return material;
	}

	public Requirements getRequirements() {
		return req;
	}
	
	public boolean isPlayedLocked() {
		return playedLocked;
	}
	
	public short getMaxDurability() {
		return durability;
	}
	
	public boolean ignoreBreakMessage() {
		return ignoreBreakMessage;
	}
	
	public PKItem setIgnoreBreakMessage(boolean ignoreBreakMessage) {
		this.ignoreBreakMessage = ignoreBreakMessage;
		return this;
	}
	
	public enum Usage {
		CONSUMABLE, WEARABLE, HOLD, PRESENT;
		
		
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
				return PRESENT;
			} else return null;
		}
	}
	
	/***
	 * Gets the durability of the item
	 * 
	 * @param stack The PKItem
	 * @return The durability
	 */
	public static short getDurability(ItemStack stack) {
		return Short.valueOf("" + NBTReflectionUtil.getInt(stack, "PKI_Durability"));
	}
	
	/***
	 * Gets the owner of the item
	 * 
	 * @param stack The PKItem
	 * @return The owner in UUID form
	 */
	public static UUID getOwner(ItemStack stack) {
		return UUID.fromString(NBTReflectionUtil.getString(stack, "PKI_Owner"));
	}
	
	/***
	 * Gets the hashcode of the item from the NBT
	 * 
	 * @param stack The PKItem
	 * @return The hashcode
	 */
	public static int getHashcode(ItemStack stack) {
		return NBTReflectionUtil.getInt(stack, "PKI_Hashcode");
	}
	
	/**
	 * Sets the owner and updates the NBT.
	 * 
	 * @param stack The ItemStack we are changing
	 * @param owner The UUID of the owner
	 */
	public static ItemStack setOwner(ItemStack stack, UUID owner) {
		return NBTReflectionUtil.setString(stack, "PKI_Owner", owner.toString());
	}
	
	/**
	 * Sets the durability and updates the NBT.
	 * 
	 * @param stack The ItemStack we are changing
	 * @param durability The durability of the item
	 */
	public static ItemStack setDurability(ItemStack stack, short durability) {
		return NBTReflectionUtil.setInt(stack, "PKI_Durability", (int) durability);
	}
	
	/**
	 * Sets the hashcode and updates the NBT.
	 * 
	 * @param stack The ItemStack we are changing
	 * @param hashcode The hashcode of the item
	 */
	public static ItemStack setHashcode(ItemStack stack, int hashcode) {
		return NBTReflectionUtil.setInt(stack, "PKI_Hashcode", hashcode);
	}
	
	/**
	 * Gets a fresh dud item. This item is for users when their PKItems that 
	 * have been loaded are found to be invalid. These items can't be used but
	 * still hold all the previous information in case the admin fixes the item
	 * in the future (As soon as they do the dud item will revert back into the
	 * correct one)
	 * 
	 * @param previousID The ID of the PKItem that was found to be invalid. 
	 * Should not currently be linked to any item.
	 * @param showID Whether or not to show the ID on the item lore. Is used
	 * for admins so they know what item it was and can fix it.
	 * @return The dud PK itemstack.
	 */
	public static ItemStack getDudItem(short previousID, boolean showID) {
		ItemStack stack = new ItemStack(Material.STICK);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "" + ChatColor.MAGIC + "l|l" + ChatColor.RESET + ChatColor.GRAY + " Unknown Item " + ChatColor.WHITE + ChatColor.MAGIC + "l|l" + PKItem.hideID(previousID));
		List<String> lore = new ArrayList<String>();
		lore.addAll(GenericUtil.splitString(ChatColor.translateAlternateColorCodes('&', ConfigManager.languageConfig.get().getString("Item.Broken.Lore")), 50));
		if (showID) lore.add(ChatColor.RED + "Secret ID: " + GenericUtil.convertSignedShort(previousID));
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}
	
	/**
	 * Returns whether or not the ID is valid or not
	 * 
	 * @param id The ID
	 * @return
	 */
	public static boolean isValidID(short id) {
		return INSTANCE_MAP.containsKey(id);
	}
	
	/**
	 * Returns whether or the item can be used
	 *
	 * @return
	 */
	public static boolean isValidItem(ItemStack stack) {
		return isPKItem(stack) && isValidID(findID(stack.getItemMeta().getDisplayName()));
	}
	
	/**
	 * Generate a hashcode based on the PKItem provided (in itemstack form)
	 * 
	 * @param stack The PKItem itemstack
	 * @return The hashcode
	 */
	public static int generateHashcode(ItemStack stack) {
		ItemMeta meta = stack.getItemMeta();
		String hashString = stack.getType() + ":" + stack.getDurability(); //The type and durability
		hashString = hashString + (meta.hasDisplayName() ? meta.getDisplayName() : ""); //The name
		hashString = hashString + String.join("\n", meta.getLore()); //The lore
		return hashString.hashCode();
	}
	
	/**
	 * Checks the itemstack's hashcode against the one stored. If it is 
	 * different then the ItemStack needs to be updated
	 * 
	 * @param stack The itemstack to check
	 * @return True if it needs updating
	 */
	public static boolean needsUpdating(ItemStack stack) {
		return getHashcode(stack) != generateHashcode(stack);
	}
	
}
