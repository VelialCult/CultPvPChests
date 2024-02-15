package ru.velialcult.pvpchests.provides.wrapper;

import eu.decentsoftware.holograms.api.DHAPI;
import ru.velialcult.pvpchests.CultPvPChests;
import ru.velialcult.pvpchests.provides.hologram.DecentHologramsProvider;
import ru.velialcult.pvpchests.provides.hologram.HologramProvider;
import ru.velialcult.pvpchests.provides.hologram.HolographicDisplaysProvider;

import java.util.List;

/**
 * @author Nicholas Alexandrov 16.07.2023
 */
public class HologramWrapper {

    private final Object hologram;

    public HologramWrapper(Object hologram) {
        this.hologram = hologram;
    }

    public void delete() {
        HologramProvider provider = CultPvPChests.getInstance().getProvidersManager().getHologramProvider();
        if (provider instanceof DecentHologramsProvider && hologram instanceof eu.decentsoftware.holograms.api.holograms.Hologram) {
            DHAPI.removeHologram(((eu.decentsoftware.holograms.api.holograms.Hologram) hologram).getName());
        } else if (provider instanceof HolographicDisplaysProvider && hologram instanceof me.filoghost.holographicdisplays.api.hologram.Hologram) {
            ((me.filoghost.holographicdisplays.api.hologram.Hologram) hologram).delete();
        }
    }

    public void updateLines(List<String> lines) {
        HologramProvider provider = CultPvPChests.getInstance().getProvidersManager().getHologramProvider();
        if (provider instanceof DecentHologramsProvider && hologram instanceof eu.decentsoftware.holograms.api.holograms.Hologram) {
            eu.decentsoftware.holograms.api.holograms.Hologram decentHolo = (eu.decentsoftware.holograms.api.holograms.Hologram) hologram;
            DHAPI.removeHologram(decentHolo.getName());
            DHAPI.createHologram(decentHolo.getName(), decentHolo.getLocation(), lines);
        } else if (provider instanceof HolographicDisplaysProvider && hologram instanceof me.filoghost.holographicdisplays.api.hologram.Hologram) {
            me.filoghost.holographicdisplays.api.hologram.Hologram hdHolo = (me.filoghost.holographicdisplays.api.hologram.Hologram) hologram;
            hdHolo.getLines().clear();
            for (String line : lines) {
                hdHolo.getLines().appendText(line);
            }
        }
    }
}
