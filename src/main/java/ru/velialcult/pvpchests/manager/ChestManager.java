package ru.velialcult.pvpchests.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.velialcult.library.bukkit.utils.location.LocationUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.utils.TimeUtil;
import ru.velialcult.pvpchests.Chest;
import ru.velialcult.pvpchests.CultPvPChests;
import ru.velialcult.pvpchests.file.ConfigFile;

import java.util.*;

public class ChestManager {
    private final List<Chest> chests;

    private final ConfigFile configFile;
    private final LootChestManager lootChestManager;

    public ChestManager(CultPvPChests cultPvPChests) {
        chests = new ArrayList<>();
        this.configFile = cultPvPChests.getConfigFile();
        this.lootChestManager = cultPvPChests.getLootChestManager();
    }

    public String getTimeUntilOpen(Chest chest) {
        if (chest.isOpenable()) return configFile.getOpenNow();
        return TimeUtil.getTime(chest.getTimeUntilOpen());
    }

    public void openChest(Chest chest) {
        chest.setOpenable(true);
        fillChest(chest);
        if (!chest.getMessage().isEmpty()) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                VersionAdapter.MessageUtils().sendMessage(onlinePlayer, chest.getMessage());
            }
        }
    }

    public Chest getChestByLocation(Location location) {
        return chests.stream()
                .filter(chest -> chest.getLocation().equals(location))
                .findFirst()
                .orElse(null);
    }

    public Chest getChestById(String id) {
        return chests.stream()
                .filter(chest -> chest.getKey().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void addChest(Chest chest) {
        chests.add(chest);
    }

    public void removeChest(String id) {
        Chest chest = getChestById(id);
        if (chest != null) {
            chests.remove(chest);
        }
    }

    public void fillChest(Chest chest) {
        Location location = chest.getLocation();
        Block block = location.getBlock();
        if (block.getType() == Material.CHEST) {
            org.bukkit.block.Chest blockChest = (org.bukkit.block.Chest) location.getBlock().getState();
            lootChestManager.fillChestWithLoot(blockChest, chest.getLoot());
        }
    }

    public void loadChests() {
        FileConfiguration config = configFile.getConfig();
        if (config.contains("chests")) {
            for (String key : config.getConfigurationSection("chests").getKeys(false)) {
                HashMap<ItemStack, Double> loot = new HashMap<>();
                if (config.contains("chests." + key + ".loot")) {
                    for (String itemKey : config.getConfigurationSection("chests." + key + ".loot").getKeys(false)) {
                        ItemStack item = ItemStack.deserialize(config.getConfigurationSection("chests." + key + ".loot." + itemKey).getValues(true));
                        double chance = config.getDouble("chests." + key + ".loot." + itemKey + ".chance");
                        loot.put(item, chance);
                    }
                }
                long timer = TimeUtil.parseStringToTime("chests." + key + ".delay");
                List<String> message = config.getStringList("chests." + key + ".message");
                int minOnlinePlayers = config.getInt("chests." + key + ".minOnlinePlayers");
                List<String> hologramLines = config.getStringList("chests." + key + ".hologram-lines");
                addChest(new Chest(key, LocationUtil.stringToLocation("chests." + key + ".location"), timer, message, minOnlinePlayers, loot, hologramLines));
            }
        }
    }

    public void saveChests() {
        FileConfiguration config = configFile.getConfig();
        for (Chest chest : chests) {
            String id = chest.getKey();
            for (ItemStack item : chest.getLoot().keySet()) {
                config.set("chests." + id + ".loot." + item.getType() + ".chance", chest.getLoot().get(item));
                config.set("chests." + id + ".loot." + item.getType() + ".data", item.serialize());
            }
            config.set("chests." + id + ".delay", chest.getDelay());
            config.set("chests." + id + ".message", chest.getMessage());
            config.set("chests." + id + ".minOnlinePlayers", chest.getMinOnlinePlayers());
            config.set("chests." + id + ".location", LocationUtil.locationToString(chest.getLocation()));
        }
        configFile.save();
    }

    public List<Chest> getChests() {
        return chests;
    }
}
