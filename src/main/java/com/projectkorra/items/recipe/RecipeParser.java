package com.projectkorra.items.recipe;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.configuration.ConfigManager;
import com.strangeone101.holoitemsapi.CustomItemRegistry;
import com.strangeone101.holoitemsapi.recipe.CIRecipeChoice;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class RecipeParser<T extends Recipe> {

    private static Map<String, RecipeParser> REGISTRY = new HashMap<>();

    private String recipeType;

    public RecipeParser(String recipeType) {
        this.recipeType = recipeType;
    }

    public abstract T parseRecipe(ConfigurationSection section, PKItem item);

    public abstract boolean registerRecipe(T recipe);

    public void unregisterRecipe(T recipe) {}

    public String getRecipeType() {
        return recipeType;
    }

    public String getConfigSectionName() {
        return recipeType + "Recipe";
    }

    public static RecipeChoice getIngredient(String ingredientString, PKItem item) {
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
                ProjectKorraItems.createError(ConfigManager.languageConfig.get().getString("Load.Recipe.InvalidTag")
                        .replace("{item}", item.getInternalName()).replace("{tag}", ingredientString)
                        .replace("{file}", item.getFileLocation()));
                return null;
            }
            ingredient = new RecipeChoice.MaterialChoice(tag);
        } else if (ingredientString.startsWith("@")) { //Custom item
            String name = ingredientString.substring(1);

            if (CustomItemRegistry.getCustomItem(name) == null) {
                ProjectKorraItems.createError(ConfigManager.languageConfig.get().getString("Load.Recipe.InvalidItem")
                        .replace("{item}", item.getInternalName()).replace("{recipeitem}", name)
                        .replace("{file}", item.getFileLocation()));
                return null;
            }

            ingredient = new CIRecipeChoice(CustomItemRegistry.getCustomItem(name).buildStack(null));
        } else if (ingredientString.equals("") || ingredientString.equalsIgnoreCase("air")) { //Air
            ingredient = null;
        } else { //Materials
            Material material = Material.getMaterial(ingredientString.toUpperCase(Locale.ROOT));

            if (material == null) {
                String error = ConfigManager.languageConfig.get().getString("Load.Recipe.InvalidMaterial");
                error = error.replace("{material}", ingredientString).replace("{item}", item.getInternalName()
                        .replace("{file}", item.getFileLocation()));
                ProjectKorraItems.createError(error);
                return null;
            }

            ingredient = new RecipeChoice.MaterialChoice(material);
        }

        return ingredient;
    }

    public static void registerRecipeType(RecipeParser recipeParser) {
        REGISTRY.put(recipeParser.recipeType.toLowerCase(), recipeParser);
    }

    static {
        registerRecipeType(new Shaped());
        registerRecipeType(new Shapeless());
        registerRecipeType(new Furnace());
        registerRecipeType(new BlastFurnace());
        registerRecipeType(new Smoker());
        registerRecipeType(new Campfire());
        registerRecipeType(new Stonecutter());
    }

    public static Collection<RecipeParser> getRegistry() {
        return REGISTRY.values();
    }

    public static boolean hasRecipe(ConfigurationSection section) {
        for (RecipeParser parser : REGISTRY.values()) {
            if (section.get(parser.getConfigSectionName()) instanceof ConfigurationSection) {
                return true;
            }
        }
        return false;
    }
}
