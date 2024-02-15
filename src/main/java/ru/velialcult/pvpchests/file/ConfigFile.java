package ru.velialcult.pvpchests.file;

import org.bukkit.configuration.file.FileConfiguration;
import ru.velialcult.library.bukkit.utils.ConfigurationUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.pvpchests.CultPvPChests;

public class ConfigFile {

    private FileConfiguration config;

    private final CultPvPChests cultPvPChests;

    private String openNow;

    public ConfigFile(CultPvPChests cultPvPChests) {
        this.cultPvPChests = cultPvPChests;
    }

    public void load() {
        config = CultPvPChests.getInstance().getConfig();
        this.openNow = VersionAdapter.TextUtil().colorize(config.getString("settings.time.now"));
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void save() {
        ConfigurationUtil.saveFile(config, cultPvPChests.getDataFolder().getAbsolutePath(), "config.yml");
    }

    public void reload() {
        ConfigurationUtil.saveFile(config, cultPvPChests.getDataFolder().getAbsolutePath(), "config.yml");
        ConfigurationUtil.loadConfigurations(cultPvPChests, "config.yml");
        load();
    }

    public String getOpenNow() {
        return openNow;
    }
}
