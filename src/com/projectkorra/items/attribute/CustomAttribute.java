package com.projectkorra.items.attribute;

import com.projectkorra.projectkorra.ability.CoreAbility;

public abstract class CustomAttribute extends Attribute {

	public CustomAttribute(String name, String desc, Class<? extends CoreAbility> ability) {
		super(name, desc, ability);
	}

	@Override
	public abstract boolean handle(CoreAbility realAbility, String value);

}
