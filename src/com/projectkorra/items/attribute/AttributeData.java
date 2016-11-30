package com.projectkorra.items.attribute;

import com.projectkorra.items.PKItemStack;
import com.projectkorra.projectkorra.ability.CoreAbility;

public class AttributeData {

	private Attribute attribute;
	private PKItemStack stack;
	private String value;

	public AttributeData(Attribute attribute, PKItemStack stack, String value) {
		
		this.attribute = attribute;
		this.stack = stack;
		this.value = value;
	}
	
	public Attribute getAttribute() {
		return attribute;
	}
	
	public PKItemStack getStack() {
		return stack;
	}
	
	public String getValue() {
		return value;
	}
	
	public boolean modifyAbility(CoreAbility ability) {
		return this.attribute.handle(ability, value);
	}

}
