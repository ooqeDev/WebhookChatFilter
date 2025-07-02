package com.ooqe.webhookChatFilter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatFilter implements Listener {

    private final WebhookChatFilter plugin;

    public ChatFilter(WebhookChatFilter plugin) {

        this.plugin = plugin;

    }

    @EventHandler

    public void onPlayerChat(AsyncPlayerChatEvent event) {

        String message = event.getMessage().toLowerCase();

        for (String blockedWord : plugin.getBlockedWords()) {

            if (message.contains(blockedWord)) {
                event.setCancelled(true);

                plugin.sendBlockedMessageToDiscord(event.getPlayer().getName(), event.getMessage());
                return;

            }
        }
    }
}
