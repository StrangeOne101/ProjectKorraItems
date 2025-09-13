package com.projectkorra.items.api.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class HighStackRecipeChoice implements RecipeChoice {

    public HighStackRecipeChoice(ItemStack stack) {

    }

    @Override
    public ItemStack getItemStack() {
        return null;
    }

    @Override
    public RecipeChoice clone() {
        return null;
    }

    @Override
    public boolean test(ItemStack t) {

        return false;
    }
}
