package com.projectkorra.items.attribute;

import java.util.HashMap;
import java.util.Map;

import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.earthbending.RaiseEarthWall;
import com.projectkorra.projectkorra.util.ReflectionHandler;

public abstract class Attribute {
	
	public enum AttributePriority {
		HIGHEST(2), HIGH(1), NORMAL(0), LOW(-1), LOWEST(-2);
		
		public int power;
		
		AttributePriority(int i) {
			this.power = i;
		}
		
	};
	
	
	
	private static Map<String, Attribute> attributeList = new HashMap<String, Attribute>();
	
	//private static final String[] defaultAttributeTypes = new String[] {"damage", "range", "selectrange", "speed", "cooldown"};
	
	private String name;
	private String desc;
	
	/*
	 * Fake ability instance passed in for registration, this can be null because
	 * some attributes don't require an ability instance.
	 */
	private Class<? extends CoreAbility> ability;
	
	/*
	 * RequireElement, RequireWorld, RequirePermission should be able to prevent other attributes from being
	 * added to the ability. If this is true then the ability have it's attributes modified. Make sure that if any
	 * Attributes set this to true then you stop checking other attributes. These attributes should be checked first.
	 */
	private boolean canCancelAttributes;
	
	/*
	 * An Attribute wants to cancel, perhaps the user didn't have the required element.
	 */
	private boolean canceled;
	
	
	private boolean canCombine;
	
	/*
	 * Some variables that let you decide when you want the handle method to be called.
	 * You can add more of these if needed.
	 */
	private boolean handleOnAbilityStart;
	private boolean handleOnClick;
	private boolean handleOnShift;
	private boolean handleOnPlayerTakeDamage;
	
	protected AttributePriority priority;

	public Attribute(String name, String desc, Class<? extends CoreAbility> ability) {
		this.name = name;
		this.desc = desc;
		this.ability = ability;
		this.canCancelAttributes = false;
		this.canceled = false;
		this.handleOnAbilityStart = true;
		this.handleOnClick = false;
		this.handleOnShift = false;
		this.handleOnPlayerTakeDamage = false;
		this.priority = AttributePriority.NORMAL;
	}

	/***
	 * Called when the player uses an ability with an item that has this attribute.
	 * 
	 * @param realAbility The ability being created
	 * @param value The value of the Attribute to be parsed (from the config)
	 * @return Whether the attribute effected the ability or not. The item will be
	 * damaged if this returns true.
	 */
	public abstract boolean handle(CoreAbility realAbility, String value);

	public void setHandleOnAbilityStart(boolean handleOnAbilityStart) {
		this.handleOnAbilityStart = handleOnAbilityStart;
	}

	public void setHandleOnClick(boolean handleOnClick) {
		this.handleOnClick = handleOnClick;
	}

	public void setHandleOnShift(boolean handleOnShift) {
		this.handleOnShift = handleOnShift;
	}

	public void setHandleOnPlayerTakeDamage(boolean handleOnPlayerTakeDamage) {
		this.handleOnPlayerTakeDamage = handleOnPlayerTakeDamage;
	}
	
	public boolean isHandleOnAbilityStart() {
		return handleOnAbilityStart;
	}
	
	public boolean isHandleOnPlayerTakeDamage() {
		return handleOnPlayerTakeDamage;
	}
	
	/**Returns whether the provided attribute exists or not*/
	public static boolean isAttribute(String name) {
		return attributeList.containsKey(name.toLowerCase());
	}

	public static void registerDefaultAttributes() {
		attributeList.clear();
		
		String[] getters = new String[] {"getDamage", "getSpeed", "getCooldown", "getRange", "getSelectRange", "getPower", "getRadius", "getForce"};
		String[] setters = new String[] {"setDamage", "setSpeed", "setCooldown", "setRange", "setSelectRange", "setPower", "setRadius", "setForce"};
		String[] attrNames = new String[] {"Damage", "Speed", "Cooldown", "Range", "SelectRange", "Power", "Radius", "Force"};
		
		ProjectKorraItems.log.info("Debug length: "+ CoreAbility.getAbilities().size());
		
		for (int i = 0; i < getters.length; i++) {
			
			for (Element element : Element.getAllElements()) {
				String name = element.getName() + attrNames[i];
				String desc = "Sets the " + attrNames[i] + " for all " + element.getName() + " abilities";
				
				Attribute attribute = new ElementAttribute(name, desc, element, getters[i], setters[i]);
				attributeList.put(name.toLowerCase(), attribute);
			}
			
			for (CoreAbility ability : CoreAbility.getAbilities()) {
				
				try {
					//Testing if both the methods exist. If not, continue to the next one.
					
					
					String name = ability.getClass().getName().split("\\.")[ability.getClass().getName().split("\\.").length - 1] + attrNames[i];
					String desc = "Sets the " + attrNames[i] + " of " + ability.getName();
					
					ReflectionHandler.getMethod(ability.getClass(), getters[i]);
					ReflectionHandler.getMethod(ability.getClass(), setters[i], Double.class);
					
					ProjectKorraItems.log.info(name + "| " + desc);
					
					Attribute attribute = new DefaultAttribute(name, desc, ability.getClass(), getters[i], setters[i]);
					attributeList.put(name.toLowerCase(), attribute);
				} catch (NoSuchMethodException e) {}
			}
		}
		
		registerAttribute("RaiseEarthWallWidth", RaiseEarthWall.class, "getWidth", "setWidth");
		registerAttribute("RaiseEarthWallHeight", RaiseEarthWall.class, "getHeight", "setHeight");
		
		
		
		/*
		 * Addon developers can registerAttributes from their own code. They do not have
		 * to provide a CoreAbility if they do not wish to do so.
		 */
		/*Attribute fireBlastDamage = new Attribute("FireBlastDamage", "Increases FireBlast Damage", CoreAbility.getAbility("FireBlast")) {
			@Override
			public void handle(CoreAbility realAbility, Object value) {
				if (!(realAbility instanceof FireBlast) || !Utils.isDouble(value)) {
					return;
				}
				FireBlast fb = (FireBlast) realAbility;
				fb.setDamage(fb.getDamage() * (1 + Double.parseDouble(value)));
			}
		};*/
		
		//fireBlastDamage.setHandleOnAbilityStart(true); // For example 
		//ProjectKorraItems.registerAttribute(fireBlastDamage);
	}
	
	/***
	 * Registers a normal attribute for abilities that aren't registered by default.
	 * 
	 * @param name The Attribute name. This should be what users have to put in their items config to get this Attribute. E.g. EarthBlastDamage
	 * @param ability The {@link CoreAbility} that should be effected
	 * @param getter The name of the getter method that gets the value
	 * @param setter The name of the setter method that sets the value once it has been changed.
	 */
	public static void registerAttribute(String name, Class<? extends CoreAbility> ability, String getter, String setter) {
		Attribute attribute = new DefaultAttribute(name, "Modifies a value in " + ability.getName(), ability, getter, setter);
		attributeList.put(name.toLowerCase(), attribute);		
	}
	
	/**
	 * Register a custom attribute.
	 * @param attribute The custom attribute
	 */
	public static void registerAttribute(CustomAttribute attribute) {
		attributeList.put(attribute.getName(), attribute);		
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public Class<? extends CoreAbility> getAbility() {
		return ability;
	}

	public AttributePriority getPriority() {
		return priority;
	}
	
	public static Attribute valueOf(String string) {
		return attributeList.get(string.toLowerCase());
	}
}