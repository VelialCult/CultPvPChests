package ru.velialcult.pvpchests.manager;

import org.bukkit.inventory.ItemStack;
import ru.velialcult.pvpchests.Chest;

import java.util.Map;
import java.util.Random;

public class LootChestManager {

    private final Random random = new Random();

    public void fillChestWithLoot(Chest chest, org.bukkit.block.Chest chestBlock, Map<ItemStack, Double> lootItems) {
        for (int i = 0; i < chestBlock.getInventory().getSize(); i++) {
            if (random.nextDouble() < chest.getItemSlotChance()) {
                ItemStack lootItem = getRandomLootItem(lootItems);
                if (lootItem != null) {
                    chestBlock.getInventory().setItem(i, lootItem);
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
