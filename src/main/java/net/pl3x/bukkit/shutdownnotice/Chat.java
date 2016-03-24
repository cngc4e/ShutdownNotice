package net.pl3x.bukkit.shutdownnotice;

import net.pl3x.bukkit.shutdownnotice.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Chat {
    private String message;

    public Chat(Lang lang) {
        this(lang.toString());
    }

    public Chat(String message) {
        this.message = message;

        colorize(); // auto color new chat messages
    }

    public void send(CommandSender recipient) {
        if (message == null || ChatColor.stripColor(message).isEmpty()) {
            return; // do not send blank messages
        }

        for (String part : message.split("\n")) {
            recipient.sendMessage(part);
        }
    }

    public void broadcast() {
        Bukkit.getOnlinePlayers().forEach(this::send);
        send(Bukkit.getConsoleSender());
    }

    private void colorize() {
        message = ChatColor.translateAlternateColorCodes('&', message);
    }
}
