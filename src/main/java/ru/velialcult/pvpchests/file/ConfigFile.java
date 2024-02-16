package ru.velialcult.pvpchests.file;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ru.velialcult.library.bukkit.utils.ConfigurationUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import ru.velialcult.pvpchests.CultPvPChests;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigFile {

    private final Map<String, String> strings;

    private FileConfiguration config;

    private final CultPvPChests cultPvPChests;

    private String minOnline;
    private String locked;
    private String unlocked;

    public ConfigFile(CultPvPChests cultPvPChests) {
        this.cultPvPChests = cultPvPChests;
        this.strings = new HashMap<>();
    }

    public List<String> getList(String path, ReplaceData... replacesData) {
        String value = strings.get(path);

        if (value == null) {
            return Arrays.asList("&cНе удалось получить сообщение из конфиугурации.");
        }

        List<String> result = Arrays.stream(value.split("\n"))
                .map(line -> line.replace("[", "").replace("]", "").replace("\n", ""))
                .collect(Collectors.toList());

        result = VersionAdapter.TextUtil().setReplaces(result, replacesData);
        return VersionAdapter.TextUtil().colorize(result);
    }

    public String getString(String path, ReplaceData... replacesData) {
        String value = strings.get(path);

        if (value == null) {
            return "&cНе удалось получить сообщение из конфигурации.";
        }

        value = VersionAdapter.TextUtil().setReplaces(value, replacesData);
        return VersionAdapter.TextUtil().colorize(value);
    }

    public void load() {
        strings.clear();
        config = CultPvPChests.getInstance().getConfig();
        this.minOnline = VersionAdapter.TextUtil().colorize(config.getString("settings.hologram.lines.min-online"));
        this.locked = VersionAdapter.TextUtil().colorize(config.getString("settings.hologram.lines.locked"));
        this.unlocked = VersionAdapter.TextUtil().colorize(config.getString("settings.hologram.lines.unlocked"));
        ConfigurationSection section = config.getConfigurationSection("messages");
        Objects.requireNonNull(section).getKeys(true).forEach(path -> {

            String fullPath = "messages." + path;
            if (config.isList(fullPath)) {
                List<String> lines = config.getStringList(fullPath);
                strings.put(fullPath, String.join("\n", lines));
            } else {
                String value =  config.getString(fullPath);
                strings.put(fullPath, value);
            }
        });
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

    public String getLocked() {
        return locked;
    }

    public String getMinOnline() {
        return minOnline;
    }

    public String getUnlocked() {
        return unlocked;
    }
}
