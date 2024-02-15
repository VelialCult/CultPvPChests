package ru.velialcult.pvpchests;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import ru.velialcult.library.bukkit.utils.location.LocationUtil;
import ru.velialcult.library.java.utils.TimeUtil;
import ru.velialcult.pvpchests.file.ConfigFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chest {

    private final ConfigFile configFile = CultPvPChests.getInstance().getConfigFile();

    private LocalDateTime lastOpen;

    private double itemSlotChance;
    private final String key;
    private List<String> hologramLines;
    private Map<ItemStack, Double> loot;
    private Location location;
    private long delay;
    private List<String> message;
    private boolean isOpenable;
    private int minOnlinePlayers;

    public Chest(String key, Location location,  long timer, int minOnlinePlayers) {
        this(key, location, timer, new ArrayList<>(), minOnlinePlayers);
    }

    public Chest(String key, Location location, long timer, List<String> message, int minOnlinePlayers) {
        this(key, location, timer, message, minOnlinePlayers, new HashMap<>(),  new ArrayList<>(), 0.25);
    }

    public Chest(String key,
                 Location location,
                 long timer,
                 List<String> message,
                 int minOnlinePlayers,
                 Map<ItemStack, Double> loot,
                 List<String> hologramLines,
                 double itemSlotChance) {
        this.location = location;
        this.key = key;
        this.delay = timer;
        this.message = message;
        this.minOnlinePlayers = minOnlinePlayers;
        this.loot = loot;
        this.isOpenable = false;
        this.hologramLines = hologramLines;
        this.itemSlotChance = itemSlotChance;
        this.lastOpen = LocalDateTime.now();
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
            this.loot.putIfAbsent(itemStack, chance);
            configFile.getConfig().set("chests." + key + ".loot." + itemStack.getType() + ".data", itemStack.serialize());
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

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        try {
            this.message = message;
            configFile.getConfig().set("chests." + key + ".message", message);
            configFile.reload();
        } catch (Exception e) {
            CultPvPChests.getInstance().getLogger().severe("Произошла ошибка при установке сообщения при открытии  " + message + " для сундука " + key);
        }
    }

    public boolean isOpenable() {
        return isOpenable;
    }

    public void setOpenable(boolean isOpenable) {
        if (!isOpenable) {
            this.lastOpen = LocalDateTime.now();
        }
        this.isOpenable = isOpenable;
    }

    public int getMinOnlinePlayers() {
        return minOnlinePlayers;
    }

    public void setMinOnlinePlayers(int minOnlinePlayers) {
        try {
            this.minOnlinePlayers = minOnlinePlayers;
            configFile.getConfig().set("chests." + key + ".minOnlinePlayers", message);
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
            CultPvPChests.getInstance().getLogger().severe("Произошла ошибка при установке текста голограммы " + message + " для сундука " + key);
        }
    }

    public double getItemSlotChance() {
        return itemSlotChance;
    }
}
