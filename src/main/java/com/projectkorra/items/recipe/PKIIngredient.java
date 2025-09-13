package com.projectkorra.items.recipe;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.configuration.ConfigManager;
import com.projectkorra.items.api.CustomItemRegistry;
import com.projectkorra.items.api.recipe.CIRecipeChoice;
import org.bukkit.inventory.RecipeChoice;

public class PKIIngredient implements IngredientProvider {

    @Override
    public RecipeChoice getIngredient(String name, PKItem item) {
        if (CustomItemRegistry.getCustomItem(name) == null) {
            ProjectKorraItems.createError(ConfigManager.languageConfig.get().getString("Load.Recipe.InvalidItem")
                    .replace("{item}", item.getInternalName()).replace("{recipeitem}", name)
                    .replace("{file}", item.getFileLocation()));
            return null;
        }
        return new CIRecipeChoice(CustomItemRegistry.getCustomItem(name).buildStack(null));
    }

    @Override
    public String getIngredientPrefix() {
        return "PKI";
    }
}
