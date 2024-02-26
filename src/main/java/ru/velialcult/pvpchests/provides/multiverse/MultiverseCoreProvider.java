package ru.velialcult.pvpchests.provides.multiverse;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import ru.velialcult.pvpchests.CultPvPChests;
import ru.velialcult.pvpchests.provides.ProvidersManager;

public class MultiverseCoreProvider {

    private final ProvidersManager providersManager;
    private final CultPvPChests cultPvPChests;

    public MultiverseCoreProvider(CultPvPChests cultPvPChests) {
        this.cultPvPChests = cultPvPChests;
        this.providersManager = cultPvPChests.getProvidersManager();
    }

    public World getWorld(String worldName) {
        World world = null;
        try {
            if (providersManager.useMultiverseCore()) {
                MultiverseCore multiverse = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
                world =  multiverse.getMVWorldManager().getMVWorld(worldName).getCBWorld();
            }
        } catch (Exception e) {
            cultPvPChests.getLogger().severe("Произошла ошибка при получении мира через MultiVerse-Core: " + e.getMessage());
        }

        return world;
    }
}
