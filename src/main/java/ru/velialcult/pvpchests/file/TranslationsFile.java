package ru.velialcult.pvpchests.file;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.TippedArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import ru.velialcult.library.bukkit.file.FileRepository;
import ru.velialcult.pvpchests.CultPvPChests;

public class TranslationsFile {

    private static final FileConfiguration config = FileRepository.getByName(CultPvPChests.getInstance(), "translation.yml").getConfiguration();

    private static String getTranslation(String path) {
        if (!config.contains(path)) {
            return "&cПеревод не найден";
        } else {
            return config.getString(path);
        }
    }

    public static String getTranslationItem(ItemStack itemStack) {
        Material material = itemStack.getType();
        if (material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION || material == Material.TIPPED_ARROW) {
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            PotionData potionData = potionMeta.getBasePotionData();
            return getTranslation("items." + material.toString() + ".EFFECT." + potionData.getType());
        } else if (material == Material.SHIELD) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof BlockStateMeta) {
                BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
                if (blockStateMeta.getBlockState() instanceof Banner) {
                    Banner banner = (Banner) blockStateMeta.getBlockState();
                    DyeColor color = banner.getBaseColor();
                    return getTranslation("items." + material.toString() + "." + color.name());
                }
            }
        }
        return getTranslation("items." + material);
    }

    public static String getTranslationEffect(PotionEffectType effectType) {
        return getTranslation("effects." + effectType.getName());
    }
}
