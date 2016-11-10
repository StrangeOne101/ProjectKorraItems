package com.projectkorra.items;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	/**Damages the item and breaks it if nessasary*/
	public void damageItem(Player player) {
		if (pki_durability > -1) {
			pki_durability--;
			
			if (pki_durability == 0) {
				this.setType(Material.AIR);
				if (!this.getItem().ignoreBreakMessage) {
					player.sendMessage(ChatColor.RED + ConfigManager.languageConfig.get().getString("Items.Use.Break").replace("{item}", getItem().getDisplayName()));
				}
			} else {
				this.getItem().updateStack(this);
			}
		}
	}
	
	public static PKItemStack loadFromItemStack(ItemStack itemstack) {
		if (!PKItem.isPKItem(itemstack)) return null;
		
		byte id = PKItem.findID(itemstack.getItemMeta().getDisplayName());
		if (!PKItem.INSTANCE_MAP.containsKey(id)) return null;
		
		
		
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
	
	public void setOwner(UUID owner) {
		this.owner = owner;
	}
	
	public UUID getOwner() {
		return owner;
	}
	
	public void setPKIDurability(short durability) {
		this.pki_durability = durability;
		NBTReflectionUtil.setInt(this, "PKI_Durability", (int) durability);
	}
	
	public short getPKIDurability() {
		return pki_durability;
	}
	
	public static ItemStack getDudItem(byte previousID, boolean showID) {
		ItemStack stack = new ItemStack(Material.STICK);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.MAGIC + "I|I" + ChatColor.RESET + ChatColor.GRAY + "Unknown Item" + ChatColor.MAGIC + ChatColor.WHITE + "I|I" + PKItem.hideID(previousID));
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.DARK_GRAY + "Your ProjectKorra Item is unknown! Contact your");
		lore.add(ChatColor.DARK_GRAY + "admin about this! Keep this item somewhere safe");
		lore.add(ChatColor.DARK_GRAY + "in case it is restored in future.");
		if (showID) lore.add(ChatColor.RED + "Secret ID: " + ((int)previousID) + 128);
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}
}
