package com.projectkorra.items.attribute;

import java.lang.reflect.Method;

import com.projectkorra.items.utils.GenericUtil;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.ReflectionHandler;

public class DefaultAttribute extends Attribute {

	public String getter;
	public String setter;
	
	public DefaultAttribute(String name, String desc, Class<? extends CoreAbility> ability, String getter, String setter) {
		super(name, desc, ability);
		
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public boolean handle(CoreAbility realAbility, String value) {
		if (!realAbility.getClass().equals(this.getAbility())) return false;
		
		value = value.replaceAll(" ", "");
		
		try {
			Method method = ReflectionHandler.getMethod(realAbility.getClass(), getter);
			Object object = method.invoke(realAbility);
			double startValue = (double)object;
			double newValue = startValue;
			
			if (value.startsWith("+")) { //Adding to the current value
				if (value.endsWith("%")) {
					if (!GenericUtil.isDouble(value.substring(1, value.length() - 2))) return false;
					newValue = startValue * (1 + (Double.parseDouble(value.substring(0, value.length() - 2)) / 100));
				} else {
					if (!GenericUtil.isDouble(value.substring(1))) return false;
					newValue += Double.parseDouble(value.substring(1));
				}
			} else if (value.startsWith("x")) { //Multiplying current value
				if (!GenericUtil.isDouble(value.substring(1))) return false;
				newValue *= Double.parseDouble(value.substring(1));
			} else if (value.startsWith("-")) { //Subtracting current value
				if (value.endsWith("%")) {
					if (!GenericUtil.isDouble(value.substring(1, value.length() - 2))) return false;
					newValue = startValue * (1 - (Double.parseDouble(value.substring(0, value.length() - 2)) / 100));
				} else {
					if (!GenericUtil.isDouble(value.substring(1))) return false;
					newValue -= Double.parseDouble(value.substring(1));
				}
			} else if (value.endsWith("%")) { //Setting it to a percentage
				if (!GenericUtil.isDouble(value.substring(0, value.length() - 2))) return false;
				newValue = startValue * (Double.parseDouble(value.substring(0, value.length() - 2)) / 100);
			} else { //Setting the value plain straight
				if (!GenericUtil.isDouble(value)) return false;
				newValue = Double.parseDouble(value);
			}
			
			Method method2 = ReflectionHandler.getMethod(realAbility.getClass(), setter);
			method2.invoke(realAbility, newValue); //Set the value again
			
			return true;
			
		} catch (Exception e) {
			//We are making 2 exceptions so we know what was the cause (1st one) and the actual exception behind it (2nd one)
			new Exception("Failed to get/set " + getter + "/" + setter + " method in " + realAbility.getClass().getName() + " with value " + value).printStackTrace();
			e.printStackTrace();
		}
		return false; 
	}

}
