package com.projectkorra.items;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ItemSet {

    private static Map<String, ItemSet> SETS = new HashMap<>();

    private String name;
    private Set<PKItem> items = new HashSet<>();

    protected ItemSet(String setName) {
        this.name = setName;
    }

    /**
     * Add an item to this set.
     * @param item The item to add.
     */
    public void addItem(@NotNull PKItem item) {
        items.add(item);

        //If the set isn't registered yet, register it
        if (!SETS.containsKey(name)) {
            SETS.put(name, this);
        }
    }

    /**
     * Get the item set with the given name.
     * @param setName The name of the set.
     * @return The item set with the given name, or a new set if it doesn't exist
     */
    public static ItemSet of(@NotNull String setName) {
        if (SETS.containsKey(setName.toLowerCase())) {
            return SETS.get(setName.toLowerCase());
        }
        return new ItemSet(setName.toLowerCase());
    }
}
