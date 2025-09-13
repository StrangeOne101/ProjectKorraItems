package com.projectkorra.items.api.interfaces;

import com.projectkorra.items.api.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * An interface for custom items that can be placed
 */
public interface Placeable {

    /**
     * Fired when the CustomItem is placed. Return true to cancel the placement
     * @param block The block being placed
     * @param player The player placing it
     * @param item The item being placed
     * @param stack The item stack
     * @return If the event should cancel the place event
     */
    boolean place(Block block, Player player, CustomItem item, ItemStack stack);
}
