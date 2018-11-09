package com.projectkorra.items.recipe;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.ProjectKorraItems;

public class PKIShapedRecipe extends ShapedRecipe implements PKIRecipe {

	private PKItem item;
	
	public PKIShapedRecipe(PKItem result, int amount) {
		super(new NamespacedKey(ProjectKorraItems.plugin, result.getName()), result.buildItem(amount));
		
		this.item = result;
	}
	
	public ShapedRecipe setIngredient(char key, PKItem item) {
		Validate.isTrue(getIngredientMap().containsKey(Character.valueOf(key)), "Symbol does not appear in the shape:", (long) key);

		getIngredientMap().put(Character.valueOf(key), item.buildItem());
		return this;
	}

	@Override
	public PKItem getItem() {
		return item;
	}

}
