package com.projectkorra.items.recipe;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.configuration.ConfigManager;
import com.projectkorra.items.api.recipe.RecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;

public class Smithing extends RecipeParser<SmithingRecipe> {

    public Smithing() {
        super("Smithing");
    }

    @Override
    public SmithingRecipe parseRecipe(ConfigurationSection section, PKItem item) {
        int amount = section.getInt("Amount", 1);
        String ingredient = section.getString("Primary");
        String ingredient2 = section.getString("Secondary");
        if (ingredient == null || ingredient2 == null) {
            ProjectKorraItems.createError(ConfigManager.languageConfig.get().getString("Load.Recipe.InvalidFurnace")
                    .replace("{item}", item.getInternalName()).replace("{file}", item.getFileLocation()));
            return null;
        }
        RecipeChoice recipeIngredient = getIngredient(ingredient, item);
        RecipeChoice recipeIngredient2 = getIngredient(ingredient2, item);
        if (recipeIngredient != null && recipeIngredient2 != null) { //If an error didn't occur
            NamespacedKey key = new NamespacedKey(ProjectKorraItems.plugin, "smith/" + item.getInternalName());
            ItemStack stack = item.buildStack(null);
            stack.setAmount(amount);
            SmithingRecipe recipe = new SmithingRecipe(key, stack, recipeIngredient, recipeIngredient2);

            return recipe;
        }
        return null;
    }

    @Override
    public boolean registerRecipe(SmithingRecipe recipe) {
        RecipeManager.registerRecipe(recipe);
        RecipeManager.addToRecipeBookAuto(recipe);
        return true;
    }

}
