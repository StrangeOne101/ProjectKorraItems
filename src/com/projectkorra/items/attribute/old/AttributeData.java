package com.projectkorra.items.attribute.old;

import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.PKItem;
import com.projectkorra.projectkorra.ability.CoreAbility;

public class AttributeData {

	private AttributeOld attribute;
	private ItemStack stack;
	private PKItem item;
	private String value;

	public AttributeData(AttributeOld attribute, PKItem item, ItemStack stack, String value) {
		
		this.attribute = attribute;
		this.stack = stack;
		this.value = value;
		this.item = item;
	}
	
	public AttributeOld getAttribute() {
		return attribute;
	}
	
	public ItemStack getStack() {
		return stack;
	}
	
	public String getValue() {
		return value;
	}
	
	public PKItem getItem() {
		return item;
	}
	
	public boolean modifyAbility(CoreAbility ability) {
		return this.attribute.handle(ability, value);
	}

}
