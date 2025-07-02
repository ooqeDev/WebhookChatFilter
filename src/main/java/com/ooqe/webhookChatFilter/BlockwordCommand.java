package com.ooqe.webhookChatFilter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class BlockwordCommand implements CommandExecutor {

    private final WebhookChatFilter plugin;

    public BlockwordCommand(WebhookChatFilter plugin) {

        this.plugin = plugin;

    }

    @Override

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("staff.manage")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length != 1) {

            sender.sendMessage(ChatColor.YELLOW + "Usage: /blockword <word>");
            return true;

        }

        String word = args[0].toLowerCase();
        plugin.addBlockedWord(word);
        sender.sendMessage(ChatColor.RED + "Blocked word added: " + word);
        return true;
    }
}