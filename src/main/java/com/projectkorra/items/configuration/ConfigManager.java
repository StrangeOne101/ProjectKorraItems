package com.projectkorra.items.configuration;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.PKItem.Usage;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.attribute.AttributeBuilder;
import com.projectkorra.items.attribute.AttributeModification;
import com.projectkorra.items.attribute.AttributeTarget;
import com.projectkorra.items.attribute.AttributeType;
import com.projectkorra.items.attribute.Requirements;

import com.projectkorra.items.event.PKItemLoadEvent;
import com.projectkorra.items.recipe.RecipeParser;
import com.projectkorra.projectkorra.Element;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.Set;
import java.util.stream.Collectors;

import com.projectkorra.items.api.CustomItem;
import com.projectkorra.items.api.CustomItemRegistry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.Recipe;

public class ConfigManager {

	public static Config languageConfig;
	public static Config genericConfig;

	private static Map<Recipe, RecipeParser> registered = new HashMap<>();
	
	public ConfigManager() {

		File languageFile = new File(ProjectKorraItems.plugin.getDataFolder(), "language.yml");
		File genericFile = new File(ProjectKorraItems.plugin.getDataFolder(), "config.yml");
		
		//We aren't copying the language file. That can be generated.
		languageConfig = new Config(languageFile);
		genericConfig = new Config(genericFile);
		registered.clear();
		
		checkLanguageFile();
		loadItems();

	}

	public void checkGenericFile() {
		//Get the FileConfiguration from genericConfig
		//Add default values
		//Save the FileConfiguration

		FileConfiguration config = genericConfig.get();

		config.addDefault("Loot.Enabled", true);

		genericConfig.save();
	}

	public void checkLanguageFile() {
		FileConfiguration config = languageConfig.get();
		
		config.addDefault("Load.Error", "Failed to load item \"{item}\"! Invalid {value}!");
		config.addDefault("Load.DuplicateID", "Two items are registered with ID \"{item}\"! Found in {file1} and {file2}");
		//config.addDefault("Item.Load.SurpassMaxItems", "You have defined too many items as the max you can define is 256! Any items defined after this will no longer be usable in game.");
		config.addDefault("Load.InvalidMaterial", "Failed to load the material of {item}! \"{material}\" is not a valid material!");
		config.addDefault("Load.InvalidUsage", "Failed to load the usage of {item}! \"{usage}\" is not one of HOLD, WEAR, CONSUMABLE, CONTAIN");
		config.addDefault("Load.InvalidAttributeTrait", "Attribute trait {trait} on item \"{item}\" not found. Skipping...");
		config.addDefault("Load.InvalidAttributeTarget", "Attribute target {target} on item \"{item}\" not found. Skipping...");
		config.addDefault("Load.InvalidEnchant", "Could not find enchantment \"{enchantment}\" in item {item}! (File {file})");
		config.addDefault("Load.Recipe.InvalidShape", "Failed to load the recipe of {item}! Must have a recipe made of ingredients and an output quantity (optional)!");
		config.addDefault("Load.Recipe.NoShape", "Failed to load the recipe of {item}! A shape must be provided!");
		config.addDefault("Load.Recipe.InvalidShapeless", "Failed to load the recipe of {item}! Must have the ingredients and the output quantity (optional)!");
		config.addDefault("Load.Recipe.InvalidItem", "Failed to load the recipe of {item}! Could not find the PK Item {recipeitem}!");
		config.addDefault("Load.Recipe.InvalidTag", "Failed to load the recipe of {item}! Could not parse the tag {tag}");
		config.addDefault("Load.Recipe.InvalidMaterial", "Failed to load the recipe of {item}! \"{material}\" is not a valid material!");
		config.addDefault("Load.Recipe.InvalidFurnace", "Failed to load the recipe of {item}! Must have an \"Item\" tag for the item to output!");
		config.addDefault("Load.Recipe.InvalidSmithing", "Failed to load the recipe of {item}! Must have both a \"Primary\" and \"Secondary\" tag for item inputs!");

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

		config.addDefault("Command.NoPermission", "&cYou don't have permission for this command!");

		languageConfig.save();
	}

	public void loadItems() {
		File itemFolder = new File(ProjectKorraItems.plugin.getDataFolder(), "items");
		if (!itemFolder.exists()) {
			itemFolder.mkdir();
			ProjectKorraItems.log.info("No items to load! If you wish to generate the default ones, please delete your config.yml and restart the server!");
			return;
		}

		Set<Pair<PKItem, ConfigurationSection>> recipesToDoLater = new HashSet<>();

		int registered = 0;
		int files = 0;

		try {
			for (Path path : Files.find(itemFolder.toPath(), 15, (p, bfa) -> bfa.isRegularFile())
					.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".yml"))
					.collect(Collectors.toList())) {
				File file = path.toFile();

				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

				String pathName = file.getAbsolutePath().substring(itemFolder.getAbsolutePath().length() + 1);

				itemloop:
				for (String itemName : config.getKeys(false)) {
					ConfigurationSection configitem = config.getConfigurationSection(itemName);

					if (CustomItemRegistry.getCustomItem(itemName) != null) {
						String error = languageConfig.get().getString("Load.DuplicateID");
						error = error.replace("{item}", itemName).replace("{file1}", pathName)
								.replace("{file2}", ((PKItem)CustomItemRegistry.getCustomItem(itemName)).getFileLocation());
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
							.setFileLocation(pathName).setDisplayName(displayName).setLore(lore);


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

						for (String targetKey : attributeList.getKeys(false)) {
							if (!AttributeBuilder.targets.containsKey(targetKey.toLowerCase())) {
								ProjectKorraItems.log.warning(languageConfig.get().getString("Load.InvalidAttributeTarget")
										.replace("{target}", targetKey).replace("{item}", itemName).replace("{file}", pathName));
								continue;
							}

							for (String traitKey : attributeList.getConfigurationSection(targetKey).getKeys(false)) {
								if (!AttributeBuilder.types.containsKey(traitKey.toLowerCase())) {
									ProjectKorraItems.log.warning(languageConfig.get().getString("Load.InvalidAttributeTrait")
											.replace("{trait}", targetKey).replace("{item}", itemName).replace("{file}", pathName));
									continue;
								}

								AttributeTarget target = AttributeBuilder.targets.get(targetKey.toLowerCase());
								AttributeType trait = AttributeBuilder.types.get(traitKey.toLowerCase());
								String mod = attributeList.getString(targetKey + "." + traitKey);

								item.addPKIAttribute(new AttributeModification(target, trait, mod, item));
							}
						}
					}
					if (configitem.contains("Color")) {
						Material mat = Material.getMaterial(material);
						if (mat == Material.LEATHER_BOOTS || mat == Material.LEATHER_LEGGINGS || mat == Material.LEATHER_CHESTPLATE
								|| mat == Material.LEATHER_HELMET) {
							Object o = configitem.get("Color");

							try {
								if (o instanceof Number) item.setLeatherColor((int) o);
								else if (o instanceof String) {
									String s = (String) o;
									if (s.startsWith("#")) item.setLeatherColor(Integer.parseInt(s.substring(1), 16));
									else if (s.startsWith("0x"))
										item.setLeatherColor(Integer.parseInt(s.substring(2), 16));
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

					PKItemLoadEvent event = new PKItemLoadEvent(item, configitem, file);
					if (!event.isCancelled()) {
						item = event.getItem(); //In case they override the item to load
						item.register(); //Register the item
						registered++;

						//ProjectKorraItems.log.info(item.getInternalName() + " registered");

						if (RecipeParser.hasRecipe(configitem)) {
							recipesToDoLater.add(new ImmutablePair<>(item, configitem));
						}
					}
				}

				files++;
			}

			if (registered > 0) {
				ProjectKorraItems.log.info("Registered " + registered + " custom items in " + files + " different files!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		registerRecipes(recipesToDoLater);
	}

	public void registerRecipes(Set<Pair<PKItem, ConfigurationSection>> items) {
		int registered = 0;

		for (Pair<PKItem, ConfigurationSection> pair : items) {
			ConfigurationSection section = pair.getRight();

			PKItem item = pair.getLeft();

			for (RecipeParser parser : RecipeParser.getParserRegistry()) {
				if (section.get(parser.getConfigSectionName()) instanceof ConfigurationSection) {
					ConfigurationSection currentSection = section.getConfigurationSection(parser.getConfigSectionName());

					Recipe recipe = parser.parseRecipe(currentSection, item);
					if (recipe != null) {
						if (parser.registerRecipe(recipe)) {
							ConfigManager.registered.put(recipe, parser);
							registered++;
						}
					}
				}
			}

			registered++;
		}

		if (registered > 0) {
			ProjectKorraItems.log.info("Registered "+ registered + " custom recipes for items.");
		}
	}

	public static void unregisterRecipes() {
		for (Recipe r : registered.keySet()) {
			registered.get(r).unregisterRecipe(r);
		}
	}

}
