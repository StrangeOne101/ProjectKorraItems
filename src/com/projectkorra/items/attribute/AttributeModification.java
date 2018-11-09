package com.projectkorra.items.attribute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.AttributeModifier;

public class AttributeModification {
	
	private Object value;
	private AttributeModifier modifier;
	private Attribute attribute;
	private PKItem source;
	
	private static Method modifyAttributesMethod;
	
	public AttributeModification(Attribute attribute, String modification, PKItem source) {
		this.attribute = attribute;
		this.source = source;
		
		modification = modification.replaceAll("[ A-Za-z]", "");
		if (isValidModification(modification)) {
			AttributeModifier modifier = null; //Null is for setting the field to the exact value
			double value;
			String parsingValue = modification;
			
			if (parsingValue.equalsIgnoreCase("false") || parsingValue.equalsIgnoreCase("true")) {
				this.value = parsingValue.equalsIgnoreCase("false") ? false : true;
				return; //The value is a boolean - no need to continue parsing
			}
			
			if (modification.startsWith("+")) {
				parsingValue = parsingValue.substring(1); //Get rid of the plus
				modifier = AttributeModifier.ADDITION;
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
				parsingValue = parsingValue.substring(0, parsingValue.length() - 1);
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
		if (modifyAttributesMethod == null) { //It hasn't been set before
			try {
				modifyAttributesMethod = CoreAbility.class.getDeclaredMethod("modifyAttributes");
				modifyAttributesMethod.setAccessible(true);
			} catch (NoSuchMethodException | SecurityException e) {
				ProjectKorraItems.createError("Failed to get modifyAttributes method within CoreAbility");
				e.printStackTrace();
			}
		}
		
		if (attribute == null) return false;
		if (!attribute.getPrefix().affects(ability)) return false;
		int hash = ability.hashCode();
		if (modifier == null) {
			ability.setAttribute(attribute.getSuffix().getActualAttributeName(), value);
			System.out.println("Set " + attribute.getSuffix().getActualAttributeName() + " to " + value);
		} else {
			ability.addAttributeModifier(attribute.getSuffix().getActualAttributeName(), (Number) value, modifier).hashCode();
		}
		
		try {
			modifyAttributesMethod.invoke(ability); //Force CoreAbility to change the values of it's variables
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			ProjectKorraItems.createError("Failed to invoke modifyAttributes method! Attributes not set!");
			e.printStackTrace();
		}
		
		System.out.println("Comparing hashes: " + hash + " | " + ability.hashCode());
		return ability.hashCode() != hash; //If the ability has different values now
	}
	
	public static boolean isValidModification(String modification) {
		modification = modification.replaceAll(" ", "");
		if (modification.endsWith("%")) {
			String percent = modification.substring(0, modification.length() - 1); //Cut off the %
			if (percent.startsWith("+") || percent.startsWith("-")) percent = percent.substring(1);
			try {
				Double.parseDouble(percent);
				return true;
			} catch (NumberFormatException e) { return false; }
		} else if (modification.startsWith("x") || modification.startsWith("*") || modification.startsWith("/")) {
			try {
				Double.parseDouble(modification.substring(1));
				return true;
			} catch (NumberFormatException e) { return false; }
		} else if (modification.startsWith("+") || modification.startsWith("-")) {
			try {
				Double.parseDouble(modification.substring(1));
				return true;
			} catch (NumberFormatException e) { return false; }
		} else {
			try {
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
		return (double)value;
	}
	
	public boolean getBooleanValue() {
		return Boolean.parseBoolean(value.toString());
	}
}
