package com.projectkorra.items.loot;

import com.google.common.io.PatternFilenameFilter;
import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.configuration.ConfigManager;
import com.projectkorra.projectkorra.ProjectKorra;
import com.strangeone101.holoitemsapi.loot.CustomLootRegistry;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class LootManager {

    public static Random RANDOM = new Random();

    public static void loadAllLoot() {
        File folder = new File(ProjectKorraItems.plugin.getDataFolder(), "loot");

        if (!folder.exists()) folder.mkdir();

        CustomLootRegistry.clearLootExtensions();

        int registered = 0;

        for (File file : FileUtils.listFiles(folder, new String[] {"yml"}, true)) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            String pathName = file.getAbsolutePath().substring(folder.getAbsolutePath().length() + 1);
            String tableName = pathName.substring(0, pathName.length() - 4).replace("\\", "/"); //Scrap the .yml

            NamespacedKey key = null;
            try {
                key = NamespacedKey.minecraft("chests/" + tableName);
            } catch (IllegalArgumentException e) {
                ProjectKorraItems.createError(ConfigManager.languageConfig.get().getString("Loot.InvalidFile")
                        .replace("{file}", tableName));
                continue;
            }

            LootTable table = Bukkit.getLootTable(key);

            if (table == null || table.getKey().getKey().equals("empty")) {
                String s = ConfigManager.languageConfig.get().getString("Loot.InvalidTable").replace("{loottable}", tableName);
                ProjectKorraItems.createError(s);
                continue;
            }

            Pool pool = Pool.getFromConfigSection(config, pathName);

            if (pool == null) continue; //An error already occurred, so just skip the table for now

            CustomLootRegistry.registerLootExtension(table, ((lootTable, list, random, player) -> {
                List<ItemStack> newStacks = pool.getItemStacks(player);
                if (!newStacks.isEmpty()) list.addAll(newStacks);

                return !newStacks.isEmpty();
            }));

            ProjectKorraItems.log.info("Registered loot table extension for table \"" + table.getKey().toString() + "\"");

            registered++;

        }

        ProjectKorra.log.info("Registered " + registered + " custom loot injections!");
    }


}
