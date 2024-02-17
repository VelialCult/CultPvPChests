package ru.velialcult.pvpchests.task;

import org.bukkit.scheduler.BukkitRunnable;
import ru.velialcult.pvpchests.Chest;
import ru.velialcult.pvpchests.manager.ChestManager;

public class ChestOpenTask extends BukkitRunnable {

    private final ChestManager chestManager;

    public ChestOpenTask(ChestManager chestManager) {
        this.chestManager = chestManager;
    }

    @Override
    public void run() {
        for (Chest chest : chestManager.getChests()) {
            if (!chest.isOpenable()) {
                if (chest.getTimeUntilOpen() <= 0) {
                    chestManager.openChest(chest);
                }
            } else {
                if (chest.getTimeUntilClose() <= 0) {
                    chest.close();
                }
            }
        }
    }
}
