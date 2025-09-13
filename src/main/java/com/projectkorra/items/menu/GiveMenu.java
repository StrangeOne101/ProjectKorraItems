package com.projectkorra.items.menu;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.api.CustomItem;
import com.projectkorra.items.api.CustomItemRegistry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveMenu extends MenuBase {

    public static final MenuItem BLANK = new MenuItem("&0", Material.BLACK_STAINED_GLASS_PANE) { //The name must have a color char or else it disables the custom name
        @Override
        public void onClick(Player player) {

        }
    };

    private int page = 0;
    private Player openPlayer;

    public GiveMenu() {
        super("ProjectKorra Items", calculateRowSize());
    }


    public MenuItem getPageArrow(boolean left) {
        String s = "&ePrevious Page &7(&e{page}&7/&e{maxpage}&7)"
                .replace("{page}", (page + 1) + "")
                .replace("{maxpage}", getMaxPages() + "");

        MenuItem item = new MenuItem(s, Material.ARROW) {
            @Override
            public void onClick(Player player) {
                page--;
                update();
            }
        };
        item.addDescription(ChatColor.GRAY + "Click to visit the previous page of items");
        return item;
    }

    public MenuItem getClose() {
        String s = "&cClose Menu";

        MenuItem item = new MenuItem(s, Material.BARRIER) {
            @Override
            public void onClick(Player player) {
                closeMenu(player);
            }
        };
        item.addDescription("&7Click to close this menu");
        return item;
    }

    public void update() {
        int y = calculateRowSize() - 1;

        if (page > 0) this.addMenuItem(getPageArrow(true), 0, y);
        else this.addMenuItem(BLANK, 0, y);
        if (page < getMaxPages() - 1) this.addMenuItem(getPageArrow(false), 8, y);
        else this.addMenuItem(BLANK, 8, y);

        this.addMenuItem(getClose(), 4, y);

        for (int i = 0; i < 9; i++) {
            if (this.getMenuItem(calculateRowSize() * 9 + i) == null) {
                this.addMenuItem(BLANK, i, y);
            }
        }

        CustomItem[] items = CustomItemRegistry.getCustomItems().values().toArray(new CustomItem[0]);

        for (int i = 0; i < y * 9; i++) {
            int index = getMaxPages() > 1 ? (54 * (page + 1) + i) : i;
            if (index >= items.length) {
                this.getInventory().setItem(i, null); //Remove item at this index as this page doesn't go that far
                continue;
            }
            PKItem item = (PKItem) items[index];
            ItemStack newItem = item.buildStack(openPlayer);

            this.getInventory().setItem(i, newItem); //Remove the current item and set the proper one there
        }
    }

    @Override
    public void openMenu(Player player) {
        super.openMenu(player);

        this.openPlayer = player;
        update();
    }

    private int getMaxPages() {
        return (CustomItemRegistry.getCustomItems().size() / 54) + 1;
    }

    private static int calculateRowSize() {
        return CustomItemRegistry.getCustomItems().size() > 45 ? 6 : (CustomItemRegistry.getCustomItems().size() / 9) + 2;
    }
}
