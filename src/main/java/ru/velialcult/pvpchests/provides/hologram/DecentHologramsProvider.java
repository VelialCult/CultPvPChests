package ru.velialcult.pvpchests.provides.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.pvpchests.CultPvPChests;
import ru.velialcult.pvpchests.provides.wrapper.HologramWrapper;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Nicholas Alexandrov 16.07.2023
 */
public class DecentHologramsProvider implements HologramProvider {

    @Override
    public void createHologram(Location location, List<String> lines, String name, Consumer<HologramWrapper> callback) {
        Hologram[] hologram = new Hologram[1];
        Bukkit.getScheduler().runTask(CultPvPChests.getInstance(), () -> {
            hologram[0] = DHAPI.createHologram(name, location, false);
            for (String line : lines) {
                DHAPI.addHologramLine(hologram[0], VersionAdapter.TextUtil().colorize(line));
            }
            callback.accept(new HologramWrapper(hologram[0]));
        });
    }
}
