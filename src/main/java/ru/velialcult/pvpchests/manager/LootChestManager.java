package ru.velialcult.pvpchests.manager;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import ru.velialcult.pvpchests.chest.Chest;
import ru.velialcult.pvpchests.CultPvPChests;

import java.util.Map;
import java.util.Random;

public class LootChestManager {

    private final Random random = new Random();

    public void fillChestWithLoot(Chest chest, Block block, Map<ItemStack, Double> lootItems) {
        try {
            org.bukkit.block.Chest chestBlock = (org.bukkit.block.Chest) block.getState();
            for (int i = 0; i < chestBlock.getInventory().getSize(); i++) {
                double randomValue = 0.1 + (100 - 0.1) * random.nextDouble();
                if (randomValue < chest.getItemSlotChance()) {
                    ItemStack lootItem = getRandomLootItem(lootItems);
                    if (lootItem != null) {
                        chestBlock.getInventory().setItem(i, lootItem);
                    }
                }
            }
        } catch (Exception e) {
            CultPvPChests.getInstance().getLogger().warning("Произошла ошибка при заполении сундука вещами: " + e.getMessage());
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
