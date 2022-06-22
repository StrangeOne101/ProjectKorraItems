package com.projectkorra.items.attribute;

import com.projectkorra.projectkorra.ability.CoreAbility;

public abstract class AttributeTarget {
	
	private String prefix;
	private AttributePriority priority;
	
	public AttributeTarget(String prefix, AttributePriority priority) {
		this.prefix = prefix;
		this.priority = priority;
	}
	
	public abstract boolean affects(CoreAbility ability);
	
	public enum AttributePriority {
		HIGHEST(2), HIGH(1), NORMAL(0), LOW(-1), LOWEST(-2);
		
		private int power;
		
		AttributePriority(int i) {
			this.power = i;
		}
		
		public int getPower() {
			return power;
		}
		
	};

	public String getPrefix() {
		return prefix;
	}
	
	public AttributePriority getPriority() {
		return priority;
	}
}
