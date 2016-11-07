package com.projectkorra.items;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.utils.NBTReflectionUtil;

public class PKItemStack extends ItemStack {

	protected short pki_durability = 1;
	protected UUID owner;
	
	protected byte PKI_ID;
	
	private PKItemStack(ItemStack stack, byte ID) {
		super(PKItem.INSTANCE_MAP.get(ID).updateStack(stack));
		this.PKI_ID = ID;
	}
	
	/**Returns the PKItem associated with this itemstack*/
	public PKItem getItem() {
		return PKItem.INSTANCE_MAP.get(PKI_ID);
	}
	
	public static PKItemStack loadFromItemStack(ItemStack itemstack) {
		if (!PKItem.isPKItem(itemstack)) return null;
		
		byte id = PKItem.findID(itemstack.getItemMeta().getDisplayName());
		if (!PKItem.INSTANCE_MAP.containsKey(id)) return null;
		
		
		
		PKItemStack pkitemstack = new PKItemStack(itemstack, id);
		
		
		if (NBTReflectionUtil.hasKey(pkitemstack, "PKI_Owner")) {
			pkitemstack.setOwner(UUID.fromString(NBTReflectionUtil.getString(itemstack, "PKI_Owner")));
		}
		if (NBTReflectionUtil.hasKey(pkitemstack, "PKI_Durability")) {
			pkitemstack.setPKIDurability(Short.parseShort("" + NBTReflectionUtil.getInt(itemstack, "PKI_Durability")));
		}
		
		//TODO: Load durability
		//TODO: Load owner
		
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
}
