package ru.velialcult.pvpchests.provides;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ru.velialcult.pvpchests.CultPvPChests;
import ru.velialcult.pvpchests.provides.hologram.DecentHologramsProvider;
import ru.velialcult.pvpchests.provides.hologram.HologramProvider;
import ru.velialcult.pvpchests.provides.hologram.HolographicDisplaysProvider;
import ru.velialcult.pvpchests.provides.multiverse.MultiverseCoreProvider;

import java.util.HashMap;
import java.util.Map;

public class ProvidersManager {

    private final Map<String, Boolean> providers = new HashMap<>();
    private final Plugin plugin;

    public ProvidersManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        loadProvider("DecentHolograms", "2.8.6");
        loadProvider("HolographicDisplays", "3.0.0");
        loadProvider("Multiverse-Core", "4.3.1");
    }

    private void loadProvider(String pluginName, String minVersion) {
        boolean isPluginLoaded = false;
        if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            String version = plugin.getDescription().getVersion();

            if (version.compareTo(minVersion) >= 0) {
                this.plugin.getLogger().info(pluginName + " найден, использую " + pluginName + " API");
                isPluginLoaded = true;
            } else {
                this.plugin.getLogger().warning("Версия " + pluginName + " < " + minVersion + " не поддерживается. Игнорирую данную зависимость");
            }
        }
        providers.put(pluginName, isPluginLoaded);
    }

    public boolean useMultiverseCore() {
        return providers.getOrDefault("Multiverse-Core", false);
    }

    public boolean useHologramAPI() {
        return providers.getOrDefault("DecentHolograms", false) || providers.getOrDefault("HolographicDisplays", false);
    }

    public HologramProvider getHologramProvider() {
        boolean useDecent = providers.getOrDefault("DecentHolograms", false);
        boolean useHolographic = providers.getOrDefault("HolographicDisplays", false);

        if (useDecent && useHolographic) {
            return new DecentHologramsProvider();

        } else if (useDecent) {
            return new DecentHologramsProvider();

        } else if (useHolographic) {
            return new HolographicDisplaysProvider();

        } else {
            return null;
        }
    }

    public MultiverseCoreProvider getMultiVerseProvider() {
        boolean use = providers.getOrDefault("Multiverse-Core", false);
        if (use) {
            return new MultiverseCoreProvider(CultPvPChests.getInstance());
        }

        return null;
    }
}
