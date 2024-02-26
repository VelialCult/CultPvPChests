package ru.velialcult.pvpchests.chest.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.velialcult.library.bukkit.utils.location.LocationUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import ru.velialcult.library.java.utils.TimeUtil;
import ru.velialcult.pvpchests.chest.Chest;
import ru.velialcult.pvpchests.CultPvPChests;
import ru.velialcult.pvpchests.file.ConfigFile;
import ru.velialcult.pvpchests.manager.LootChestManager;
import ru.velialcult.pvpchests.provides.ProvidersManager;

import java.util.*;

public class ChestManager {
    private final List<Chest> chests;

    private final ConfigFile configFile;
    private final LootChestManager lootChestManager;
    private final CultPvPChests cultPvPChests;
    private final ProvidersManager providersManager;

    public ChestManager(CultPvPChests cultPvPChests) {
        chests = new ArrayList<>();
        this.cultPvPChests = cultPvPChests;
        this.configFile = cultPvPChests.getConfigFile();
        this.lootChestManager = cultPvPChests.getLootChestManager();
        this.providersManager = cultPvPChests.getProvidersManager();
    }

    public void deleteChest(Chest chest) {
        removeChest(chest.getKey());
        chest.delete();
    }

    public Chest getChestByLocation(Location location) {
        return chests.stream()
                .filter(chest -> chest.getLocation().equals(location))
                .findFirst()
                .orElse(null);
    }

    public boolean chestNameIsExists(String key) {
        return chests.stream().anyMatch(chest -> chest.getKey().equals(key));
    }

    public String getTimeUntilOpen(Chest chest) {
        if (Bukkit.getOnlinePlayers().size() < chest.getMinOnlinePlayers()) {
            return VersionAdapter.TextUtil().setReplaces(configFile.getMinOnline(),
                    new ReplaceData("{online}", chest.getMinOnlinePlayers()));
        }
        if (chest.isOpenable())  {
            return VersionAdapter.TextUtil().setReplaces(configFile.getUnlocked(),
                    new ReplaceData("{time}", TimeUtil.getTime(chest.getTimeUntilClose())));
        }  else {
            return VersionAdapter.TextUtil().setReplaces(configFile.getLocked(),
                    new ReplaceData("{time}", TimeUtil.getTime(chest.getTimeUntilOpen())));
        }
    }

    public void closChest(Chest chest) {
        try {
            chest.close();
            Location location = chest.getLocation();
            Block block = location.getBlock();
            Inventory chestInventory =  ( (org.bukkit.block.Chest) block.getState()).getInventory();
            chestInventory.clear();
        } catch (Exception e) {
            cultPvPChests.getLogger().warning("Произошла ошибка при закрытии сундука: " + e.getMessage());
        }
    }

    public void openChest(Chest chest) {
        chest.open();
        fillChest(chest);
        if (!chest.getUnlockedBroadcast().isEmpty()) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                VersionAdapter.MessageUtils().sendMessage(onlinePlayer, chest.getUnlockedBroadcast());
            }
        }
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
        try {
            Location location = chest.getLocation();
            Block block = location.getBlock();
            lootChestManager.fillChestWithLoot(chest, block, chest.getLoot());
        } catch (Exception e) {
            cultPvPChests.getLogger().warning("Произошла ошибка при заполнении сундука: " + e.getMessage());
        }
    }

    public void loadChests() {
        try {
            FileConfiguration config = configFile.getConfig();
            if (config.contains("chests")) {
                for (String key : config.getConfigurationSection("chests").getKeys(false)) {
                    HashMap<ItemStack, Double> loot = new HashMap<>();
                    if (config.contains("chests." + key + ".loot")) {
                        for (String itemKey : config.getConfigurationSection("chests." + key + ".loot").getKeys(false)) {
                            ItemStack item = ItemStack.deserialize(config.getConfigurationSection("chests." + key + ".loot." + itemKey + ".data").getValues(true));
                            double chance = config.getDouble("chests." + key + ".loot." + itemKey + ".chance");
                            loot.put(item, chance);
                        }
                    }
                    long delay = TimeUtil.parseStringToTime(config.getString("chests." + key + ".delay", "1m"));
                    List<String> message = config.getStringList("chests." + key + ".message");
                    int minOnlinePlayers = config.getInt("chests." + key + ".minOnlinePlayers");
                    double itemSlotChance = config.getDouble("chests." + key + ".item-slot-chance", 0.25);
                    long pauseDelay = TimeUtil.parseStringToTime(config.getString("chests." + key + ".pause-delay", "1m"));
                    List<String> hologramLines = config.getStringList("chests." + key + ".hologram-lines");
                    String[] loc = config.getString("chests." + key + ".location").split(";");
                    World world = providersManager.useMultiverseCore() ? providersManager.getMultiVerseProvider().getWorld(loc[0]) : Bukkit.getWorld(loc[0]);
                    Location location = new Location(world, Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));
                    addChest(new Chest(key, location, delay, pauseDelay, message, minOnlinePlayers, loot, hologramLines, itemSlotChance));
                }
            }
        } catch (Exception e) {
            cultPvPChests.getLogger().warning("Произошла ошибка при загрузке сундуков: " + e.getMessage());
        }
    }

    public void saveChests() {
        try {
            FileConfiguration config = configFile.getConfig();
            for (Chest chest : chests) {
                String id = chest.getKey();
                for (ItemStack item : chest.getLoot().keySet()) {
                    String uniqueKey = UUID.randomUUID().toString();
                    config.set("chests." + id + ".loot." + uniqueKey + ".chance", chest.getLoot().get(item));
                    config.set("chests." + id + ".loot." + uniqueKey + ".data", item.serialize());
                }
                config.set("chests." + id + ".item-slot-chance", chest.getItemSlotChance());
                config.set("chests." + id + ".hologram-lines", chest.getHologramLines());
                config.set("chests." + id + ".pause-delay", TimeUtil.parseTimeToString(chest.getPauseDelay()));
                config.set("chests." + id + ".delay", TimeUtil.parseTimeToString(chest.getDelay()));
                config.set("chests." + id + ".message", chest.getUnlockedBroadcast());
                config.set("chests." + id + ".minOnlinePlayers", chest.getMinOnlinePlayers());
                config.set("chests." + id + ".location", LocationUtil.locationToString(chest.getLocation()));
            }
            configFile.save();
        } catch (Exception e) {
            cultPvPChests.getLogger().warning("Произошла ошибка при сохранении сундуков: " + e.getMessage());
        }
    }

    public List<Chest> getChests() {
        return chests;
    }
}
