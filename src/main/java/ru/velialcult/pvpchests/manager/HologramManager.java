package ru.velialcult.pvpchests.manager;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import ru.velialcult.library.bukkit.utils.location.LocationUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import ru.velialcult.pvpchests.Chest;
import ru.velialcult.pvpchests.CultPvPChests;
import ru.velialcult.pvpchests.provides.ProvidersManager;
import ru.velialcult.pvpchests.provides.wrapper.HologramWrapper;

import java.util.*;

public class HologramManager {

    private final CultPvPChests plugin;
    private final ProvidersManager providersManager;
    private final ChestManager chestManager;
    private final Map<Chest, HologramWrapper> hologramWrappers;

    public HologramManager(CultPvPChests plugin) {
        this.plugin = plugin;
        this.providersManager = plugin.getProvidersManager();
        this.chestManager = plugin.getChestManager();
        this.hologramWrappers = new HashMap<>();
    }

    private HologramWrapper getHologramByChest(Chest chest) {
        return this.hologramWrappers.getOrDefault(chest, null);
    }

    public void createHolograms() {
        if (providersManager.useHologramAPI()) {
            for (Chest chest : chestManager.getChests()) {
                createHologram(chest);
            }
        }
    }

    public void createHologram(Chest chest) {
        if (providersManager.useHologramAPI()) {
            Location hologramLocation = chest.getLocation().clone().add(0.5, 2, 0.5);
            providersManager.getHologramProvider().createHologram(hologramLocation, chest.getHologramLines(), chest.getKey(), callBack -> addHologram(chest, callBack));
        }
    }

    public void deleteHologram(Chest chest) {
        if (providersManager.useHologramAPI()) {
            HologramWrapper hologramWrapper = getHologramByChest(chest);
            if (hologramWrapper != null) {
                this.hologramWrappers.remove(chest);
                hologramWrapper.delete();
            }
        }
    }

    public void startUpdate() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Map.Entry<Chest, HologramWrapper> entry : hologramWrappers.entrySet()) {
                    Chest chest = entry.getKey();
                    HologramWrapper hologramWrapper = entry.getValue();
                    hologramWrapper.updateLines(VersionAdapter.TextUtil().setReplaces(chest.getHologramLines(),
                            new ReplaceData("{status}", chestManager.getTimeUntilOpen(chest))));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void addHologram(Chest chest, HologramWrapper hologramWrapper) {
        this.hologramWrappers.putIfAbsent(chest, hologramWrapper);
    }
}
