package com.projectkorra.items.attribute;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.attribute.Attribute.AttributeEvent;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.SubElement;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;

public class AttributeBuilder {
	
	public static final Map<String, AttributePrefix> prefixes = new HashMap<String, AttributePrefix>();
	public static final Map<String, AttributeSuffix> suffixes = new HashMap<String, AttributeSuffix>();
	public static final Map<String, Attribute> attributes = new HashMap<String, Attribute>();
	private static final Map<Class<? extends CoreAbility>, Set<String>> abilityInnerAttributes = new HashMap<>();

	private static final String[] basicSuffixes = {
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
	
	public static class ElementPrefix extends AttributePrefix {

		private Element element;
		
		public ElementPrefix(String name, Element element) {
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
	
	public static class SubElementPrefix extends AttributePrefix {

		private SubElement element;
		
		public SubElementPrefix(String name, SubElement element) {
			super(name, AttributePriority.HIGH);
			this.element = element;
		}
		
		@Override
		public boolean affects(CoreAbility ability) {
			return ability.getElement() == element;
		}	
		
	}
	
	public static class AbilityPrefix extends AttributePrefix {
		
		private CoreAbility ability;

		public AbilityPrefix(String prefix, CoreAbility ability) {
			super(prefix, AttributePriority.LOW);
			this.ability = ability;
		}

		@Override
		public boolean affects(CoreAbility ability) {
			return this.ability.getClass().equals(ability.getClass());
		}
		
	}
	
	public static class ComboPrefix extends AttributePrefix {

		public ComboPrefix(String prefix) {
			super(prefix, AttributePriority.LOW);
		}

		@Override
		public boolean affects(CoreAbility ability) {
			return ability instanceof ComboAbility;
		}
		
	}

	public static class ElementComboPrefix extends ComboPrefix {

		private Element element;
		
		public ElementComboPrefix(String prefix, Element element) {
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

	/**
	 * An attribute subclass that will check if the ability given actually has the attribute
	 */
	public static class BroadAttribute extends Attribute {

		public BroadAttribute(AttributePrefix prefix, AttributeSuffix suffix, AttributeEvent event) {
			super(prefix, suffix, event);
		}

		@Override
		public boolean affects(CoreAbility ability) { //We are also checking if the ability contains the specific attribute
			return super.affects(ability) && abilityInnerAttributes.containsKey(ability.getClass())
					&& abilityInnerAttributes.get(ability.getClass()).contains(getSuffix().getActualAttributeName().toLowerCase());
		}
	}
	
	private static void setupBasicPrefixes() {
		if (!prefixes.isEmpty()) return; //We have already set up
		
		//Go through all subs and define prefixes that affect every ability of that subelement
		//Subs have a priority of HIGH because they go before elements and before abilities
		for (final SubElement sub : Element.getAllSubElements()) {
			prefixes.put(sub.getName().toLowerCase(), new SubElementPrefix(sub.getName(), sub));
			
			//Same as above, but SUBELEMENTAbility instead of just SUBELEMENT
			prefixes.put(sub.getName().toLowerCase() + "ability", new SubElementPrefix(sub.getName() + "ability", sub));
		
			prefixes.put(sub.getName().toLowerCase() + "combo", new ElementComboPrefix(sub.getName() + "combo", sub));
		}
		
		//Like subs, go through all elements and define their prefixes. Priority of NORMAL so its after subs
		//but before abilities
		for (final Element element : Element.getAllElements()) {
			prefixes.put(element.getName().toLowerCase(), new ElementPrefix(element.getName(), element));
			
			//Same as above, but it is ELEMENTAbility instead of just ELEMENT
			prefixes.put(element.getName().toLowerCase() + "ability", new ElementPrefix(element.getName(), element));
			
			prefixes.put(element.getName().toLowerCase() + "combo", new ElementComboPrefix(element.getName() + "combo", element));
		}
		
		prefixes.put("combo", new ComboPrefix("combo"));
		
		//Time for abilities! Priority of low because they go last
		/*for (final CoreAbility abil : CoreAbility.getAbilities()) {
			if (prefixes.containsKey(abil.getName().toLowerCase())) continue; //Some abilities may double up on the same name
			
			prefixes.put(abil.getName().toLowerCase(), new AbilityPrefix(abil.getName().toLowerCase(), abil));
		}*/
	}
	
	private static void setupBasicSuffixes() {
		if (!suffixes.isEmpty()) return;

		for (String s : basicSuffixes) { //Create basic suffixes for Damage, Cooldown, Duration, etc
			suffixes.put(s.toLowerCase(), new AttributeSuffix(s));
		}
		
		suffixes.put("resistance", new AttributeSuffix("Resistance")); //PKI attribute to reduce damage
	}
	
	public static void setupAttributes() {
		if (!attributes.isEmpty()) return;
		
		long time = System.currentTimeMillis();
		
		setupBasicPrefixes();
		setupBasicSuffixes();
		
		Set<AttributePrefix> basicPrefixes = new HashSet<AttributePrefix>();
		basicPrefixes.addAll(prefixes.values());
		
		for (CoreAbility ability : CoreAbility.getAbilities()) {
			if (!ability.isEnabled()) continue;
			
			Class<? extends CoreAbility> clazz = ability.getClass();
			
			AttributePrefix prefix = new AbilityPrefix(ability.getName(), ability);
			prefixes.put(ability.getName().toLowerCase(), prefix); //Register
			
			for (Field f : clazz.getDeclaredFields()) {
				if (f.isAnnotationPresent(com.projectkorra.projectkorra.attribute.Attribute.class)) {
					com.projectkorra.projectkorra.attribute.Attribute annotation = f.getAnnotation(com.projectkorra.projectkorra.attribute.Attribute.class);
					String attributeFieldName = annotation.value();
					
					AttributeSuffix suffix = suffixes.get(attributeFieldName.toLowerCase());
					
					if (suffix == null) {
						suffix = new AttributeSuffix(attributeFieldName, attributeFieldName);
						suffixes.put(attributeFieldName.toLowerCase(), suffix); //Register the suffix in case it needs to be used next time
					}

					if (!abilityInnerAttributes.containsKey(clazz)) {
						abilityInnerAttributes.put(clazz, new HashSet<String>());
					}
					abilityInnerAttributes.get(clazz).add(attributeFieldName.toLowerCase());
					
					Attribute attribute = new Attribute(prefix, suffix);
					attributes.put(prefix.getPrefix().toLowerCase() + suffix.getName().toLowerCase(), attribute);
				}
			}
		}
		
		//For every suffix added from the classes, create attributes from those suffixes for Elements, Combos, etc
		for (AttributePrefix prefix : basicPrefixes) { //Elements, combos, subs, etc. Not including individual element prefixes
			for (AttributeSuffix suffix : suffixes.values()) {
				Attribute attribute = new BroadAttribute(prefix, suffix, AttributeEvent.ABILITY_START);
				
				if (suffix.getActualAttributeName().equals("Resistance")) {
					attribute = new BroadAttribute(prefix, suffix, AttributeEvent.DAMAGE_RECIEVED);
				}
				
				attributes.put(prefix.getPrefix().toLowerCase() + suffix.getName().toLowerCase(), attribute);
			}
		}
		
		addAttributeAlias(attributes.get("eartharmorgoldenhearts"), "eartharmorhearts");
		
		//ProjectKorraItems.log.info("Registered " + attributes.size() + " attributes in " + (System.currentTimeMillis() - time) + "ms");
		
		/*for (AttributePrefix prefix : prefixes.values()) {
			for (String suffixString : basicSuffixes) {
				AttributeSuffix suffix = suffixes.get(suffixString.toLowerCase());
				if (suffix != null && suffix.canPair(prefix.getPrefix())) {
					attributes.put(prefix.getPrefix().toLowerCase() + suffix.getName().toLowerCase(), new Attribute(prefix, suffix));
				}
			}
			
			AttributeSuffix resistance = suffixes.get("resistance");
			
			attributes.put(prefix.getPrefix().toLowerCase() + resistance.getName().toLowerCase(), new Attribute(prefix, resistance, AttributeEvent.DAMAGE_RECIEVED));
		}
		
		AttributeSuffix heartSuffix = new AttributeSuffix("Hearts", "GoldenHearts") { //Suffix for EarthArmorHearts
			@Override
			public boolean canPair(String prefix) {
				return prefix.equalsIgnoreCase("EarthArmor");
			}
		};
		suffixes.put("hearts", heartSuffix);
		attributes.put("eartharmorhearts", new Attribute(prefixes.get("eartharmor"), heartSuffix));*/
	}
	
	/**
	 * Formats the string so it is like <code>String</code>
	 * @param name The name of the attribute
	 * @return The fixed string
	 */
	public static String format(String name) {
		if (name.toLowerCase().equals(name) || name.toUpperCase().equals(name)) {
			name = name.toUpperCase().charAt(0) + name.toLowerCase().substring(1); //Make first letter uppercase, rest lowercase
		} 
		
		return name;
	}
	
	public static void addAttributeAlias(Attribute attribute, String alias) {
		if (attribute == null) return;
		attributes.put(alias.toLowerCase(), attribute);
	}
	
	
}
