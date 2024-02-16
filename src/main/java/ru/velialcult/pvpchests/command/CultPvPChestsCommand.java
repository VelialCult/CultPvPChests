package ru.velialcult.pvpchests.command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.velialcult.library.bukkit.utils.PlayerUtil;
import ru.velialcult.library.bukkit.utils.location.LocationUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import ru.velialcult.library.java.utils.TimeUtil;
import ru.velialcult.pvpchests.Chest;
import ru.velialcult.pvpchests.CultPvPChests;
import ru.velialcult.pvpchests.file.ConfigFile;
import ru.velialcult.pvpchests.file.TranslationsFile;
import ru.velialcult.pvpchests.manager.ChestManager;
import ru.velialcult.pvpchests.manager.HologramManager;

import java.util.ArrayList;
import java.util.List;

public class CultPvPChestsCommand implements CommandExecutor {

    private final ConfigFile configFile;
    private final ChestManager chestManager;
    private final HologramManager hologramManager;

    public CultPvPChestsCommand(CultPvPChests cultPvPChests) {
        this.configFile = cultPvPChests.getConfigFile();
        this.chestManager = cultPvPChests.getChestManager();
        this.hologramManager = cultPvPChests.getHologramManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("cultpvpchests.admin")) {
            if (args.length == 0) {
                VersionAdapter.MessageUtils().sendMessage(sender, configFile.getList("messages.commands.help"));
                return true;
            } else {
                String cmd = args[0];
                switch (cmd) {
                    case "create": {
                        if (PlayerUtil.senderIsPlayer(sender)) {
                            Player player = (Player) sender;
                            if (args.length != 5) {
                                VersionAdapter.MessageUtils().sendMessage(player, configFile.getString("messages.commands.create.usage"));
                                return true;
                            }

                            String key = args[1];

                            if (chestManager.chestNameIsExists(key)) {
                                VersionAdapter.MessageUtils().sendMessage(player, configFile.getString("messages.commands.create.key-already-exists"));
                                return true;
                            }

                            long timer = 0;

                            try {
                                timer = TimeUtil.parseStringToTime(args[2]);

                            } catch (Exception e) {
                                VersionAdapter.MessageUtils().sendMessage(player, configFile.getString("messages.commands.wrong-value",
                                        new ReplaceData("{value}", args[2])));
                            }

                            int minOnline = 0;

                            try {
                                minOnline = Integer.parseInt(args[3]);

                            } catch (Exception e) {
                                VersionAdapter.MessageUtils().sendMessage(player, configFile.getString("messages.commands.wrong-value",
                                        new ReplaceData("{value}", args[3])));
                            }

                            long pauseDelay = 0;

                            try {
                                pauseDelay = TimeUtil.parseStringToTime(args[4]);

                            } catch (Exception e) {
                                VersionAdapter.MessageUtils().sendMessage(player, configFile.getString("messages.commands.wrong-value",
                                        new ReplaceData("{value}", args[4])));
                            }

                            Block block = player.getTargetBlockExact(1);

                            if (block == null) {
                                VersionAdapter.MessageUtils().sendMessage(player, "messages.commands.block-is-null");
                                return true;
                            }

                            Location location = block.getLocation();

                            Chest chest = new Chest(key, location, timer, pauseDelay, minOnline);
                            chestManager.addChest(chest);
                            hologramManager.createHologram(chest);
                            VersionAdapter.MessageUtils().sendMessage(player, "messages.commands.create.create");
                        }
                        break;
                    }
                    case "set-location": {
                        if (PlayerUtil.senderIsPlayer(sender)) {
                            Player player = (Player) sender;
                            if (args.length != 3) {
                                VersionAdapter.MessageUtils().sendMessage(player, configFile.getString("messages.commands.set-location.usage"));
                                return true;
                            }

                            String key = args[2];

                            if (!chestManager.chestNameIsExists(key)) {
                                VersionAdapter.MessageUtils().sendMessage(player, configFile.getString("messages.commands.not-exists"));
                                return true;
                            }

                            Chest chest = chestManager.getChestById(key);

                            Block block = player.getTargetBlockExact(1);

                            if (block == null) {
                                VersionAdapter.MessageUtils().sendMessage(player, "messages.commands.block-is-null");
                                return true;
                            }

                            Location blockLocation = block.getLocation();
                            chest.setLocation(blockLocation);
                            VersionAdapter.MessageUtils().sendMessage(player, configFile.getString("messages.commands.set-location.set",
                                    new ReplaceData("{location}", LocationUtil.locationToString(blockLocation))));
                        }
                        break;
                    }
                    case "add-item": {
                        if (PlayerUtil.senderIsPlayer(sender)) {
                            Player player = (Player) sender;
                            if (args.length != 3) {
                                VersionAdapter.MessageUtils().sendMessage(player, configFile.getString("messages.commands.add-item.usage"));
                                return true;
                            }

                            String key = args[1];

                            if (!chestManager.chestNameIsExists(key)) {
                                VersionAdapter.MessageUtils().sendMessage(player, configFile.getString("messages.commands.not-exists"));
                                return true;
                            }

                            Chest chest = chestManager.getChestById(key);

                            double chance = 0.0D;

                            try {
                                chance = Double.parseDouble(args[2]);
                            } catch (Exception e) {
                                VersionAdapter.MessageUtils().sendMessage(player, configFile.getString("messages.commands.wrong-value",
                                        new ReplaceData("{value}", args[2])));
                            }

                            ItemStack itemStack = player.getInventory().getItemInMainHand();

                            if (itemStack.getType() == Material.AIR) {
                                VersionAdapter.MessageUtils().sendMessage(player, configFile.getString("messages.commands.add-item.material-is-null"));
                                return true;
                            }
                            chest.addItem(itemStack, chance);
                            VersionAdapter.MessageUtils().sendMessage(player, configFile.getString("messages.commands.add-item.add",
                                    new ReplaceData("{item}", TranslationsFile.getTranslationItem(itemStack.getType())),
                                    new ReplaceData("{chance}", chance)));
                        }
                        break;
                    }
                    case "set-delay": {
                        if (args.length != 3) {
                            VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.set-delay.usage"));
                            return true;
                        }

                        String key = args[1];

                        if (!chestManager.chestNameIsExists(key)) {
                            VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.not-exists"));
                            return true;
                        }

                        Chest chest = chestManager.getChestById(key);

                        long delay = 0;

                        try {
                            delay = TimeUtil.parseStringToTime(args[2]);

                        } catch (Exception e) {
                            VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.wrong-value",
                                    new ReplaceData("{value}", args[2])));
                        }

                        chest.setDelay(delay);
                        VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.set-delay.set",
                                new ReplaceData("{delay}", TimeUtil.getTime(delay))));
                        break;
                    }
                    case "set-min-online": {
                        if (args.length != 3) {
                            VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.set-min-online.usage"));
                            return true;
                        }

                        String key = args[1];

                        if (!chestManager.chestNameIsExists(key)) {
                            VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.not-exists"));
                            return true;
                        }

                        Chest chest = chestManager.getChestById(key);

                        int minOnline = 0;

                        try {
                            minOnline = Integer.parseInt(args[2]);

                        } catch (Exception e) {
                            VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.wrong-value",
                                    new ReplaceData("{value}", args[2])));
                        }

                        chest.setMinOnlinePlayers(minOnline);
                        VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.set-min-online.set",
                                new ReplaceData("{online}", minOnline)));
                        break;
                    }
                    case "set-hologram": {
                        if (args.length > 2) {

                            String key = args[1];

                            if (!chestManager.chestNameIsExists(key)) {
                                VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.not-exists"));
                                return true;
                            }

                            Chest chest = chestManager.getChestById(key);


                            List<String> messageLines = getStrings(args);
                            chest.setHologramLines(messageLines);
                            VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.set-hologram.set"));
                            break;
                        } else {
                            VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.set-hologram.usage"));
                        }
                        break;
                    }
                    case "set-message": {
                        if (args.length > 2) {

                            String key = args[1];

                            if (!chestManager.chestNameIsExists(key)) {
                                VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.not-exists"));
                                return true;
                            }

                            Chest chest = chestManager.getChestById(key);


                            List<String> messageLines = getStrings(args);
                            chest.setUnlockedBroadcast(messageLines);
                            VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.set-message.set"));
                            break;
                        } else {
                            VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.set-message.usage"));
                        }
                        break;
                    }
                    case "set-item-chance": {
                        if (args.length != 3) {
                            VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.set-item-chance.usage"));
                            return true;
                        }

                        String key = args[1];

                        if (!chestManager.chestNameIsExists(key)) {
                            VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.not-exists"));
                            return true;
                        }

                        Chest chest = chestManager.getChestById(key);

                        double itemChance = 0;

                        try {
                            itemChance = Double.parseDouble(args[2]);

                        } catch (Exception e) {
                            VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.wrong-value",
                                    new ReplaceData("{value}", args[2])));
                        }

                        chest.setItemSlotChance(itemChance);
                        VersionAdapter.MessageUtils().sendMessage(sender, configFile.getString("messages.commands.set-item-chance.set",
                                new ReplaceData("{chance}", itemChance)));
                        break;
                    }
                    default: {
                        VersionAdapter.MessageUtils().sendMessage(sender, configFile.getList("messages.commands.help"));
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @NotNull
    private static List<String> getStrings(String[] args) {
        List<String> messageLines = new ArrayList<>();

        StringBuilder lineBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            if (args[i].contains("\\n")) {
                lineBuilder.append(args[i].replace("\\n", ""));
                messageLines.add(lineBuilder.toString());
                lineBuilder = new StringBuilder();
            } else {
                lineBuilder.append(args[i]);
                if (i != args.length - 1) {
                    lineBuilder.append(" ");
                }
            }
        }
        // Добавляем последнюю строку в список
        if (lineBuilder.length() > 0) {
            messageLines.add(lineBuilder.toString());
        }
        return messageLines;
    }
}

