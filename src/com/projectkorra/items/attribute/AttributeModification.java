package com.projectkorra.items.attribute;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.AttributeModifier;

public class AttributeModification {
	
	private double value;
	private AttributeModifier modifier;
	private Attribute attribute;
	private PKItem source;
	
	public AttributeModification(Attribute attribute, String modification, PKItem source) {
		this.attribute = attribute;
		this.source = source;
		
		modification = modification.replaceAll("[ A-Za-z]", "");
		if (isValidModification(modification)) {
			AttributeModifier modifier = AttributeModifier.ADDITION;
			double value;
			String parsingValue = modification;
			
			if (modification.startsWith("+")) {
				parsingValue = parsingValue.substring(1); //Get rid of the plus
			} else if (modification.startsWith("x") || modification.startsWith("*")) {
				parsingValue = parsingValue.substring(1);
				modifier = AttributeModifier.MULTIPLICATION;
			} else if (modification.startsWith("/")) {
				parsingValue = parsingValue.substring(1);
				modifier = AttributeModifier.DIVISION;
			} else if (modification.startsWith("-")) {
				parsingValue = parsingValue.substring(1);
				modifier = AttributeModifier.SUBTRACTION;
			}
			
			if (modification.endsWith("%")) {
				parsingValue = parsingValue.substring(0, parsingValue.length() - 2);
				modifier = AttributeModifier.MULTIPLICATION;
			}
			
			try {
				value = Double.parseDouble(parsingValue);
				
				if (modification.endsWith("%")) {
					value /= 100;
					
					if (modification.startsWith("+")) {
						value += 1;
					} else if (modification.startsWith("-")) {
						value = 1 - value;
					}
				}
				
				this.value = value;
				this.modifier = modifier;
				
			} catch (NumberFormatException e) {
				ProjectKorraItems.createError("Cannot parse modification \"" + modification + "\" for Attribute \"" + attribute.getAttributeName());
				this.attribute = null; 
			}
		}
	}
	
	/**
	 * Tries to apply the attribute modification to the provided ability
	 * @param ability The ability
	 * @return True if it applied
	 */
	public boolean performModification(CoreAbility ability) {
		if (attribute == null) return false;
		if (!attribute.getPrefix().affects(ability)) return false;
		
		return ability.hashCode() != ability.addAttributeModifier(attribute.getAttributeName(), value, modifier).hashCode();
	}
	
	public static boolean isValidModification(String modification) {
		modification = modification.replaceAll(" ", "");
		if (modification.endsWith("%")) {
			String percent = modification.substring(0, modification.length() - 2); //Cut off the %
			if (percent.startsWith("+")) percent = percent.substring(1);
			try {
				Double.parseDouble(percent);
				return true;
			} catch (NumberFormatException e) { return false; }
		} else if (modification.startsWith("x") || modification.startsWith("*")) {
			try {
				Double.parseDouble(modification.substring(1));
				return true;
			} catch (NumberFormatException e) { return false; }
		} else {
			try {
				if (modification.startsWith("+"))
				Double.parseDouble(modification);
				return true;
			} catch (NumberFormatException e) { return false; }
		}
	}
	
	public Attribute getAttribute() {
		return attribute;
	}
	
	public PKItem getItemSource() {
		return source;
	}
	
	public AttributeModifier getModifier() {
		return modifier;
	}
	
	public double getValue() {
		return value;
	}
}
