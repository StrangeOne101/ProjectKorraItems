package com.projectkorra.items.recipe;

import com.projectkorra.items.PKItem;
import org.bukkit.inventory.RecipeChoice;

public interface IngredientProvider {

    /**
     * Get the ingredient for the recipe.
     * @param name The ingredient name
     * @param item The PK item this ingredient is being used in
     * @return The ingredient
     */
    public RecipeChoice getIngredient(String name, PKItem item);

    /**
     * Get the prefix used to specify a custom ingredient from another plugin
     * @return The prefix
     */
    public String getIngredientPrefix();
}
