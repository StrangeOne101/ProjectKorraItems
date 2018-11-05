package com.projectkorra.items.attribute.old;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.SubElement;
import com.projectkorra.projectkorra.ability.CoreAbility;

public class ElementAttribute extends DefaultAttribute {
	
	public ElementAttribute(String name, String desc, Element element, String getter, String setter) {
		super(name, desc, null, getter, setter);

		this.priority = element instanceof SubElement ? AttributePriority.HIGH : AttributePriority.HIGHEST;
		
		//Adds all abilities from the matching element to the abilites list. Then it just handles it like a default attribute.
		for (CoreAbility abil : CoreAbility.getAbilities()) {
			if (abil.isHiddenAbility()) continue;
			
			if (abil.getElement() == element || (element instanceof SubElement && element == ((SubElement)abil.getElement()).getParentElement())) {
				this.addAbility(abil.getClass());
			}
		}
	}

	@Override
	public boolean handle(CoreAbility realAbility, String value) {	
		try {
			return super.handle(realAbility, value);
		} catch (Exception e) {} //Don't print stacktraces if it fails to find the right methods. This is bound to happen lots.
		return false;
	}
	
	@Override
	public AttributePriority getPriority() {
		return AttributePriority.HIGH;
	}

}
