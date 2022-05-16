package com.projectkorra.items.loot;

import com.projectkorra.items.PKItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Pool implements LootProvider {

    private int rolls = 1;
    private LootProvider[] items;
    private Random random;
    private int innerWeight;

    private int totalWeight;

    public Pool(Random random, int rolls, int weight, LootProvider... items) {
        this.rolls = rolls;
        this.items = items;
        this.innerWeight = weight;
        this.random = random;

        for (LootProvider provider : items) totalWeight += provider.getWeight();
    }

    @Override
    public List<Item> getItems() {
        List<Item> rolledItems = new ArrayList<>();
        int ro = rolls;

        while (ro > 0) {
            int idx = 0;
            for (double r = random.nextDouble() * totalWeight; idx < items.length - 1; ++idx) {
                r -= items[idx].getWeight();
                if (r <= 0.0) break;
            }

            LootProvider provider = items[idx];
            rolledItems.addAll(provider.getItems()); //This will either add a single item or all items from another pool

            ro--;
        }

        return rolledItems;
    }

    public List<ItemStack> getItemStacks(Player player) {
        List<ItemStack> stacks = new ArrayList<>();

        for (Item item : getItems()) { //Get the items from this roll
            if (random.nextFloat() <= item.getChance()) {
                stacks.add(item.getItemStack(player));
            }
        }
        return stacks;
    }

    public int getRolls() {
        return rolls;
    }

    @Override
    public float getChance() {
        return 1;
    }

    @Override
    public int getWeight() {
        return innerWeight;
    }

    public static Pool getFromConfigSection(ConfigurationSection section, String file) {
        int rolls = 1;
        int weight = 1;
        List<LootProvider> items = new ArrayList<>();

        if (section.contains("Rolls")) {
            rolls = section.getInt("Rolls");
        }

        if (section.contains("Weight")) {
            weight = section.getInt("Weight");
        }

        for (String key : section.getKeys(false)) {
            if (key.toLowerCase(Locale.ROOT).startsWith("pool")) {
                Pool pool = Pool.getFromConfigSection(section.getConfigurationSection(key), file);

                if (pool == null) return null;

                items.add(pool);
            } else if (key.toLowerCase(Locale.ROOT).startsWith("item")) {
                Item item = Item.getFromConfigSection(section.getConfigurationSection(key), file);

                if (item == null) return null;

                items.add(item);
            }
        }

        return new Pool(LootManager.RANDOM, rolls, weight, items.toArray(new LootProvider[0]));
    }
}
