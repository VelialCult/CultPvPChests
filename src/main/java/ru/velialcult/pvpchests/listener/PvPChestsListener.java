package ru.velialcult.pvpchests.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.velialcult.pvpchests.Chest;
import ru.velialcult.pvpchests.manager.ChestManager;

import javax.swing.plaf.synth.SynthUI;

public class PvPChestsListener implements Listener {

    private final ChestManager chestManager;

    public PvPChestsListener(ChestManager chestManager) {
        this.chestManager = chestManager;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {;
        Block block = e.getClickedBlock();
        if (block != null) {
            Location location = block.getLocation();
            Chest chest = chestManager.getChestByLocation(location);
            if (chest != null) {
                if (!chest.isOpenable()) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
