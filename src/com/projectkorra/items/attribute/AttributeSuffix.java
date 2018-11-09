package com.projectkorra.items.attribute;

public class AttributeSuffix {
	
	private String name;
	private String actualAttributeName;
	
	public AttributeSuffix(String name, String actualAttributeName) {
		this.name = name;
		this.actualAttributeName = actualAttributeName;
	}
	
	public AttributeSuffix(String name) {
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
}
