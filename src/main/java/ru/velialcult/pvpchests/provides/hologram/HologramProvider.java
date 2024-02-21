package ru.velialcult.pvpchests.provides.hologram;

import org.bukkit.Location;
import ru.velialcult.pvpchests.provides.wrapper.HologramWrapper;

import java.util.List;
import java.util.function.Consumer;

public interface HologramProvider {

    void createHologram(Location location, List<String> lines, String name, Consumer<HologramWrapper> callback) throws Exception;
}
