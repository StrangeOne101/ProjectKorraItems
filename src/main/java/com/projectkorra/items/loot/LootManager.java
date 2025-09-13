package com.projectkorra.items.loot;

import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.items.configuration.ConfigManager;
import com.projectkorra.items.utils.LootUtil;
import com.projectkorra.items.api.loot.CustomLootRegistry;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class LootManager {

    public static Random RANDOM = new Random();

    public static void loadAllLoot() {
        File folder = new File(ProjectKorraItems.plugin.getDataFolder(), "loot");

        if (!folder.exists()) folder.mkdir();

        CustomLootRegistry.clearLootExtensions();

        int registered = 0;

        try {
            for (Path path : Files.find(folder.toPath(), 15, (p, bfa) -> bfa.isRegularFile())
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".yml"))
                    .collect(Collectors.toList())) {
                File file = path.toFile();
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

                if (table == null || LootUtil.isBlank(table)) {
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

            ProjectKorraItems.log.info("Registered " + registered + " custom loot injections!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
