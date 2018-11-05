package com.projectkorra.items.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapelessRecipe;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.ProjectKorraItems;

public class PKIShapelessRecipe extends ShapelessRecipe implements PKIRecipe {

	private PKItem item;
	
	public PKIShapelessRecipe(PKItem result, int amount) {
		super(new NamespacedKey(ProjectKorraItems.plugin, result.getName()), result.buildItem(amount));
		
		this.item = result;
	}

	@Override
	public PKItem getItem() {
		return item;
	}

}
