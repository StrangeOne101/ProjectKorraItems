package com.projectkorra.items.items;

import com.projectkorra.items.PKItem;
import com.strangeone101.holoitemsapi.CustomItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Consumable extends PKItem implements com.strangeone101.holoitemsapi.interfaces.Interactable {

    public Consumable(String name, Material material) {
        super(name, material);
    }

    @Override
    public boolean onInteract(Player player, CustomItem customItem, ItemStack itemStack) {



        customItem.damageItem(itemStack, 1, player);
        //TODO
        return true;
    }
}
