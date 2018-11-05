package com.projectkorra.items.attribute;

public class Attribute {
	
	public enum AttributeEvent {
		ABILITY_START, MOVE, DAMAGE_RECIEVED, CUSTOM;
	}
	
	private AttributePrefix prefix;
	private AttributeSuffix suffix;
	private AttributeEvent event;
	
	public Attribute(AttributePrefix prefix, AttributeSuffix suffix, AttributeEvent event) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.event = event;
	}
	
	public Attribute(AttributePrefix prefix, AttributeSuffix suffix) {
		this(prefix, suffix, AttributeEvent.ABILITY_START);
	}
	
	public String getAttributeName() {
		return prefix.getPrefix() + suffix.getName();
	}
	
	public static boolean isAttribute(String attribute) {
		return AttributeBuilder.attributes.containsKey(attribute.toLowerCase());
	}
	
	public static Attribute getAttribute(String attribute) {
		return AttributeBuilder.attributes.get(attribute.toLowerCase());
	}

	public AttributePrefix getPrefix() {
		return prefix;
	}
	
	public AttributeSuffix getSuffix() {
		return suffix;
	}
	
	public AttributeEvent getEvent() {
		return event;
	}
}
