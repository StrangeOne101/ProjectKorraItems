package com.projectkorra.items.attribute;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.SubElement;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;

public class AttributeBuilder {
	
	public static final Map<String, AttributeTarget> targets = new HashMap<String, AttributeTarget>();
	public static final Map<String, AttributeType> types = new HashMap<String, AttributeType>();
	//public static final Map<String, Attribute> attributes = new HashMap<String, Attribute>();
	private static final Map<Class<? extends CoreAbility>, Set<String>> ABILITY_ATTRIBUTES = new HashMap<>();

	private static final String[] basicTypes = {
			com.projectkorra.projectkorra.attribute.Attribute.CHARGE_DURATION,
			com.projectkorra.projectkorra.attribute.Attribute.COOLDOWN,
			com.projectkorra.projectkorra.attribute.Attribute.DAMAGE,
			com.projectkorra.projectkorra.attribute.Attribute.DURATION,
			com.projectkorra.projectkorra.attribute.Attribute.HEIGHT,
			com.projectkorra.projectkorra.attribute.Attribute.KNOCKBACK,
			com.projectkorra.projectkorra.attribute.Attribute.FIRE_TICK,
			com.projectkorra.projectkorra.attribute.Attribute.KNOCKUP,
			com.projectkorra.projectkorra.attribute.Attribute.RADIUS,
			com.projectkorra.projectkorra.attribute.Attribute.RANGE,
			com.projectkorra.projectkorra.attribute.Attribute.SELECT_RANGE,
			com.projectkorra.projectkorra.attribute.Attribute.SPEED,
			com.projectkorra.projectkorra.attribute.Attribute.WIDTH
		};
	
	public static class ElementTarget extends AttributeTarget {

		private Element element;
		
		public ElementTarget(String name, Element element) {
			super(name, AttributePriority.NORMAL);
			this.element = element;
		}
		
		@Override
		public boolean affects(CoreAbility ability) {
			if (ability.getElement() instanceof SubElement) { //If it's a subelement ability, get the parent element
				return ((SubElement)ability.getElement()).getParentElement() == element;
			}
			return ability.getElement() == element;
		}	
		
	}
	
	public static class SubElementTarget extends AttributeTarget {

		private SubElement element;
		
		public SubElementTarget(String name, SubElement element) {
			super(name, AttributePriority.HIGH);
			this.element = element;
		}
		
		@Override
		public boolean affects(CoreAbility ability) {
			return ability.getElement() == element;
		}	
		
	}
	
	public static class AbilityTarget extends AttributeTarget {
		
		private CoreAbility ability;

		public AbilityTarget(String prefix, CoreAbility ability) {
			super(prefix, AttributePriority.LOW);
			this.ability = ability;
		}

		@Override
		public boolean affects(CoreAbility ability) {
			return this.ability.getClass().equals(ability.getClass());
		}
		
	}
	
	public static class ComboTarget extends AttributeTarget {

		public ComboTarget(String prefix) {
			super(prefix, AttributePriority.LOW);
		}

		@Override
		public boolean affects(CoreAbility ability) {
			return ability instanceof ComboAbility;
		}
		
	}

	public static class ElementComboTarget extends ComboTarget {

		private Element element;
		
		public ElementComboTarget(String prefix, Element element) {
			super(prefix);
			this.element = element;
		}
		
		@Override
		public boolean affects(CoreAbility ability) {
			if (ability.getElement() instanceof SubElement) { //If it's a subelement ability, get the parent element
				return ((SubElement)ability.getElement()).getParentElement() == element && ability instanceof ComboAbility;
			}
			return ability.getElement() == element && ability instanceof ComboAbility;
		}
	}

	public static class CoreTarget extends AttributeTarget {

		public CoreTarget() {
			super("all", AttributePriority.LOWEST);
		}

		@Override
		public boolean affects(CoreAbility ability) {
			return !(ability instanceof PassiveAbility) || (((PassiveAbility) ability).isInstantiable() && ((PassiveAbility) ability).isProgressable());
		}
	}
	
	private static void setupBasicTargets() {
		if (!targets.isEmpty()) return; //We have already set up
		
		//Go through all subs and define prefixes that affect every ability of that subelement
		//Subs have a priority of HIGH because they go before elements and before abilities
		for (final SubElement sub : Element.getAllSubElements()) {
			targets.put(sub.getName().toLowerCase(), new SubElementTarget(sub.getName(), sub));
			
			//Same as above, but SUBELEMENTAbility instead of just SUBELEMENT
			targets.put(sub.getName().toLowerCase() + "ability", new SubElementTarget(sub.getName() + "ability", sub));
		
			targets.put(sub.getName().toLowerCase() + "combo", new ElementComboTarget(sub.getName() + "combo", sub));
		}
		
		//Like subs, go through all elements and define their prefixes. Priority of NORMAL so its after subs
		//but before abilities
		for (final Element element : Element.getAllElements()) {
			targets.put(element.getName().toLowerCase(), new ElementTarget(element.getName(), element));
			
			//Same as above, but it is ELEMENTAbility instead of just ELEMENT
			targets.put(element.getName().toLowerCase() + "ability", new ElementTarget(element.getName(), element));
			
			targets.put(element.getName().toLowerCase() + "combo", new ElementComboTarget(element.getName() + "combo", element));
		}
		
		targets.put("combo", new ComboTarget("combo"));
		targets.put("all", new CoreTarget());
		
		//Time for abilities! Priority of low because they go last
		/*for (final CoreAbility abil : CoreAbility.getAbilities()) {
			if (prefixes.containsKey(abil.getName().toLowerCase())) continue; //Some abilities may double up on the same name
			
			prefixes.put(abil.getName().toLowerCase(), new AbilityTarget(abil.getName().toLowerCase(), abil));
		}*/
	}
	
	private static void setupBasicTypes() {
		if (!types.isEmpty()) return;

		for (String s : basicTypes) { //Create basic suffixes for Damage, Cooldown, Duration, etc
			types.put(s.toLowerCase(), new AttributeType(s));
		}
		
		types.put("resistance", new AttributeType("Resistance", "Resistance", AttributeEvent.DAMAGE_RECEIVED)); //PKI attribute to reduce damage
	}
	
	public static void setupAttributes() {
		if (!ABILITY_ATTRIBUTES.isEmpty()) ABILITY_ATTRIBUTES.clear();
		
		long time = System.currentTimeMillis();
		
		setupBasicTargets();
		setupBasicTypes();
		
		for (CoreAbility ability : CoreAbility.getAbilities()) {
			if (!ability.isEnabled()) continue;
			
			Class<? extends CoreAbility> clazz = ability.getClass();
			
			AttributeTarget prefix = new AbilityTarget(ability.getName(), ability);
			targets.put(ability.getName().toLowerCase(), prefix); //Register
			
			for (Field f : clazz.getDeclaredFields()) {
				if (f.isAnnotationPresent(com.projectkorra.projectkorra.attribute.Attribute.class)) {
					com.projectkorra.projectkorra.attribute.Attribute annotation = f.getAnnotation(com.projectkorra.projectkorra.attribute.Attribute.class);
					String attrName = annotation.value();
					
					AttributeType type = types.get(attrName.toLowerCase());
					
					if (type == null) {
						type = new AttributeType(attrName, attrName);
						types.put(attrName.toLowerCase(), type); //Register the type in case it needs to be used next time
					}

					if (!ABILITY_ATTRIBUTES.containsKey(clazz)) {
						ABILITY_ATTRIBUTES.put(clazz, new HashSet<>());
					}
					ABILITY_ATTRIBUTES.get(clazz).add(attrName.toLowerCase());
				}
			}
		}
		long taken = System.currentTimeMillis() - time;
	}
}
