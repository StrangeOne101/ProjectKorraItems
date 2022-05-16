package com.projectkorra.items.loot;

import com.projectkorra.items.PKItem;

import java.util.List;
import java.util.Random;

public interface LootProvider {

    List<Item> getItems();

    default int getWeight() {
        return 1;
    }

    float getChance();
}
