package com.projectkorra.items.recipe;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.configuration.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class RecipeParser<T extends Recipe> {

    private static Map<String, RecipeParser> PARSER_REGISTRY = new HashMap<>();
    private static Map<String, IngredientProvider> INGREDIENT_REGISTRY = new HashMap<>();

    private String recipeType;

    public RecipeParser(String recipeType) {
        this.recipeType = recipeType;
    }

    /**
     * Parse a recipe from a configuration section. This method will return null if the recipe is invalid.
     * @param section The configuration section
     * @param item The PK item this recipe is being used in
     * @return The recipe
     */
    public abstract T parseRecipe(ConfigurationSection section, PKItem item);

    /**
     * Register a recipe to the server
     * @param recipe The recipe
     * @return True if registered
     */
    public abstract boolean registerRecipe(T recipe);

    /**
     * Unregister a recipe (called onDisable)
     * @param recipe The recipe to unregister
     */
    public void unregisterRecipe(T recipe) {}

    /**
     * @return The recipe type this parser is for
     */
    public String getRecipeType() {
        return recipeType;
    }

    /**
     * @return The configuration section this recipe is loaded from
     */
    public String getConfigSectionName() {
        return recipeType + "Recipe";
    }

    /**
     * Gets an ingredient from a string
     * @param ingredientString The ingredient listed in the recipe
     * @param item The item registering a recipe
     * @return The custom ingredient, or null if it doesn't exist
     */
    @Nullable
    public static RecipeChoice getIngredient(String ingredientString, PKItem item) {
        RecipeChoice ingredient = null;

        if (ingredientString.startsWith("#")) { //Tags
            String prefix = "minecraft";
            String suffix = ingredientString.substring(1).toLowerCase();
            if (ingredientString.contains(":")) {
                prefix = ingredientString.substring(1).split(":")[0].toLowerCase();
                suffix = ingredientString.substring(1).split(":")[1].toLowerCase();
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
        } else if (ingredientString.contains(":")) { //Custom item
            String prefix = ingredientString.split(":")[0].toLowerCase();
            String suffix = ingredientString.split(":")[1].toLowerCase();

            IngredientProvider provider = INGREDIENT_REGISTRY.get(prefix);
            if (provider == null) {
                ProjectKorraItems.createError(ConfigManager.languageConfig.get().getString("Load.Recipe.InvalidItem")
                        .replace("{item}", item.getInternalName()).replace("{recipeitem}", ingredientString)
                        .replace("{file}", item.getFileLocation()));
                return null;
            }
            ingredient = provider.getIngredient(suffix, item);
            if (ingredient != null) {
                return ingredient;
            }



        } else if (ingredientString.equals("") || ingredientString.equalsIgnoreCase("air")) { //Air
            ingredient = null;
        } else { //Materials
            Material material = Material.getMaterial(ingredientString.toUpperCase());

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

    /**
     * Register a new recipe parser. E.g. Shaped recipe parser, Shapeless recipe parser, etc.
     * @param recipeParser The recipe parser
     */
    public static void registerRecipeParser(RecipeParser recipeParser) {
        PARSER_REGISTRY.put(recipeParser.recipeType.toLowerCase(), recipeParser);
    }

    /**
     * Register a custom ingredient provider. This will allow
     * recipes to use ingredients from other plugins in its recipe
     * @param ingredient The ingredient provider
     */
    public static void registerIngredientProvider(IngredientProvider ingredient) {
        INGREDIENT_REGISTRY.put(ingredient.getIngredientPrefix().toLowerCase(), ingredient);
    }

    static {
        registerRecipeParser(new Shaped());
        registerRecipeParser(new Shapeless());
        registerRecipeParser(new Furnace());
        registerRecipeParser(new BlastFurnace());
        registerRecipeParser(new Smoker());
        registerRecipeParser(new Campfire());
        registerRecipeParser(new Stonecutter());

        registerIngredientProvider(new PKIIngredient());
    }

    /**
     * @return A list of all registered recipe parsers
     */
    public static Collection<RecipeParser> getParserRegistry() {
        return PARSER_REGISTRY.values();
    }

    /**
     * Check if this configuration section for a custom item has a recipe within it
     * @param section The configuration section of the item
     * @return True if it has a recipe
     */
    public static boolean hasRecipe(ConfigurationSection section) {
        for (RecipeParser parser : PARSER_REGISTRY.values()) {
            if (section.get(parser.getConfigSectionName()) instanceof ConfigurationSection) {
                return true;
            }
        }
        return false;
    }
}
