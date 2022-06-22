package com.projectkorra.items.recipe;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.configuration.ConfigManager;
import com.strangeone101.holoitemsapi.recipe.RecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class Shapeless extends RecipeParser<ShapelessRecipe> {

    public Shapeless() {
        super("Shapeless");
    }

    @Override
    public ShapelessRecipe parseRecipe(ConfigurationSection section, PKItem item) {
        if (section.contains("Ingredients")) {
            List<RecipeChoice> ingredients = new ArrayList<>();
            List<String> ingredientsList;
            if (section.get("Ingredients") instanceof List) {
                ingredientsList = section.getStringList("Ingredients");
            } else {
                String ingredientsString = section.getString("Ingredients").trim();
                String[] ingredientsArray = null;

                //Split the individual materials with commas and spaces
                if (ingredientsString.contains(" ") && ingredientsString.contains(",")) ingredientsArray = ingredientsString.replaceAll(" ", "").split(",");
                else if (ingredientsString.contains(",")) ingredientsArray = ingredientsString.split(",");
                else if (ingredientsString.contains(" ")) ingredientsArray = ingredientsString.split(" ");
                else {
                    ProjectKorraItems.createError(ConfigManager.languageConfig.get().getString("Load.Recipe.InvalidShapeless")
                            .replace("{item}", item.getInternalName()).replace("{file}", item.getFileLocation()));
                    return null;
                }

                ingredientsList = List.of(ingredientsArray);
            }

            for (String s : ingredientsList) {
                RecipeChoice ingr = getIngredient(s, item);
                if (ingr == null) return null; //An error occurred, so skip the recipe for now
            }

            ItemStack stack = item.buildStack(null);
            stack.setAmount(section.getInt("Amount", 1));
            ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(ProjectKorraItems.plugin, "shapeless/" + item.getInternalName()), stack);

            ingredients.forEach(recipe::addIngredient);

            return recipe;

        } else {
            ProjectKorraItems.createError(ConfigManager.languageConfig.get().getString("Load.Recipe.InvalidShapeless")
                    .replace("{item}", item.getInternalName()).replace("{file}", item.getFileLocation()));
        }
        return null;
    }

    @Override
    public boolean registerRecipe(ShapelessRecipe recipe) {
        RecipeManager.registerRecipe(recipe);
        RecipeManager.addToRecipeBookAuto(recipe);
        return true;
    }
}
