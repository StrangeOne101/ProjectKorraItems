package com.projectkorra.items.configuration;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.PKItem.Usage;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.attribute.Requirements;
import com.projectkorra.projectkorra.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.MaterialData;

public class ConfigManager {

	public static Config itemsConfig;
	public static Config languageConfig;
	public static Config recipeConfig;
	
	public ConfigManager() {
		
		itemsConfig = new Config(new File("items.yml"));
		languageConfig = new Config(new File("language.yml"));
		recipeConfig = new Config(new File("recipe.yml"));
		
		checkLanguageFile();
		loadItems();
		loadRecipes();
		
	}
	
	public void loadRecipes() {
		
		
	}

	public void checkLanguageFile() {
		FileConfiguration config = languageConfig.get();
		
		config.addDefault("Item.Load.Error", "Failed to load item {item}! Invalid {value}!");
		config.addDefault("Item.Load.DuplicateID", "Items {item1} and {item2} both have an ID of {id}! ");
		config.addDefault("Item.Load.SurpassMaxItems", "You have defined too many items as the max you can define is 256! Any items defined after this will no longer be usable in game.");
		config.addDefault("Item.Load.InvalidMaterial", "Failed to load the material of {item}! \"{material}\" is not a valid material!");
		config.addDefault("Item.Load.InvalidUsage", "Failed to load the usage of {item}! \"{usage}\" is not one of HOLD, WEAR, CONSUMEABLE, CONTAIN");
		
		config.addDefault("Item.Use.DuplicateID", "This item cannot be used because it has a duplicate ID! Please report this to your admin!");
		config.addDefault("Item.Use.Break", "Your {item} has broken!");
		config.addDefault("Item.Use.Anvil", "You can't place this item in an anvil!");
		
		config.addDefault("Item.Give.ItemNotFound", "Item {item} not found!");
		config.addDefault("Item.Give.Disabled", "This item is disabled right now!");
		config.addDefault("Item.Give.DontOwn", "This item can only be used by {owner}!");
		config.addDefault("Item.Give.NotANumber", "The page you entered is not a number!");
		config.addDefault("Item.Give.GiveToPlayer", "You were given {item}!");
		
		config.addDefault("Item.Command.PlayerOnly", "This command can only be run by players!");
		config.addDefault("Item.Command.NoPermission", "You don't have permission for this command!");
		config.addDefault("Item.Command.NotANumber", "You don't have permission for this command!");
		config.addDefault("Item.Command.PlayerNotFound", "Player not found!");
		config.addDefault("Item.Command.Reload", "ProjectKorra Items reloaded!");
		
		
		config.addDefault("Item.Lore.Durability", "Durability: ({durability}/{maxdurability})!");
		config.addDefault("Item.Lore.Usage.Consumable", "Right click while holding to use!");
		config.addDefault("Item.Lore.Usage.Wearable", "Must be worn for the effects to show!");
		config.addDefault("Item.Lore.Usage.Hold", "Active while holding!");
	}
	
	@SuppressWarnings("deprecation")
	public void loadItems() {
		FileConfiguration config = itemsConfig.get();
		Random random = new Random();
		
		boolean overMax = false;
		
		for (String itemName : config.getKeys(false)) {
			if (PKItem.INSTANCE_MAP.size() >= 255) {
				if (!overMax) {
					ProjectKorraItems.createError(languageConfig.get().getString("Item.Load.SurpassMaxItems"));
					overMax = true;
				}
				PKItem.DISABLED.add(itemName);
			}
			
			byte id;
			
			ConfigurationSection configitem = config.getConfigurationSection(itemName);

			if (config.contains(itemName + ".SecretID")) {
				id = (byte) (config.getInt(itemName + ".SecretID") - 128);
			} else {
				id = (byte) random.nextInt(256);
				
				while (PKItem.INSTANCE_MAP.containsKey(id)) {
					id++;
				}
			}
			
			PKItem item = new PKItem(itemName, id);
			
			if (configitem.contains("Name")) {
				item.setDisplayName(configitem.getString("Name"));
			}
			if (configitem.contains("Lore")) {
				List<String> list = new ArrayList<String>();
				for (String s : configitem.getStringList("Lore")) {
					list.add(ChatColor.RESET + "" + ChatColor.GRAY + s);
				}
				item.setLore(list);
			}
			if (configitem.contains("Material")) {
				Material material;
				byte meta = 0;
				if (configitem.getString("Material").contains(":")) {
					material = Material.getMaterial(configitem.getString("Material").split(":")[0]);
					meta = Byte.parseByte(configitem.getString("Material").split(":")[1]);
				} else {
					material = Material.getMaterial(configitem.getString("Material"));
				}
				if (material == null) {
					String error = languageConfig.get().getString("Item.Load.InvalidMaterial");
					error = error.replace("{material}", configitem.getString("Material").split(":")[0]).replace("{item}", itemName);
					ProjectKorraItems.createError(error);
				} else {
					item.setMaterial(new MaterialData(material, meta));
				}
			}
			if (configitem.contains("Durability")) {
				item.setDurability(configitem.getInt("Durability"));
			}
			if (configitem.contains("Usage")) {
				Usage usage = PKItem.Usage.getUsage(configitem.getString("Usage"));
				if (usage == null) {
					String error = languageConfig.get().getString("Item.Load.InvalidMaterial");
					error = error.replace("{usage}", configitem.getString("Usage")).replace("{item}", itemName);
					
					ProjectKorraItems.createError(error);
				} else {
					item.setUsage(usage);
				}	
			}
			if (configitem.contains("Glow")) {
				item.setGlow(configitem.getBoolean("Glow"));
			}
			if (configitem.contains("Requirements")) {
				ConfigurationSection req = configitem.getConfigurationSection("Requirements");
				Requirements requirements = new Requirements();
				if (req.contains("World")) {
					requirements.setWorld(req.getString("World"));
				} else if (req.contains("Worlds")) {
					requirements.setWorlds(req.getStringList("Worlds"));
				}
				if (req.contains("Permission")) {
					requirements.setPermission(req.getString("Permission"));
				} else if (req.contains("Permissions")) {
					requirements.setPermissions(req.getStringList("Permissions"));
				}
				if (req.contains("Element")) {
					Element element = Element.getElement(req.getString("Element"));
					requirements.setElement(element);
				} else if (req.contains("Elements")) {
					HashSet<Element> set = new HashSet<Element>();
					
					for (String s : req.getStringList("Elements")) {
						Element element = Element.getElement(s);
						set.add(element);
					}
					requirements.setElements(set);
				}
				item.setRequirements(requirements);
			}
			
		}
	}


	/**
	 * Uses a string that represents the configuration file to parse through all
	 * of the items and create instances of CustomItems.
	 * 
	 * @param configStr a string version of the config.yml
	 * @param customItemNames a set containing the names of all the custom items
	 *            that were defined in the config.
	 */
	/*@Deprecated
	public void analyzeConfig(String configStr, Set<String> customItemNames) {
		String[] configLines = configStr.split("\n");
		PKItem newItem = null;
		boolean invalid = false;

		for (String line : configLines) {
			line = line.trim();
			if (line.length() == 0)
				continue;
			if (line.toLowerCase().startsWith(ITEM_PREF.toLowerCase())) {
				if (newItem != null && !invalid) {
					newItem.build();
				}
				invalid = false;
				newItem = new PKItem();
			} else {
				boolean prefFound = false;
				for (String prefix : PREFIXES) {
					if (line.toLowerCase().startsWith(prefix.toLowerCase())) {
						prefFound = true;
						String tmp = "";
						try {
							tmp = line.substring(prefix.length(), line.length());
							tmp = tmp.trim();
							if (prefix.equalsIgnoreCase(NAME_PREF))
								newItem.updateName(tmp);
							else if (prefix.equalsIgnoreCase(NAME_PREF))
								newItem.updateDisplayName(tmp);
							else if (prefix.equalsIgnoreCase(LORE_PREF))
								newItem.updateLore(tmp);
							else if (prefix.equalsIgnoreCase(SHAPED_RECIPE_PREF)) {
								newItem.updateRecipe(tmp, customItemNames);
								newItem.setUnshapedRecipe(false);
							} else if (prefix.equalsIgnoreCase(UNSHAPED_RECIPE_PREF)) {
								newItem.updateRecipe(tmp, customItemNames);
								newItem.setUnshapedRecipe(true);
							} else if (prefix.equalsIgnoreCase(MATERIAL_PREF))
								newItem.updateMaterial(tmp);
							else if (prefix.equalsIgnoreCase(DURA_PREF))
								newItem.updateDamage(tmp);
							else if (prefix.equalsIgnoreCase(AMT_PREF))
								newItem.updateQuantity(tmp);
							else if (prefix.equalsIgnoreCase(GLOW_PREF))
								newItem.updateGlow(tmp);
						}
						catch (Exception e) {
							ProjectKorraItems.log.info(Messages.BAD_PREFIX + ": " + prefix);
							invalid = false;
						}
					}
				}*/

				/* Check if it is an attribute */
				/*if (!prefFound) {
					try {
						String prefix = line.substring(0, line.indexOf(":"));
						String valueStr = line.substring(line.indexOf(":") + 1, line.length()).trim();
						valueStr = valueStr.replaceAll("(?i)true", "1");
						valueStr = valueStr.replaceAll("(?i)false", "0");
						String[] commaSplit = valueStr.split(",");
						if (commaSplit.length == 0) {
							ProjectKorraItems.log.info(Messages.MISSING_VALUES + ": " + line);
							invalid = false;
						}
						Attribute att = Attribute.getAttribute(prefix);
						Attribute newAtt = new Attribute(att.getName());
						newAtt.getValues().addAll(Arrays.asList(commaSplit));
						newItem.getAttributes().add(newAtt);
					}
					catch (Exception e) {
						ProjectKorraItems.log.info(Messages.BAD_PREFIX + ": " + line);
						invalid = false;
					}
				}

			}
		}
		if (newItem != null && !invalid) {
			newItem.build();
		}
	}*/

	/**
	 * Gathers the names of all of the custom items within the config file.
	 * 
	 * @param config a String representation of the configuration file
	 * @return a set containing the item names
	 */
	/*@Deprecated
	public Set<String> getConfigItemNames(String config) {
		HashSet<String> names = new HashSet<>();
		String[] lines = config.split("\n");
		String prefix = NAME_PREF;

		for (String line : lines) {
			line = line.trim();
			if (line.toLowerCase().startsWith(prefix.toLowerCase())) {
				String itemName = line.substring(prefix.length(), line.length()).trim();
				names.add(itemName);
			}
		}

		return names;
	}*/
}
