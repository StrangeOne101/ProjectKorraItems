package com.projectkorra.items.recipe;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.configuration.ConfigManager;
import com.projectkorra.items.api.recipe.RecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmokingRecipe;

public class BlastFurnace extends RecipeParser<BlastingRecipe> {

    public BlastFurnace() {
        super("BlastFurnace");
    }

    @Override
    public BlastingRecipe parseRecipe(ConfigurationSection section, PKItem item) {
        int amount = section.getInt("Amount", 1);
        String ingredient = section.getString("Item");
        if (ingredient == null) {
            ProjectKorraItems.createError(ConfigManager.languageConfig.get().getString("Load.Recipe.InvalidFurnace")
                    .replace("{item}", item.getInternalName()).replace("{file}", item.getFileLocation()));
            return null;
        }
        float experience = (float)section.getDouble("Experience", 0.1D);
        int burntime = section.getInt("CookTime", 100);
        RecipeChoice recipeIngredient = getIngredient(ingredient, item);
        if (recipeIngredient != null) { //If an error didn't occur
            NamespacedKey key = new NamespacedKey(ProjectKorraItems.plugin, "blast/" +item.getInternalName());
            ItemStack stack = item.buildStack(null);
            stack.setAmount(amount);
            BlastingRecipe recipe = new BlastingRecipe(key, stack, recipeIngredient, experience, burntime);

            return recipe;
        }
        return null;
    }

    @Override
    public boolean registerRecipe(BlastingRecipe recipe) {
        RecipeManager.registerRecipe(recipe);
        RecipeManager.addToRecipeBookAuto(recipe);
        return true;
    }

}
