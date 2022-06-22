package com.projectkorra.items.recipe;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.configuration.ConfigManager;
import com.strangeone101.holoitemsapi.recipe.RecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class Furnace extends RecipeParser<FurnaceRecipe> {

    public Furnace() {
        super("Furnace");
    }

    @Override
    public FurnaceRecipe parseRecipe(ConfigurationSection section, PKItem item) {
        int amount = section.getInt("Amount", 1);
        String ingredient = section.getString("Item");
        if (ingredient == null) {
            ProjectKorraItems.createError(ConfigManager.languageConfig.get().getString("Load.Recipe.InvalidFurnace")
                    .replace("{item}", item.getInternalName()).replace("{file}", item.getFileLocation()));
            return null;
        }
        float experience = (float)section.getDouble("Experience", 0.1D);
        int burntime = section.getInt("CookTime", 200);
        RecipeChoice recipeIngredient = getIngredient(ingredient, item);
        if (recipeIngredient != null) { //If an error didn't occur
            NamespacedKey key = new NamespacedKey(ProjectKorraItems.plugin, "furnace/" + item.getInternalName());
            ItemStack stack = item.buildStack(null);
            stack.setAmount(amount);
            FurnaceRecipe recipe = new FurnaceRecipe(key, stack, recipeIngredient, experience, burntime);

            return recipe;
        }
        return null;
    }

    @Override
    public boolean registerRecipe(FurnaceRecipe recipe) {
        RecipeManager.registerRecipe(recipe);
        RecipeManager.addToRecipeBookAuto(recipe);
        return true;
    }
}
