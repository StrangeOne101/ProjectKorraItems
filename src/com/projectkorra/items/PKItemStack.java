package com.projectkorra.items;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.projectkorra.items.configuration.ConfigManager;
import com.projectkorra.items.utils.NBTReflectionUtil;

import net.md_5.bungee.api.ChatColor;

public class PKItemStack extends ItemStack {

	protected short pki_durability = 1;
	protected UUID owner;
	
	protected byte PKI_ID;
	
	PKItemStack(ItemStack stack, byte ID) {
		super(PKItem.INSTANCE_MAP.get(ID).updateStack(stack));
		this.PKI_ID = ID;
		this.pki_durability = getItem().getDurability();
	}
	
	/**Returns the PKItem associated with this itemstack*/
	public PKItem getItem() {
		return PKItem.INSTANCE_MAP.get(PKI_ID);
	}
	
	/**
	 * Gets whether the provided player can use this item 
	 * or not. It checks the item requirements and the owner
	 * and possibly more in future.
	 * 
	 * @param player The player being tested
	 * @return If the player can use the item
	 */
	public boolean canUse(Player player) {
		if (!getItem().req.canUse(player)) {
			return false;
		}
		
		if (getItem().isPlayedLocked() && !getOwner().equals(player.getUniqueId())) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Damages the item and breaks it if necessary by the amount provided.
	 * 
	 * @param player The player damaging the item
	 * @param amount The amount to damage it by
	 * */
	public void damageItem(Player player, int amount) {
		if (pki_durability > -1) { //Items with no durability (-1) don't get checked.
			setPKIDurability((short) (pki_durability - amount)); //Setting it the long way so we update the NBT too.
			
			if (pki_durability <= 0) {
				this.setType(Material.AIR); //Setting it to air will remove the itemstack from the inventory. Saves us having to worry about that stuff.
				if (!this.getItem().ignoreBreakMessage) {
					player.sendMessage(ChatColor.RED + ConfigManager.languageConfig.get().getString("Items.Use.Break").replace("{item}", getItem().getDisplayName()));
				}
			} else {
				this.getItem().updateStack(this); //Updates the durability on the lore.
			}
		}
	}
	
	/**
	 * Damages the item and breaks it if necessary.
	 * 
	 * @param player The player damaging the item
	 */
	public void damageItem(Player player) {
		this.damageItem(player, 1);
	}
	
	/**
	 * Loads an old itemstack and turns it into a PKItemStack. This is
	 * used when a player logs in and their items are loaded from their
	 * save and their PK Items aren't PKItemStacks (just normal itemstacks)
	 * 
	 * @param itemstack The ItemStack being loaded
	 * @return The PKItemStack 
	 */
	public static PKItemStack loadFromItemStack(ItemStack itemstack) {
		if (!PKItem.isPKItem(itemstack)) return null;
		
		PKItem item = PKItem.getPKItem(itemstack);
		if (item == null) return null;
		byte id = item.getID();	
		
		PKItemStack pkitemstack = new PKItemStack(itemstack, id);		
		
		if (NBTReflectionUtil.hasKey(itemstack, "PKI_Owner")) {
			pkitemstack.setOwner(UUID.fromString(NBTReflectionUtil.getString(itemstack, "PKI_Owner")));
		}
		if (NBTReflectionUtil.hasKey(itemstack, "PKI_Durability")) {
			pkitemstack.setPKIDurability(Short.parseShort("" + NBTReflectionUtil.getInt(itemstack, "PKI_Durability")));
		}
		
		pkitemstack = (PKItemStack) PKItem.INSTANCE_MAP.get(id).updateStack(pkitemstack);
		
		return pkitemstack;
	}
	
	
	/**
	 * Sets the owner and updates the NBT.
	 * 
	 * @param owner The UUID of the owner
	 */
	public void setOwner(UUID owner) {
		NBTReflectionUtil.setString(this, "PKI_Owner", owner.toString());
		this.owner = owner;
	}
	
	public UUID getOwner() {
		return owner;
	}
	
	/**
	 * Sets the durability and updates the NBT.
	 * 
	 * @param durability The durability of the item
	 */
	public void setPKIDurability(short durability) {
		this.pki_durability = durability;
		NBTReflectionUtil.setInt(this, "PKI_Durability", (int) durability);
	}
	
	public short getPKIDurability() {
		return pki_durability;
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
	public static ItemStack getDudItem(byte previousID, boolean showID) {
		ItemStack stack = new ItemStack(Material.STICK);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "" + ChatColor.MAGIC + "l|l" + ChatColor.RESET + ChatColor.GRAY + "Unknown Item" + ChatColor.WHITE + ChatColor.MAGIC + "l|l" + PKItem.hideID(previousID));
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.DARK_GRAY + "Your ProjectKorra Item is unknown! Contact your");
		lore.add(ChatColor.DARK_GRAY + "admin about this! Keep this item somewhere safe");
		lore.add(ChatColor.DARK_GRAY + "in case it is restored in future.");
		if (showID) lore.add(ChatColor.RED + "Secret ID: " + (((int)previousID) + 128));
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}
}
