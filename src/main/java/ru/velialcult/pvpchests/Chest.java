package ru.velialcult.pvpchests;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import ru.velialcult.library.bukkit.utils.location.LocationUtil;
import ru.velialcult.library.java.utils.TimeUtil;
import ru.velialcult.pvpchests.file.ConfigFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Chest {

    private final ConfigFile configFile = CultPvPChests.getInstance().getConfigFile();

    private LocalDateTime lastOpen;
    private LocalDateTime open;

    private double itemSlotChance;
    private final String key;
    private List<String> hologramLines;
    private Map<ItemStack, Double> loot;
    private Location location;
    private long delay;
    private List<String> unlockedBroadcast;
    private boolean isOpenable;
    private int minOnlinePlayers;
    private long pauseDelay;

    public Chest(String key, Location location, long delay, long pauseDelay, int minOnlinePlayers) {
        this(key, location, delay, pauseDelay, new ArrayList<>(), minOnlinePlayers);
    }

    public Chest(String key, Location location, long delay, long pauseDelay, List<String> unlockedBroadcast, int minOnlinePlayers) {
        this(key, location, delay, pauseDelay, unlockedBroadcast, minOnlinePlayers, new HashMap<>(),  new ArrayList<>(), 0.25);
    }

    public Chest(String key,
                 Location location,
                 long delay,
                 long pauseDelay,
                 List<String> unlockedBroadcast,
                 int minOnlinePlayers,
                 Map<ItemStack, Double> loot,
                 List<String> hologramLines,
                 double itemSlotChance) {
        this.location = location;
        this.key = key;
        this.delay = delay;
        this.pauseDelay = pauseDelay;
        this.unlockedBroadcast = unlockedBroadcast;
        this.minOnlinePlayers = minOnlinePlayers;
        this.loot = loot;
        this.isOpenable = false;
        this.hologramLines = hologramLines;
        this.itemSlotChance = itemSlotChance;
        this.lastOpen = LocalDateTime.now();
    }

    public void delete() {
        try {
            configFile.getConfig().set("chests." + getKey(), null);
            CultPvPChests.getInstance().getConfigFile().reload();
        } catch (Exception e) {
            CultPvPChests.getInstance().getLogger().severe("Произошла ошибка при удалении сундука из конфигурации: " + e.getMessage());
        }
    }

    public long getTimeUntilClose() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = open.plusSeconds(pauseDelay);
        return Duration.between(now, future).toSeconds();
    }

    public long getTimeUntilOpen() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = lastOpen.plusSeconds(delay);
        return Duration.between(now, future).toSeconds();
    }

    private void setLastOpen(LocalDateTime lastOpen) {
        this.lastOpen = lastOpen;
    }

    public void setLocation(Location location) {
        try {
            this.location = location;
            configFile.getConfig().set("chests." + getKey() + ".location", LocationUtil.locationToString(location));
            CultPvPChests.getInstance().getConfigFile().reload();
        } catch (Exception e) {
            CultPvPChests.getInstance().getLogger().severe("Произошла ошибка при установке локации для сундука " + key);
        }
    }

    public Map<ItemStack, Double> getLoot() {
        return loot;
    }

    public void setLoot(HashMap<ItemStack, Double> loot) {
        this.loot = loot;
    }

    public void addItem(ItemStack itemStack, Double chance) {
        try {
            String uniqueKey = UUID.randomUUID().toString();
            this.loot.putIfAbsent(itemStack, chance);
            configFile.getConfig().set("chests." + key + ".loot." + uniqueKey + ".data", itemStack.serialize());
            configFile.reload();
        } catch (Exception e) {
            CultPvPChests.getInstance().getLogger().severe("Произошла ошибка при добавлении материала " + itemStack.getType() + " с шансом  " + chance + " для сундука " + key);
        }
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        try {
            this.delay = delay;
            configFile.getConfig().set("chests." + key + ".delay", TimeUtil.parseTimeToString(delay));
            configFile.reload();
        } catch (Exception e) {
            CultPvPChests.getInstance().getLogger().severe("Произошла ошибка при установке времени открытия  " + delay + " для сундука " + key);
        }
    }

    public List<String> getUnlockedBroadcast() {
        return unlockedBroadcast;
    }

    public void setUnlockedBroadcast(List<String> unlockedBroadcast) {
        try {
            this.unlockedBroadcast = unlockedBroadcast;
            configFile.getConfig().set("chests." + key + ".message", unlockedBroadcast);
            configFile.reload();
        } catch (Exception e) {
            CultPvPChests.getInstance().getLogger().severe("Произошла ошибка при установке сообщения при открытии  " + unlockedBroadcast + " для сундука " + key);
        }
    }

    public boolean isOpenable() {
        return isOpenable;
    }

    public void close() {
        setOpenable(false);
        this.lastOpen = LocalDateTime.now();
    }

    public void open() {
        setOpenable(true);
        this.open = LocalDateTime.now();
    }

    public void setOpenable(boolean isOpenable) {
        this.isOpenable = isOpenable;
    }

    public int getMinOnlinePlayers() {
        return minOnlinePlayers;
    }

    public void setMinOnlinePlayers(int minOnlinePlayers) {
        try {
            this.minOnlinePlayers = minOnlinePlayers;
            configFile.getConfig().set("chests." + key + ".minOnlinePlayers", unlockedBroadcast);
            configFile.reload();
        } catch (Exception e) {
            CultPvPChests.getInstance().getLogger().severe("Произошла ошибка при установке минимального онлайна  " + minOnlinePlayers + " для сундука " + key);
        }
    }

    public Location getLocation() {
        return location;
    }

    public String getKey() {
        return key;
    }

    public List<String> getHologramLines() {
        return hologramLines;
    }

    public void setHologramLines(List<String> hologramLines) {
        try {
            this.hologramLines = hologramLines;
            configFile.getConfig().set("chests." + key + ".hologram-lines", hologramLines);
            configFile.reload();
        } catch (Exception e) {
            CultPvPChests.getInstance().getLogger().severe("Произошла ошибка при установке текста голограммы " + unlockedBroadcast + " для сундука " + key);
        }
    }

    public double getItemSlotChance() {
        return itemSlotChance;
    }

    public void setItemSlotChance(double itemSlotChance) {
        try {
            this.itemSlotChance = itemSlotChance;
            configFile.getConfig().set("chests." + key + ".item-slot-chance", itemSlotChance);
            configFile.reload();
        } catch (Exception e) {
            CultPvPChests.getInstance().getLogger().severe("Произошла ошибка при установке вероятности лута в слоте " + unlockedBroadcast + " для сундука " + key);
        }
    }

    public void setPauseDelay(long delay) {
        try {
            this.pauseDelay = delay;
            configFile.getConfig().set("chests." + key + ".pause-delay", TimeUtil.parseTimeToString(pauseDelay));
            configFile.reload();
        } catch (Exception e) {
            CultPvPChests.getInstance().getLogger().severe("Произошла ошибка при установке задержки перед закрытием в слоте " + unlockedBroadcast + " для сундука " + key);
        }
    }

    public long getPauseDelay() {
        return pauseDelay;
    }
}
