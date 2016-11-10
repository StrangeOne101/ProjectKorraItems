package com.projectkorra.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.projectkorra.items.attribute.Requirements;
import com.projectkorra.items.configuration.ConfigManager;
import com.projectkorra.items.customs.EnchantGlow;

public class PKItem {

	public static Map<Byte, PKItem> INSTANCE_MAP = new HashMap<Byte, PKItem>();
	public static List<String> DISABLED = new ArrayList<String>();
	
	protected String name;
	protected String displayName;
	protected List<String> lore;
	protected MaterialData material;
	protected short durability;
	protected byte ID;
	protected Usage usage;
	protected boolean glow = false;
	protected Requirements req;
	protected boolean playedLocked = false;
	protected boolean ignoreBreakMessage = false;
	
	public PKItem(String name, byte ID) {
		if (INSTANCE_MAP.containsKey(ID)) {
			String duplicateID = ConfigManager.languageConfig.get().getString("Item.Load.DuplicateID");
			duplicateID = duplicateID.replace("{item1}", INSTANCE_MAP.get(ID).name).replace("{item2}", name);
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
		ConfigManager.itemsConfig.get().set(name + ".SecretID", ((int)ID) + 128);
		ConfigManager.itemsConfig.save();
	}
	
	public ItemStack buildItem() {
		@SuppressWarnings("deprecation")
		//ItemStack stack = PKItemStack.loadFromItemStack(new ItemStack(this.material.getItemType(), this.material.getData()));
		//((PKItemStack)stack).setPKIDurability((short) durability);
		ItemStack stack = new PKItemStack(new ItemStack(this.material.getItemType(), 1, this.material.getData()), ID);
		stack = updateStack(stack);
		return updateStack(stack);
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack updateStack(ItemStack stack) {
		if (stack.getType() != material.getItemType() || stack.getDurability() != material.getData()) {
			stack.setData(material);
		}
		
		ItemMeta meta = stack.getItemMeta();
		
		if (this.glow) {
			meta.addEnchant(EnchantGlow.getGlow(), 0, false);
		}
		
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.displayName) + hideID(this.ID));
		
		List<String> newLore = new ArrayList<String>();
		
		for (String l : lore) {
			newLore.add(ChatColor.translateAlternateColorCodes('&', l));
		}
		
		//TODO: Get attributes and turn them into a lore
		
		if (playedLocked && stack instanceof PKItemStack) {
			String owner = Bukkit.getOfflinePlayer(((PKItemStack)stack).owner).getName();
			newLore.add(ChatColor.BLUE + ConfigManager.languageConfig.get().getString("Item.Lore.Owner").replace("{owner}", owner));
		}
		
		newLore.add("");
		
		if (this.durability > -1) {
			
			short currDurability = (short) this.durability;
			
			if (stack instanceof PKItemStack) {
				currDurability = ((PKItemStack)stack).getPKIDurability();
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
		
		return stack;
	}
	
	
	
	public boolean canUse(Player player) {
		if (!this.req.canUse(player)) {
			return false;
		}
		
		return true;
	}
	
	
	
	/**Creates an invisible string for the ID of the PKItem to be hidden in*/
	public static String hideID(byte id) {
		String hex = String.format("%02X ", id);
		return "§k§" + hex.charAt(0) + "§" + hex.charAt(1);
	}
	
	/**Gets the ID from a display name of any PK Item.*/
	public static byte findID(String name) {
		String refined = name.split("§k")[name.split("§k").length - 1];
		return Byte.valueOf("" + refined.charAt(1) + refined.charAt(3), 16);
	}
	
	/**Returns whether the itemstack is a PKItem or not*/
	public static boolean isPKItem(ItemStack itemstack) {
		if (!itemstack.hasItemMeta()) return false;
		String name = itemstack.getItemMeta().getDisplayName();
		if (name.split("§k").length == 1) return false;
		
		String refined = name.split("§k")[name.split("§k").length - 1];
		if (refined.charAt(0) != '§' && refined.charAt(2) != '§') return false;
		try {
			Byte.valueOf("" + refined.charAt(1) + refined.charAt(3), 16);
			return true;
		} catch (NumberFormatException e) {return false;}
	}
	
	public static PKItem getItemFromName(String name) {
		for (PKItem item : INSTANCE_MAP.values()) {
			if (item.getName().equalsIgnoreCase(name)) {
				return item;
			}
		}
		return null;
	}
	
	public void setRequirements(Requirements req) {
		this.req = req;
	}
	
	public PKItem setDisplayName(String displayName) {
		if (displayName != null && displayName != "") this.displayName = displayName;
		return this;
	}
	
	public PKItem setDurability(short durability) {
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
	
	public byte getID() {
		return ID;
	}
	
	public List<String> getLore() {
		return lore;
	}
	
	public MaterialData getMaterial() {
		return material;
	}
	
	public boolean isPlayedLocked() {
		return playedLocked;
	}
	
	public short getDurability() {
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
		
		public static Usage getUsage(String string) {
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
}
