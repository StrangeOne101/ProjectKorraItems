package com.projectkorra.items.event;

import com.projectkorra.items.PKItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class PKItemLoadEvent extends PKItemEvent implements Cancellable {

    private ConfigurationSection section;
    private File file;
    private boolean cancelled;

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public PKItemLoadEvent(PKItem item, ConfigurationSection section, File file) {
        super(item);
        this.section = section;
        this.file = file;
    }


    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public File getFile() {
        return file;
    }

    public void setFinalItem(PKItem item) {
        this.setItem(item);
    }
}
