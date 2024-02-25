package ru.velialcult.pvpchests.update;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckUpdateManager {

    private final Plugin plugin;
    private static final String REPO = "VelialCult/CultPvPChests";

    public CheckUpdateManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void checkUpdates() {
        try {
            String currentVersion = plugin.getDescription().getVersion();
            String latestVersion = getLatestVersion();
            if (currentVersion.equals(latestVersion)) {
                plugin.getLogger().info("Используется последняя версия плагина: " + latestVersion);
            } else {
                plugin.getLogger().warning("Доступна новая версия плагина: " + latestVersion);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Произошла ошибка при проверке плагина на обновления: " + e.getMessage());
        }
    }

    private static String getLatestVersion() throws IOException {
        URL url = new URL("https://api.github.com/repos/" + REPO + "/releases/latest");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        InputStream is = connection.getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(is);

        return jsonNode.get("tag_name").asText();
    }
}
