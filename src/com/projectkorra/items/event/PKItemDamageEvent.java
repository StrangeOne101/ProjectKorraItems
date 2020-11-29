package com.projectkorra.items.event;

import com.projectkorra.items.PKItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PKItemDamageEvent extends PKItemEvent implements Cancellable {

    private ItemStack stack;
    private Player player;
    private boolean cancelled;

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public PKItemDamageEvent(PKItem item, ItemStack stack, Player player) {
        super(item);
        this.stack = stack;
        this.player = player;
    }


    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getStack() {
        return stack;
    }
}
