package com.projectkorra.items.attribute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.AttributeModifier;
import com.projectkorra.projectkorra.event.AbilityDamageEntityEvent;

public class AttributeModification {
	
	private Object value;
	private AttributeModifier modifier;
	private AttributeTarget target;
	private AttributeType trait;
	private PKItem source;
	
	//private static Method modifyAttributesMethod;
	
	public AttributeModification(AttributeTarget target, AttributeType trait, String modification, PKItem source) {
		this.target = target;
		this.trait = trait;
		this.source = source;
		
		modification = modification.replaceAll("[ A-Za-z]", "");
		if (isValidModification(modification)) {
			AttributeModifier modifier = null; //Null is for setting the field to the exact value
			double value;
			String parsingValue = modification;
			
			if (parsingValue.equalsIgnoreCase("false") || parsingValue.equalsIgnoreCase("true")) {
				this.value = !parsingValue.equalsIgnoreCase("false");
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
				ProjectKorraItems.createError("Cannot parse modification \"" + modification + "\" for Attribute \"" + target.getPrefix() + ":" + trait.getActualAttributeName());
			}
		}
	}
	
	/**
	 * Tries to apply the attribute modification to the provided ability
	 * @param ability The ability
	 * @return True if it applied
	 */
	public boolean performModification(CoreAbility ability) {
		/*if (modifyAttributesMethod == null) { //It hasn't been set before
			try {
				modifyAttributesMethod = CoreAbility.class.getDeclaredMethod("modifyAttributes");
				modifyAttributesMethod.setAccessible(true);
			} catch (NoSuchMethodException | SecurityException e) {
				ProjectKorraItems.createError("Failed to get modifyAttributes method within CoreAbility");
				e.printStackTrace();
			}
		}*/
		
		if (target == null || !target.affects(ability)) return false; //if the attribute doesn't affect the ability, it didn't work
		if (modifier == null) {
			ability.setAttribute(trait.getActualAttributeName(), value);
			//System.out.println("Set " + attribute.getSuffix().getActualAttributeName() + " to " + value);
		} else {
			ability.addAttributeModifier(trait.getActualAttributeName(), (Number) value, modifier);
		}

		return true;
	}

	public boolean performDamageModification(CoreAbility ability, AbilityDamageEntityEvent event) {
		if (target == null || !target.affects(ability)) return false; //if the attribute doesn't affect the ability, it didn't work

		if (trait.getName().equalsIgnoreCase("resistance")) {
			double percentage = 100;

			if (modifier == null) percentage = getValue() * 100;
			else percentage = (double) getModifier().performModification(percentage, getValue());

			percentage /= 100;

			event.setDamage(event.getDamage() * (1D - percentage));
			return true;
		}
		return false;
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
	
	public AttributeType getAttributeTrait() {
		return trait;
	}

	public AttributeTarget getAttributeTarget() {
		return target;
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
