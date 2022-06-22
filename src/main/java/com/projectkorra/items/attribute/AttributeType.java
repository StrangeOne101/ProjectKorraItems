package com.projectkorra.items.attribute;

public class AttributeType {

	private String name;
	private String actualAttributeName;
	private AttributeEvent event;
	
	public AttributeType(String name, String actualAttributeName, AttributeEvent eventType) {
		this.name = name;
		this.actualAttributeName = actualAttributeName;
		this.event = eventType;
	}

	public AttributeType(String name, String actualAttributeName) {
		this(name, actualAttributeName, AttributeEvent.ABILITY_START);
	}
	
	public AttributeType(String name) {
		this(name, name);
	}
	
	public String getName() {
		return name;
	}
	
	public String getActualAttributeName() {
		return actualAttributeName;
	}
	
	/**
	 * Override this to make this suffix only for pair with certain prefixes. E.g.
	 * "EarthArmor" for golden hearts
	 * @param prefix The prefix
	 * @return True if it can pair with the prefix to make an attribute
	 */
	public boolean canPair(String prefix) {
		return true;
	}

	void setActualAttributeName(String name) {
		this.actualAttributeName = name;
	}
	
	void setName(String name) {
		this.name = name;
	}

	public AttributeEvent getEvent() {
		return event;
	}
}
