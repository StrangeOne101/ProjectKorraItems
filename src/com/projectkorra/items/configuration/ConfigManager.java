package com.projectkorra.items.configuration;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.PKItem.Usage;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.attribute.Attribute;
import com.projectkorra.items.attribute.AttributeModification;
import com.projectkorra.items.attribute.Requirements;
import com.projectkorra.items.attribute.old.AttributeOld;
import com.projectkorra.items.recipe.PKIShapelessRecipe;
import com.projectkorra.items.utils.GenericUtil;
import com.projectkorra.projectkorra.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

public class ConfigManager {

	public static Config itemsConfig;
	public static Config languageConfig;
	
	public ConfigManager() {
		
		File itemsFile = new File(ProjectKorraItems.plugin.getDataFolder(), "items.yml");
		File languageFile = new File(ProjectKorraItems.plugin.getDataFolder(), "language.yml");
		
		if (!itemsFile.exists()) {
			itemsFile.getParentFile().mkdirs();
			copy(ProjectKorraItems.plugin.getResource("items.yml"), itemsFile);
		}
		
		//We aren't copying the language file. That can be generated.
		
		itemsConfig = new Config(itemsFile);
		languageConfig = new Config(languageFile);
		
		//languageConfig.create();
		
		checkLanguageFile();
		loadItems();
		
	}

	public void checkLanguageFile() {
		FileConfiguration config = languageConfig.get();
		
		config.addDefault("Item.Load.Error", "Failed to load item {item}! Invalid {value}!");
		config.addDefault("Item.Load.DuplicateID", "Items {item1} and {item2} both have an ID of {id}! ");
		//config.addDefault("Item.Load.SurpassMaxItems", "You have defined too many items as the max you can define is 256! Any items defined after this will no longer be usable in game.");
		config.addDefault("Item.Load.InvalidMaterial", "Failed to load the material of {item}! \"{material}\" is not a valid material!");
		config.addDefault("Item.Load.InvalidUsage", "Failed to load the usage of {item}! \"{usage}\" is not one of HOLD, WEAR, CONSUMEABLE, CONTAIN");
		config.addDefault("Item.Load.InvalidAttribute", "Attribute {attribute} on item {item} not found. Skipping...");
		config.addDefault("Item.Load.InvalidRecipe.Shaped", "Failed to load the recipe of {item}! Must have the shape and the output quantity (optional)!");
		config.addDefault("Item.Load.InvalidRecipe.Shapeless", "Failed to load the recipe of {item}! Must have the ingredients and the output quantity (optional)!");
		config.addDefault("Item.Load.InvalidRecipe.ShapedRecipeError", "Failed to load the recipe of {item}! Could not parse the recipe line!");
		config.addDefault("Item.Load.InvalidRecipe.ShapelessRecipeError", "Failed to load the recipe of {item}! Could not parse the ingredients line!");
		config.addDefault("Item.Load.InvalidRecipe.InvalidMaterial", "Failed to load the recipe of {item}! \"{material}\" is not a valid material!");
		
		config.addDefault("Item.Use.DuplicateID", "This item cannot be used because it has a duplicate ID! Please report this to your admin!");
		config.addDefault("Item.Use.Break", "Your {item} has broken!");
		config.addDefault("Item.Use.Anvil", "You can't place this item in an anvil!");
		config.addDefault("Item.Use.Enchant", "You can't enchant this item in an enchantment table!");
		config.addDefault("Item.Use.Place", "You can't place this item!");
		config.addDefault("Item.Use.Deny.Permission", "You don't have permission to use this item!");
		config.addDefault("Item.Use.Deny.World", "You can't use this item in the current world!");
		config.addDefault("Item.Use.Deny.Element", "You must have {element} to use this item!");
		config.addDefault("Item.Use.Deny.None", "You meet the requirements to use this item!");
		
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
		config.addDefault("Item.Lore.Owned", "Owner: {owner}");
		
		languageConfig.save();
	}
	
	@SuppressWarnings("deprecation")
	public void loadItems() {
		FileConfiguration config = itemsConfig.get();
		Random random = new Random();
		
		boolean overMax = false;
		
		PKItem.INSTANCE_MAP.clear();
	
		int max = (int) Math.pow(Short.SIZE, 2); //The max number of possibilities of shorts
		
		for (String itemName : config.getKeys(false)) {
			if (PKItem.INSTANCE_MAP.size() >= max) {
				if (!overMax) {
					ProjectKorraItems.createError(languageConfig.get().getString("Item.Load.SurpassMaxItems"));
					overMax = true;
				}
				PKItem.DISABLED.add(itemName);
			}
			
			short id;
			
			ConfigurationSection configitem = config.getConfigurationSection(itemName);

			if (config.contains(itemName + ".SecretID")) {
				id = GenericUtil.convertUnsignedShort(config.getInt(itemName + ".SecretID"));
			} else {
				id = (short) random.nextInt(); //Make it another random value
				
				while (PKItem.INSTANCE_MAP.containsKey(id)) { //Easier to ++ then to make a new random num. And its faster
					id++;
				}
			}
			
			PKItem item = new PKItem(itemName, id);
			
			if (configitem.contains("Name")) {
				item.setDisplayName(configitem.getString("Name"));
			}
			if (configitem.contains("Lore")) {
				List<String> list = new ArrayList<String>();
				
				if (configitem.get("Lore") instanceof List) {
					for (String s : configitem.getStringList("Lore")) {
						list.add(ChatColor.RESET + "" + ChatColor.GRAY + s);
					}
				} else {
					list.add(ChatColor.RESET + "" + ChatColor.GRAY + configitem.getString("Lore"));
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
				item.setMaxDurability((short) configitem.getInt("Durability"));
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
			if (configitem.contains("OwnerLocked")) {
				item.setPlayerLocked(configitem.getBoolean("OwnerLocked"));
			}
			if (configitem.contains("PlayerLocked")) {
				item.setPlayerLocked(configitem.getBoolean("PlayerLocked"));
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
			if (configitem.contains("Attributes")) {
				ConfigurationSection attributeList = configitem.getConfigurationSection("Attributes");
				
				for (String key : attributeList.getKeys(false)) {
					if (Attribute.isAttribute(key)) {
						item.addAttribute(Attribute.getAttribute(key), new AttributeModification(Attribute.getAttribute(key), attributeList.getString(key), item));
					} else {
						ProjectKorraItems.log.warning(languageConfig.get().getString("Item.Load.InvalidAttribute").replace("{attribute}", key).replace("{item}", itemName));
					}
				}
			}
			if (configitem.contains("ShapedRecipe")) {
				if (configitem.contains("ShapedRecipe.Recipe")) {
					//Map of all the ingredients and their placeholders (we are using numbers for simplicity)
					Map<MaterialData, Integer> ingredients = new HashMap<MaterialData, Integer>();
					List<String> recipe = new ArrayList<String>();
					for (String line : configitem.getStringList("ShapedRecipe.Recipe")) {
						
						String[] materials = null;
						recipe.add(""); //Adds a new line to the recipe
						
						//Split the individual materials with commas and spaces
						if (line.contains(" ") && line.contains(",")) materials = line.replaceAll(",", "").split(" ");
						else if (line.contains(" ")) materials = line.split(" ");
						else if (line.contains(",")) materials = line.split(",");
						else ProjectKorraItems.createError(languageConfig.get().getString("Item.Load.InvalidRecipe.ShapedRecipeError").replace("{item}", itemName));
						
						//If it equals null there's already been an error created ^
						if (materials != null) {
							for (String mat : materials) { //Loop through every material on the line
								
								MaterialData materialdata = null;
								
								if (mat.contains(":")) { //If it contains a : it probably has metadata
									materialdata = new MaterialData(Material.valueOf(mat.split(":")[0]), Byte.valueOf(mat.split(":")[1]));
								} else materialdata = new MaterialData(Material.valueOf(mat));
								
								if (materialdata.getItemType() == null) {
									ProjectKorraItems.createError(languageConfig.get().getString("Item.Load.InvalidRecipe.InvalidMaterial")
											.replace("{item}", itemName).replace("{material}", mat));
								} else {
									//If we've already used the material before, resuse the placeholder
									if (ingredients.containsKey(materialdata)) {
										//Appends the placeholder to the recipe for the line we are currently on
										recipe.set(recipe.size() - 1, recipe.get(recipe.size() - 1) + ingredients.get(materialdata));
									} else {
										//If not, use the next avaliable number as a placeholder
										int newplaceholder = ingredients.values().size() + 1;
										recipe.set(recipe.size() - 1, recipe.get(recipe.size() - 1) + "" + newplaceholder);
										ingredients.put(materialdata, newplaceholder);
									}
									
								}
							}
						}
					}
					
					ItemStack recipeItem = item.buildItem();
					if (configitem.contains("ShapedRecipe.Amount")) {
						recipeItem.setAmount(configitem.getInt("ShapedRecipe.Amount"));
					}
					
					ShapedRecipe finalrecipe = new ShapedRecipe(recipeItem);
					
					String[] recipeArray = new String[recipe.size()];
					for (int i = 0; i < recipe.size(); i++) recipeArray[i] = recipe.get(i);
					
					finalrecipe.shape(recipeArray);
					for (MaterialData material : ingredients.keySet()) {
						finalrecipe.setIngredient(String.valueOf(ingredients.get(material)).charAt(0), material);
					}
					Bukkit.addRecipe(finalrecipe);
					
				} else {
					ProjectKorraItems.createError(languageConfig.get().getString("Item.Load.InvalidRecipe.Shaped").replace("{item}", itemName));
				}
			}
			else if (configitem.contains("ShapelessRecipe")) {
				if (configitem.contains("ShapelessRecipe.Ingredients")) {
					String ingredients = configitem.getString("ShapelessRecipe.Ingredients");
					String[] ingredientsArray = null;
					
					//Split the individual materials with commas and spaces
					if (ingredients.contains(" ") && ingredients.contains(",")) ingredientsArray = ingredients.replaceAll(",", "").split(" ");
					else if (ingredients.contains(",")) ingredientsArray = ingredients.split(",");
					else if (ingredients.contains(" ")) ingredientsArray = ingredients.split(" ");
					else ProjectKorraItems.createError(languageConfig.get().getString("Item.Load.InvalidRecipe.ShapelessRecipeError").replace("{item}", itemName));
					
					ItemStack recipeItem = item.buildItem();
					if (configitem.contains("ShapelessRecipe.Amount")) {
						recipeItem.setAmount(configitem.getInt("ShapelessRecipe.Amount", 1));
					}
					
					ShapelessRecipe recipe = new PKIShapelessRecipe(item, configitem.getInt("ShapelessRecipe.Amount", 1));
					
					for (String ingredient : ingredientsArray) {
						MaterialData materialdata = null;
						
						if (ingredient.contains(":")) { //If it contains a : it probably has metadata
							materialdata = new MaterialData(Material.valueOf(ingredient.split(":")[0]), Byte.valueOf(ingredient.split(":")[1]));
						} else materialdata = new MaterialData(Material.valueOf(ingredient));
						
						recipe.addIngredient(materialdata);
					}		
					
					Bukkit.addRecipe(recipe);
				} else {
					ProjectKorraItems.createError(languageConfig.get().getString("Item.Load.InvalidRecipe.Shapeless").replace("{item}", itemName));
				}
			}
			
			ProjectKorraItems.log.info(item.getName() + " registered with ID " + GenericUtil.convertSignedShort(item.getID()));
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
	
	private void copy(InputStream in, File file) {
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
