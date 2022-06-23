package com.projectkorra.items.utils;

import com.projectkorra.items.ProjectKorraItems;
import com.projectkorra.projectkorra.util.ReflectionHandler;
import com.strangeone101.holoitemsapi.util.ReflectionUtils;
import org.bukkit.loot.LootTable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LootUtil {

    private static Object BLANK;
    private static Field BLANK_FIELD;
    private static Method GET_HANDLE;
    private static boolean setup = false;

    static {
        //References:
        // - CraftServer#getLootTable
        // - CraftLootTable#convertContext:
        // `if (getHandle() != LootTable.a) {` //Checks if the loot table isn't just a default blank one
        // So if the underlying vanilla loot table is LootTable.a, it's blank
        try {
            GET_HANDLE = ReflectionHandler.getMethod("CraftLootTable", ReflectionHandler.PackageType.CRAFTBUKKIT, "getHandle");
            Class clazz = Class.forName("net.minecraft.world.level.storage.loot.LootTable");

            for (Field f : clazz.getDeclaredFields()) {
                if (f.getType().equals(clazz)) { //If the field is the same type as the loot table class
                    BLANK_FIELD = f;
                    break;
                }
            }

            if (BLANK_FIELD == null) {
                ProjectKorraItems.createError("Could not locate blank loot table! Will not be able to tell you if your loot table is invalid!");
            } else {
                BLANK = BLANK_FIELD.get(null); //Get statically
                setup = true;
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the provided LootTable is blank
     * @param table The loot table
     * @return True if it's blank
     */
    public static boolean isBlank(LootTable table) {
        if (!setup) return false;
        try {
            return GET_HANDLE.invoke(table).equals(BLANK);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

}
