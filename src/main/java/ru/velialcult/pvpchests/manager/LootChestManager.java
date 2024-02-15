package ru.velialcult.pvpchests.manager;

import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Random;

public class LootChestManager {

    private final Random random = new Random();

    public void fillChestWithLoot(Chest chest, Map<ItemStack, Double> lootItems) {
        for (int i = 0; i < chest.getInventory().getSize(); i++) {
            if (random.nextDouble() < 0.25) {
                ItemStack lootItem = getRandomLootItem(lootItems);
                if (lootItem != null) {
                    chest.getInventory().setItem(i, lootItem);
                }
            }
        }
    }

    private ItemStack getRandomLootItem(Map<ItemStack, Double> lootItems) {
        double totalWeight = 0.0;
        for (double weight : lootItems.values()) {
            totalWeight += weight;
        }

        double value = random.nextDouble() * totalWeight;
        for (Map.Entry<ItemStack, Double> entry : lootItems.entrySet()) {
            value -= entry.getValue();
            if (value <= 0.0) {
                return entry.getKey();
            }
        }

        return null;
    }
}
