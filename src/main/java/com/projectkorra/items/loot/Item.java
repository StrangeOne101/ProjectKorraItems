package com.projectkorra.items.loot;

import com.projectkorra.items.PKItem;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.configuration.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class Item implements LootProvider {

    private PKItem item;
    private float chance;
    private NumberProvider amount, durability;
    private Random random;
    private int weight;

    public Item(Random random, PKItem item, float chance, int weight, NumberProvider amount, NumberProvider durability) {
        this.item = item;
        this.chance = chance;
        this.amount = amount;
        this.random = random;
        this.durability = durability;
        this.weight = weight;
    }

    @Override
    public List<Item> getItems() {
        return List.of(this);
    }

    @Override
    public int getWeight() {
        return weight;
    }

    public ItemStack getItemStack(Player player) {
        ItemStack stack = item.buildStack(player);
        stack.setAmount(amount.getNumber(random));
        if (item.getMaxDurability() > 0) item.setDurability(stack, durability.getNumber(random));
        return stack;
    }

    @Override
    public float getChance() {
        return chance;
    }

    public static Item getFromConfigSection(ConfigurationSection config, String file) {
        String error = null;
        if (config.contains("Name")) {
            PKItem item = PKItem.getItemFromName(config.getString("Name"));
            if (item != null) {
                float chance = (float)config.getDouble("Chance", 1D);
                int weight = Math.max(config.getInt("Weight", 1), 1);

                Object amount = config.get("Amount");
                NumberProvider amountProvider = NumberProvider.ONE;
                if (amount instanceof ConfigurationSection) { //For min and max
                    int min = ((ConfigurationSection) amount).getInt("Min", 1);
                    int max = ((ConfigurationSection) amount).getInt("Max", 1);
                    amountProvider = new NumberProvider.MinMax(min, max);
                } else if (amount instanceof Number && (int)amount != 1) { //Dont bother with 1 because its already set that way
                    amountProvider = new NumberProvider.Static((int)amount);
                }

                Object durability = config.get("Durability");
                NumberProvider durabilityProvider = NumberProvider.ZERO;
                if (durability instanceof ConfigurationSection) { //For min and max
                    int min = ((ConfigurationSection) durability).getInt("Min", 1);
                    int max = ((ConfigurationSection) durability).getInt("Max", 1);
                    amountProvider = new NumberProvider.MinMax(min, max);
                } else if (durability instanceof Number && (int)durability != 0) { //Dont bother with 1 because its already set that way
                    amountProvider = new NumberProvider.Static((int)durability);
                }

                return new Item(LootManager.RANDOM, item, chance, weight, amountProvider, durabilityProvider);
            } else
                error = ConfigManager.languageConfig.get().getString("Loot.Item.InvalidItem")
                        .replace("{file}", file).replace("{item}", config.getString("Name"));
        } else
            error = ConfigManager.languageConfig.get().getString("Loot.Item.NoName")
                .replace("{file}", file).replace("{section}", config.getCurrentPath());

        if (error != null) {
            ProjectKorraItems.createError(error);
        }
        return null;
    }
}
