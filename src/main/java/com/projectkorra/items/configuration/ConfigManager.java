package com.projectkorra.items.configuration;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.PKItem.Usage;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.attribute.Attribute;
import com.projectkorra.items.attribute.AttributeModification;
import com.projectkorra.items.attribute.Requirements;

import com.projectkorra.projectkorra.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitemsapi.CustomItemRegistry;
import com.strangeone101.holoitemsapi.HoloItemsAPI;
import com.strangeone101.holoitemsapi.recipe.CIRecipeChoice;
import com.strangeone101.holoitemsapi.recipe.RecipeManager;
import commonslang3.projectkorra.lang3.tuple.Pair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import javax.swing.filechooser.FileNameExtensionFilter;

public class ConfigManager {

	@Deprecated
	public static Config itemsConfig;
	public static Config languageConfig;
	
	public ConfigManager() {

		File languageFile = new File(ProjectKorraItems.plugin.getDataFolder(), "language.yml");
		



		
		//We aren't copying the language file. That can be generated.
		languageConfig = new Config(languageFile);
		
		//languageConfig.create();
		
		checkLanguageFile();
		loadItems();
		
	}

	public void checkLanguageFile() {
		FileConfiguration config = languageConfig.get();
		
		config.addDefault("Load.Error", "Failed to load item \"{item}\"! Invalid {value}!");
		config.addDefault("Load.DuplicateID", "Two items are registered with ID \"{item}\"! Found in {file1} and {file2}");
		//config.addDefault("Item.Load.SurpassMaxItems", "You have defined too many items as the max you can define is 256! Any items defined after this will no longer be usable in game.");
		config.addDefault("Load.InvalidMaterial", "Failed to load the material of {item}! \"{material}\" is not a valid material!");
		config.addDefault("Load.InvalidUsage", "Failed to load the usage of {item}! \"{usage}\" is not one of HOLD, WEAR, CONSUMABLE, CONTAIN");
		config.addDefault("Load.InvalidAttribute", "Attribute {attribute} on item \"{item}\" not found. Skipping...");
		config.addDefault("Load.InvalidEnchant", "Could not find enchantment \"{enchantment}\" in item {item}! (File {file})");
		config.addDefault("Load.Recipe.InvalidShape", "Failed to load the recipe of {item}! Must have a recipe made of ingredients and an output quantity (optional)!");
		config.addDefault("Load.Recipe.NoShape", "Failed to load the recipe of {item}! A shape must be provided!");
		config.addDefault("Load.Recipe.InvalidShapeless", "Failed to load the recipe of {item}! Must have the ingredients and the output quantity (optional)!");
		config.addDefault("Load.Recipe.InvalidItem", "Failed to load the recipe of {item}! Could not find the PK Item {recipeitem}!");
		config.addDefault("Load.Recipe.InvalidTag", "Failed to load the recipe of {item}! Could not parse the tag {tag}");
		config.addDefault("Load.Recipe.InvalidMaterial", "Failed to load the recipe of {item}! \"{material}\" is not a valid material!");
		config.addDefault("Load.Recipe.InvalidFurnace", "Failed to load the recipe of {item}! Must have an \"Item\" tag for the item to output!");

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

		config.addDefault("Loot.Item.NoName", "Invalid loottable in file {file}! No item name provided in section \"{section}\"!");
		config.addDefault("Loot.Item.InvalidItem", "Invalid loottable in file {file}! No PKItem with name {item}!");
		config.addDefault("Loot.InvalidTable", "Could not find minecraft loot table {loottable}!");
		config.addDefault("Loot.InvalidFile", "File {file} is not a valid name for a loot table!");


		config.addDefault("Lore.Durability", "&7Remaining Uses: {durabilitycolor}{durability}&7/{maxdurability}");
		config.addDefault("Lore.Usage.Consumable", "Right click while holding to use!");
		config.addDefault("Lore.Usage.Wearable", "Must be worn for the effects to show!");
		config.addDefault("Lore.Usage.Hold", "Active while holding!");
		config.addDefault("Lore.Usage.Inventory", "");
		config.addDefault("Lore.SelfOwned", "&eYou own this item!");
		config.addDefault("Lore.PlayerOwned", "&7Can only be used by &c{owner}&7!");

		languageConfig.save();
	}

	public void loadItems() {
		File itemFolder = new File(ProjectKorraItems.plugin.getDataFolder(), "items");
		if (!itemFolder.exists()) {
			itemFolder.mkdir();
			ProjectKorraItems.log.info("No items to load! If you wish to generate the default ones, please delete your config.yml and restart the server!");
			return;
		}

		Map<CustomItem, File> tempFileLocations = new HashMap<>();
		Set<Triple<PKItem, String, ConfigurationSection>> recipesToDoLater = new HashSet<>();

		int registered = 0;
		int files = 0;

		for (File file : FileUtils.listFiles(itemFolder, new String[] {"yml"}, true)) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

			String pathName = file.getAbsolutePath().substring(itemFolder.getAbsolutePath().length() + 1);

			itemloop:
			for (String itemName : config.getKeys(false)) {
				ConfigurationSection configitem = config.getConfigurationSection(itemName);

				if (CustomItemRegistry.getCustomItem(itemName) != null) {
					String error = languageConfig.get().getString("Load.DuplicateID");
					error = error.replace("{item}", itemName).replace("{file1}", pathName)
							.replace("{file2}", tempFileLocations.get(CustomItemRegistry.getCustomItem(itemName)).getName());
					ProjectKorraItems.createError(error);
					continue itemloop;
				}

				String material = configitem.getString("Material", "Stick").toUpperCase(Locale.ROOT);
				String displayName = ChatColor.translateAlternateColorCodes('&', configitem.getString("Name"));
				List<String> lore = new ArrayList<>();

				if (configitem.contains("Lore")) {
					if (configitem.get("Lore") instanceof List) {
						configitem.getStringList("Lore").forEach(s -> lore.add(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', s)));
					} else {
						lore.add(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', configitem.getString("Lore")));
					}
				}

				if (Material.getMaterial(material) == null) {
					String error = languageConfig.get().getString("Load.InvalidMaterial");
					error = error.replace("{material}", material)
							.replace("{item}", itemName).replace("{file}", pathName);
					ProjectKorraItems.createError(error);
					continue itemloop;
				}

				PKItem item = (PKItem) new PKItem(itemName, Material.getMaterial(material))
						.setDisplayName(displayName).setLore(lore);


				if (configitem.contains("Durability")) {
					item.setMaxDurability((short) configitem.getInt("Durability"));
				}
				if (configitem.contains("Usage")) {
					Usage usage = PKItem.Usage.getUsage(configitem.getString("Usage"));
					if (usage == null) {
						String error = languageConfig.get().getString("Load.InvalidUsage");
						error = error.replace("{usage}", configitem.getString("Usage"))
								.replace("{item}", itemName).replace("{file}", pathName);

						ProjectKorraItems.createError(error);
					} else {
						item.setUsage(usage);
					}
				}
				if (configitem.contains("Glow")) {
					item.setEnchantedGlow(configitem.getBoolean("Glow"));
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
							item.addPKIAttribute(Attribute.getAttribute(key), new AttributeModification(Attribute.getAttribute(key), attributeList.getString(key), item));
						} else {
							ProjectKorraItems.log.warning(languageConfig.get().getString("Load.InvalidAttribute")
									.replace("{attribute}", key).replace("{item}", itemName).replace("{file}", pathName));
						}
					}
				}
				if (configitem.contains("Color")) {
					Material mat = Material.getMaterial(material);
					if (mat == Material.LEATHER_BOOTS || mat == Material.LEATHER_LEGGINGS || mat == Material.LEATHER_CHESTPLATE
							|| mat == Material.LEATHER_HELMET) {
						Object o = configitem.get("Color");

						try {
							if (o instanceof Number) item.setLeatherColor((int)o);
							else if (o instanceof String) {
								String s = (String) o;
								if (s.startsWith("#")) item.setLeatherColor(Integer.parseInt(s.substring(1), 16));
								else if (s.startsWith("0x")) item.setLeatherColor(Integer.parseInt(s.substring(2), 16));
								else item.setLeatherColor(Integer.parseInt(s, 16));
							}
						} catch (NumberFormatException e) {
							String error = languageConfig.get().getString("Load.InvalidColor");
							error = error.replace("{color}", configitem.getString("Color"))
									.replace("{item}", itemName).replace("{file}", pathName);

							ProjectKorraItems.createError(error);
						}
					}
				}
				if (configitem.contains("Enchantments")) {
					for (String key : configitem.getConfigurationSection("Enchantments").getKeys(false)) {
						Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(key));
						if (enchant == null) {
							enchant = Enchantment.getByName(key);

							if (enchant == null) {
								String error = languageConfig.get().getString("Load.InvalidEnchant");
								error = error.replace("{enchantment}", key).replace("{item}", itemName)
										.replace("{file}", pathName);

								ProjectKorraItems.createError(error);
								continue itemloop;
							}
						}

						int level = configitem.getInt("Enchantments." + key);
						item.addEnchantment(enchant, level);
					}
				}
				if (configitem.contains("HideEnchants") || configitem.contains("HideEnchantments")) {
					ItemFlag[] flags = new ItemFlag[item.getFlags().length + 1];
					System.arraycopy(item.getFlags(), 0, flags, 0, item.getFlags().length);
					flags[flags.length - 1] = ItemFlag.HIDE_ENCHANTS;

					item.setFlags(flags);
				}
				if (configitem.contains("HideDamage") || configitem.contains("HideArmor")) {
					ItemFlag[] flags = new ItemFlag[item.getFlags().length + 1];
					System.arraycopy(item.getFlags(), 0, flags, 0, item.getFlags().length);
					flags[flags.length - 1] = ItemFlag.HIDE_ATTRIBUTES;

					item.setFlags(flags);
				}
				if (configitem.contains("HideOther") || configitem.contains("HidePotions")) {
					ItemFlag[] flags = new ItemFlag[item.getFlags().length + 1];
					System.arraycopy(item.getFlags(), 0, flags, 0, item.getFlags().length);
					flags[flags.length - 1] = ItemFlag.HIDE_POTION_EFFECTS;

					item.setFlags(flags);
				}
				if (configitem.contains("HideDye") || configitem.contains("HideColor")) {
					ItemFlag[] flags = new ItemFlag[item.getFlags().length + 1];
					System.arraycopy(item.getFlags(), 0, flags, 0, item.getFlags().length);
					flags[flags.length - 1] = ItemFlag.HIDE_DYE;

					item.setFlags(flags);
				}
				if (configitem.contains("Damage")) {
					item.setDamage(configitem.getDouble("Damage"));
				}
				if (configitem.contains("AttackSpeed")) {
					item.setAttackSpeed(configitem.getDouble("AttackSpeed"));
				}
				if (configitem.contains("Armor")) {
					item.setArmor(configitem.getDouble("Armor"));
				}
				if (configitem.contains("ArmorToughness")) {
					item.setArmorToughness(configitem.getDouble("ArmorToughness"));
				}
				if (configitem.contains("CustomModel")) {
					item.setCustomModel(configitem.getInt("CustomModel"));
				}
				if (configitem.contains("CustomModelID")) {
					item.setCustomModel(configitem.getInt("CustomModelID"));
				}
				if (configitem.contains("Unstackable")) {
					item.setStackable(!configitem.getBoolean("Unstackable"));
				}
				if (configitem.contains("SkullOwner")) {
					item.setHeadSkin(configitem.getString("SkullOwner"));
				}
				if (configitem.contains("Texture")) {
					item.setHeadSkin(configitem.getString("Texture"));
				}
				if (configitem.contains("NBT")) {
					ConfigurationSection nbtSection = configitem.getConfigurationSection("NBT");
					for (String s : nbtSection.getKeys(true)) {
						item.addNBT(s, nbtSection.get(s));
					}
				}

				item.register();
				tempFileLocations.put(item, file);
				registered++;

				ProjectKorraItems.log.info(item.getInternalName() + " registered");

				if (configitem.contains("ShapedRecipe") || configitem.contains("ShapelessRecipe")
						|| configitem.contains("FurnaceRecipe")) {
					recipesToDoLater.add(new ImmutableTriple<>(item, pathName, configitem));
				}
			}

			files++;
		}
		
		if (registered > 0) {
			ProjectKorraItems.log.info("Registered " + registered + " custom items in " + files + " different files!");
		}

		registerRecipes(recipesToDoLater);
	}

	public void registerRecipes(Set<Triple<PKItem, String, ConfigurationSection>> items) {

		int registered = 0;

		itemloop:
		for (Triple<PKItem, String, ConfigurationSection> pair : items) {
			ConfigurationSection section = pair.getRight();

			String filename = pair.getMiddle();
			PKItem item = pair.getLeft();

			if (section.contains("ShapedRecipe")) {
				if (section.contains("ShapedRecipe.Recipe")) {
					//List of all ingredients used in the current recipe
					List<RecipeChoice> ingredientIndex = new ArrayList<>();

					//A list of the compiled shape lines
					List<String> compiledShapeLines = new ArrayList<>();

					List<String> configLines = section.getStringList("ShapedRecipe.Recipe");
					for (String line : configLines) {

						String[] materials = null;

						String ingredientsLine = "";

						//Split the individual materials with commas and spaces
						if (line.contains(" ") && line.contains(",")) materials = line.replaceAll(" ", "").split(",");
						else if (line.contains(" ")) materials = line.split(" ");
						else if (line.contains(",")) materials = line.split(",");
						else materials = new String[] {line.trim()};

						if (materials.length > 3) {
							ProjectKorraItems.createError(languageConfig.get().getString("Load.Recipe.InvalidShape")
									.replace("{item}", item.getInternalName()).replace("{file}", filename));
							continue itemloop;
						}

						//If it equals null there's already been an error created ^
						if (materials != null) {
							for (int i = 0; i < materials.length; i++) {
								//Loop through every material on the line
								String mat = materials[i];

								//Load the ingredient. Can be material, tag or custom item
								RecipeChoice ingredient = getIngredient(mat, (PKItem) item, filename);
								if (ingredient == null) continue itemloop; //An error occurred,

								int index = ingredientIndex.size();
								for (RecipeChoice rc : ingredientIndex) { //Loop through all existing ingredients and find it
									if ((rc == null && ingredient == null) || rc.equals(ingredient)) { //Both are air OR they match
										index = ingredientIndex.indexOf(rc);
										break;
									}
								}

								//If it doesn't exist, add it to the list
								if (index >= ingredientIndex.size()) {
									ingredientIndex.add(ingredient);
								}

								if (ingredientIndex.get(index) == null) {
									ingredientsLine = ingredientsLine + " "; //Add air
								} else {
									ingredientsLine = ingredientsLine + "" + index; //Add a character to represent the ingredient
								}
							}

							compiledShapeLines.add(ingredientsLine);
						}
					}

					ItemStack stack = item.buildStack(null);
					stack.setAmount(section.getInt("ShapedRecipe.Amount", 1));
					ShapedRecipe compiledRecipe = new ShapedRecipe(new NamespacedKey(ProjectKorraItems.plugin, item.getInternalName()), stack);

					String[] shapeArray = new String[compiledShapeLines.size()];
					for (int i = 0; i < compiledShapeLines.size(); i++) shapeArray[i] = compiledShapeLines.get(i);

					compiledRecipe.shape(shapeArray);
					for (int i = 0; i < ingredientIndex.size(); i++) { //Loop through all ingredients
						RecipeChoice ingredient = ingredientIndex.get(i);
						if (ingredient != null) { //Null is for air
							compiledRecipe.setIngredient(Character.forDigit(i, 10), ingredient);
						}
					}
					RecipeManager.registerRecipe(compiledRecipe);
					RecipeManager.addToRecipeBookAuto(compiledRecipe);

				} else {
					ProjectKorraItems.createError(languageConfig.get().getString("Load.Recipe.NoShape")
							.replace("{item}", item.getInternalName()).replace("{file}", filename));
				}
			} else if (section.contains("ShapelessRecipe")) {
				if (section.contains("ShapelessRecipe.Ingredients")) {
					List<RecipeChoice> ingredients = new ArrayList<>();
					List<String> ingredientsList;
					if (section.get("ShapelessRecipe.Ingredients") instanceof List) {
						ingredientsList = section.getStringList("ShapelessRecipe.Ingredients");
					} else {
						String ingredientsString = section.getString("ShapelessRecipe.Ingredients").trim();
						String[] ingredientsArray = null;

						//Split the individual materials with commas and spaces
						if (ingredientsString.contains(" ") && ingredientsString.contains(",")) ingredientsArray = ingredientsString.replaceAll(" ", "").split(",");
						else if (ingredientsString.contains(",")) ingredientsArray = ingredientsString.split(",");
						else if (ingredientsString.contains(" ")) ingredientsArray = ingredientsString.split(" ");
						else {
							ProjectKorraItems.createError(languageConfig.get().getString("Load.Recipe.InvalidShapeless")
									.replace("{item}", item.getInternalName()).replace("{file}", filename));
							continue itemloop;
						}

						ingredientsList = List.of(ingredientsArray);
					}

					for (String s : ingredientsList) {
						RecipeChoice ingr = getIngredient(s, item, filename);
						if (ingr == null) continue itemloop; //An error occurred, so skip the recipe for now
					}

					ItemStack stack = item.buildStack(null);
					stack.setAmount(section.getInt("ShapelessRecipe.Amount", 1));
					ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(ProjectKorraItems.plugin, item.getInternalName()), stack);

					ingredients.forEach(recipe::addIngredient);

					RecipeManager.registerRecipe(recipe);
					RecipeManager.addToRecipeBookAuto(recipe);
				} else {
					ProjectKorraItems.createError(languageConfig.get().getString("Load.Recipe.InvalidShapeless")
							.replace("{item}", item.getInternalName()).replace("{file}", filename));
				}
			} else if (section.contains("FurnaceRecipe")) {
				int amount = section.getInt("FurnaceRecipe.Amount", 1);
				String ingredient = section.getString("FurnaceRecipe.Item");
				if (ingredient == null) {
					ProjectKorraItems.createError(languageConfig.get().getString("Load.Recipe.InvalidFurnace")
							.replace("{item}", item.getInternalName()).replace("{file}", filename));
					continue itemloop;
				}
				float experience = (float)section.getDouble("FurnaceRecipe.Experience", 0.1D);
				int burntime = section.getInt("FurnaceRecipe.CookTime", 200);
				RecipeChoice recipeIngredient = getIngredient(ingredient, item, filename);
				if (recipeIngredient != null) { //If an error didn't occur
					NamespacedKey key = new NamespacedKey(ProjectKorraItems.plugin, item.getInternalName());
					ItemStack stack = item.buildStack(null);
					stack.setAmount(amount);
					FurnaceRecipe recipe = new FurnaceRecipe(key, stack, recipeIngredient, experience, burntime);

					RecipeManager.registerRecipe(recipe);
					RecipeManager.addToRecipeBookAuto(recipe);
				}
			}
			registered++;
		}

		if (registered > 0) {
			ProjectKorraItems.log.info("Registered "+ registered + " custom recipes for items.");
		}
	}

	public static RecipeChoice getIngredient(String ingredientString, PKItem item, String file) {
		RecipeChoice ingredient;

		if (ingredientString.startsWith("#")) { //Tags
			String prefix = "minecraft";
			String suffix = ingredientString.substring(1).toLowerCase(Locale.ROOT);
			if (ingredientString.contains(":")) {
				prefix = ingredientString.substring(1).split(":")[0].toLowerCase(Locale.ROOT);
				suffix = ingredientString.substring(1).split(":")[1].toLowerCase(Locale.ROOT);
			}
			NamespacedKey key = new NamespacedKey(prefix, suffix);
			Tag<Material> tag = Bukkit.getTag("items", key, Material.class);
			if (tag == null) {
				ProjectKorraItems.createError(languageConfig.get().getString("Load.Recipe.InvalidTag")
						.replace("{item}", item.getInternalName()).replace("{tag}", ingredientString)
						.replace("{file}", file));
				return null;
			}
			ingredient = new RecipeChoice.MaterialChoice(tag);
		} else if (ingredientString.startsWith("@")) { //Custom item
			String name = ingredientString.substring(1);

			if (CustomItemRegistry.getCustomItem(name) == null) {
				ProjectKorraItems.createError(languageConfig.get().getString("Load.Recipe.InvalidItem")
						.replace("{item}", item.getInternalName()).replace("{recipeitem}", name)
						.replace("{file}", file));
				return null;
			}

			ingredient = new CIRecipeChoice(CustomItemRegistry.getCustomItem(name).buildStack(null));
		} else if (ingredientString.equals("") || ingredientString.equalsIgnoreCase("air")) { //Air
			ingredient = null;
		} else { //Materials
			Material material = Material.getMaterial(ingredientString.toUpperCase(Locale.ROOT));

			if (material == null) {
				String error = languageConfig.get().getString("Load.Recipe.InvalidMaterial");
				error = error.replace("{material}", ingredientString).replace("{item}", item.getInternalName()
						.replace("{file}", file));
				ProjectKorraItems.createError(error);
				return null;
			}

			ingredient = new RecipeChoice.MaterialChoice(material);
		}

		return ingredient;
	}
}
