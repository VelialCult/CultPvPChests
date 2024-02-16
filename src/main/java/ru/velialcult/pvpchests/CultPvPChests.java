package ru.velialcult.pvpchests;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.velialcult.library.bukkit.utils.ConfigurationUtil;
import ru.velialcult.pvpchests.command.CultPvPChestsCommand;
import ru.velialcult.pvpchests.file.ConfigFile;
import ru.velialcult.pvpchests.listener.PvPChestsListener;
import ru.velialcult.pvpchests.manager.ChestManager;
import ru.velialcult.pvpchests.manager.HologramManager;
import ru.velialcult.pvpchests.manager.LootChestManager;
import ru.velialcult.pvpchests.provides.ProvidersManager;
import ru.velialcult.pvpchests.task.ChestOpenTask;

public class CultPvPChests extends JavaPlugin {

    private static CultPvPChests instance;

    private ConfigFile configFile;
    private ChestManager chestManager;
    private LootChestManager lootChestManager;
    private ProvidersManager providersManager;
    private HologramManager hologramManager;

    @Override
    public void onEnable() {
        instance = this;

        try {

            providersManager = new ProvidersManager(this);
            providersManager.load();

            this.saveDefaultConfig();
            configFile = new ConfigFile(this);
            configFile.load();
            ConfigurationUtil.loadConfigurations(this, "translation.yml");

            lootChestManager = new LootChestManager();

            chestManager = new ChestManager(this);
            chestManager.loadChests();

            hologramManager = new HologramManager(this);
            hologramManager.createHolograms();
            hologramManager.startUpdate();

            Bukkit.getPluginCommand("cultpvpchests").setExecutor(new CultPvPChestsCommand(this));

            ChestOpenTask chestOpenTask = new ChestOpenTask(chestManager);
            chestOpenTask.runTaskTimer(this, 0L, 20L);

            Bukkit.getPluginManager().registerEvents(new PvPChestsListener(chestManager), this);
        } catch (Exception e) {
            getLogger().severe("Произошла ошибка при инициализации плагина: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        chestManager.saveChests();
    }

    public static CultPvPChests getInstance() {
        return instance;
    }

    public ConfigFile getConfigFile() {
        return configFile;
    }

    public ChestManager getChestManager() {
        return chestManager;
    }

    public LootChestManager getLootChestManager() {
        return lootChestManager;
    }

    public ProvidersManager getProvidersManager() {
        return providersManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }
}
