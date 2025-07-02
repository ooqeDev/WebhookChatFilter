package com.ooqe.webhookChatFilter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebhookChatFilter extends JavaPlugin {

    private final Set<String> blockedWords = new HashSet<>();
    private File blockedWordsFile;
    private FileConfiguration blockedWordsConfig;
    private static final String WebhookURL = "WEBHOOK LINK HERE";

    @Override
    public void onEnable() {

        getLogger().info("WebhookChatFilter Enabled!");
        createBlockedWordsFile();
        blockedWords.addAll(blockedWordsConfig.getStringList("words"));
        getCommand("blockword").setExecutor(new BlockwordCommand(this));
        getCommand("blockword").setTabCompleter(new BlockwordTabCompleter());
        getCommand("unblockword").setExecutor(new UnblockwordCommand(this));
        getCommand("unblockword").setTabCompleter(new BlockwordTabCompleter());
        getServer().getPluginManager().registerEvents(new ChatFilter(this), this);

    }

    @Override
    public void onDisable() {

        getLogger().info("WebhookChatFilter Disabled!");
        saveBlockedWords();

    }

    private void createBlockedWordsFile() {

        blockedWordsFile = new File(getServer().getWorldContainer(), "blocked_words.yml");

        if (!blockedWordsFile.exists()) {

            try {

                blockedWordsFile.createNewFile();
                YamlConfiguration defaultConfig = new YamlConfiguration();
                defaultConfig.set("words", List.of());
                defaultConfig.save(blockedWordsFile);

            }

            catch (IOException e) {

                e.printStackTrace();

            }
        }

        blockedWordsConfig = YamlConfiguration.loadConfiguration(blockedWordsFile);

    }

    public void saveBlockedWords() {

        blockedWordsConfig.set("words", blockedWords.stream().toList());

        try {

            blockedWordsConfig.save(blockedWordsFile);

        }

        catch (IOException e) {

            e.printStackTrace();

        }
    }

    public void addBlockedWord(String word) {

        blockedWords.add(word.toLowerCase());
        saveBlockedWords();

    }

    public boolean removeBlockedWord(String word) {

        boolean removed = blockedWords.remove(word.toLowerCase());
        if (removed) saveBlockedWords();
        return removed;

    }

    public Set<String> getBlockedWords() {

        return blockedWords;

    }
    private class BlockwordTabCompleter implements TabCompleter {

        @Override

        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

            return Collections.emptyList();


        }
    }
        public void sendBlockedMessageToDiscord(String playerName, String message) {

            try {

                String json = """
        {
          "embeds": [
            {
              "title": "Webhook ChatFilter",
              "color": 16711680,
              "fields": [
                { "name": "Player", "value": "%s", "inline": true },
                { "name": "Message", "value": "%s" }
              ],
              "timestamp": "%s"
            }
          ]
        }
        """.formatted(

                        playerName,
                        message.replace("\"", "\\\""),
                        java.time.Instant.now().toString()

                );

                java.net.URL url = new java.net.URL(WebhookURL);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                try (java.io.OutputStream os = connection.getOutputStream()) {

                    byte[] input = json.getBytes("utf-8");
                    os.write(input, 0, input.length);

                }

                connection.getInputStream().close();

            }

            catch (Exception e) {

                getLogger().warning("Failed to send message to Discord: " + e.getMessage());

            }
        }
    }
