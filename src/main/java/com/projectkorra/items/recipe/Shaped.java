package com.projectkorra.items.recipe;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.configuration.ConfigManager;
import com.strangeone101.holoitemsapi.recipe.RecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class Shaped extends RecipeParser<ShapedRecipe> {

    public Shaped() {
        super("Shaped");
    }

    @Override
    public ShapedRecipe parseRecipe(ConfigurationSection section, PKItem item) {
        if (section.contains("Recipe")) {
            //List of all ingredients used in the current recipe
            List<RecipeChoice> ingredientIndex = new ArrayList<>();

            //A list of the compiled shape lines
            List<String> compiledShapeLines = new ArrayList<>();

            List<String> configLines = section.getStringList("Recipe");
            for (String line : configLines) {

                String[] materials = null;

                String ingredientsLine = "";

                //Split the individual materials with commas and spaces
                if (line.contains(" ") && line.contains(",")) materials = line.replaceAll(" ", "").split(",");
                else if (line.contains(" ")) materials = line.split(" ");
                else if (line.contains(",")) materials = line.split(",");
                else materials = new String[] {line.trim()};

                if (materials.length > 3) {
                    ProjectKorraItems.createError(ConfigManager.languageConfig.get().getString("Load.Recipe.InvalidShape")
                            .replace("{item}", item.getInternalName()).replace("{file}", item.getFileLocation()));
                    return null;
                }

                //If it equals null there's already been an error created ^
                for (int i = 0; i < materials.length; i++) {
                    //Loop through every material on the line
                    String mat = materials[i];

                    //Load the ingredient. Can be material, tag or custom item
                    RecipeChoice ingredient = getIngredient(mat, item);
                    if (ingredient == null) return null; //An error occurred,

                    int index = ingredientIndex.size();
                    for (RecipeChoice rc : ingredientIndex) { //Loop through all existing ingredients and find it
                        if ((rc == null && ingredient == null) || rc.equals(ingredient)) { //Both are air OR they match
                            index = ingredientIndex.indexOf(rc);
                            break;
                        }
                    }

                    //If it doesn't exist, add it to the list
                    if (index >= ingredientIndex.size()) {
                        ingredientIndex.add(ingredient);
                    }

                    if (ingredientIndex.get(index) == null) {
                        ingredientsLine = ingredientsLine + " "; //Add air
                    } else {
                        ingredientsLine = ingredientsLine + "" + index; //Add a character to represent the ingredient
                    }
                }

                compiledShapeLines.add(ingredientsLine);
            }

            ItemStack stack = item.buildStack(null);
            stack.setAmount(section.getInt("Amount", 1));
            ShapedRecipe compiledRecipe = new ShapedRecipe(new NamespacedKey(ProjectKorraItems.plugin, "shaped/" +item.getInternalName()), stack);

            String[] shapeArray = new String[compiledShapeLines.size()];
            for (int i = 0; i < compiledShapeLines.size(); i++) shapeArray[i] = compiledShapeLines.get(i);

            compiledRecipe.shape(shapeArray);
            for (int i = 0; i < ingredientIndex.size(); i++) { //Loop through all ingredients
                RecipeChoice ingredient = ingredientIndex.get(i);
                if (ingredient != null) { //Null is for air
                    compiledRecipe.setIngredient(Character.forDigit(i, 10), ingredient);
                }
            }

            return compiledRecipe;

        } else {
            ProjectKorraItems.createError(ConfigManager.languageConfig.get().getString("Load.Recipe.NoShape")
                    .replace("{item}", item.getInternalName()).replace("{file}", item.getFileLocation()));
        }
        return null;
    }

    @Override
    public boolean registerRecipe(ShapedRecipe recipe) {
        RecipeManager.registerRecipe(recipe);
        RecipeManager.addToRecipeBookAuto(recipe);
        return true;
    }
}
