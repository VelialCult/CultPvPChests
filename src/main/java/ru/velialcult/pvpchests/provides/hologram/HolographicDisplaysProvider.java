package ru.velialcult.pvpchests.provides.hologram;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import org.bukkit.Location;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.pvpchests.CultPvPChests;
import ru.velialcult.pvpchests.provides.wrapper.HologramWrapper;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Nicholas Alexandrov 16.07.2023
 */
public class HolographicDisplaysProvider implements HologramProvider {

    private final HolographicDisplaysAPI api;

    {
        this.api = HolographicDisplaysAPI.get(CultPvPChests.getInstance());
    }


    @Override
    public void createHologram(Location location, List<String> lines, String name, Consumer<HologramWrapper> callback) {
        Hologram hologram = api.createHologram(location);
        HologramLines hologramLines = hologram.getLines();
        lines.replaceAll(string -> VersionAdapter.TextUtil().colorize(string));
        lines.forEach(hologramLines::appendText);
        callback.accept(new HologramWrapper(hologram));
    }
}
