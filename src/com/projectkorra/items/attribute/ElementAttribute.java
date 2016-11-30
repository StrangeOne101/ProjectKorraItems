package com.projectkorra.items.attribute;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.SubElement;
import com.projectkorra.projectkorra.ability.CoreAbility;

public class ElementAttribute extends DefaultAttribute {

	public Element element;
	
	public ElementAttribute(String name, String desc, Element element, String getter, String setter) {
		super(name, desc, null, getter, setter);

		this.element = element;
		this.priority = element instanceof SubElement ? AttributePriority.HIGH : AttributePriority.HIGHEST;
	}

	@Override
	public boolean handle(CoreAbility realAbility, String value) {
		if (realAbility.getElement() != element) return false;
	
		try {
			return super.handle(realAbility, value);
		} catch (Exception e) {} //Don't print stacktraces if it fails to find the right methods. This is bound to happen lots.
		return false;
	}

}
