package net.pl3x.bukkit.shutdownnotice.configuration;

import net.pl3x.bukkit.shutdownnotice.ShutdownNotice;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Lang {
    public static String COMMAND_NO_PERMS;

    public static String NOTHING_TO_CANCEL;
    public static String SHUTDOWN_CANCELLED;
    public static String RESTART_CANCELLED;
    public static String PROCESS_ALREADY_IN_ACTION;
    public static String INVALID_TIME;
    public static String TIME_NOT_POSITIVE;

    public static String TIME_FORMAT;

    public static String SHUTTING_DOWN;
    public static String RESTARTING;
    public static String RIGHT_NOW;

    public static String PING_MESSAGE;
    public static String KICK_MESSAGE;

    public static String ACTIONBAR_TXT;
    public static String TITLE_TXT;
    public static String SUBTITLE_TXT;
    public static String CHAT_TXT;
    public static String DISCORD_TXT;

    public static String VERSION;
    public static String RELOAD;

    public static void reload() {
        ShutdownNotice plugin = ShutdownNotice.getPlugin();
        String langFile = Config.LANGUAGE_FILE;
        File configFile = new File(plugin.getDataFolder(), langFile);
        if (!configFile.exists()) {
            plugin.saveResource(Config.LANGUAGE_FILE, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        COMMAND_NO_PERMS = config.getString("command-no-perms", "&4You do not have permission for that!");

        NOTHING_TO_CANCEL = config.getString("nothing-to-cancel", "&4Nothing to cancel here!");
        SHUTDOWN_CANCELLED = config.getString("shutdown-cancelled", "&dShutdown cancelled!");
        RESTART_CANCELLED = config.getString("restart-cancelled", "&dRestart cancelled!");
        PROCESS_ALREADY_IN_ACTION = config.getString("process-already-in-action", "&4Process already in action!");
        INVALID_TIME = config.getString("invalid-time", "&4Not a valid value for time!");
        TIME_NOT_POSITIVE = config.getString("time-not-positive", "&4Time must be a positive number!");

        TIME_FORMAT = config.getString("time-format", "&7&l{minutes}&e&l:&7&l{seconds}");

        SHUTTING_DOWN = config.getString("shutting-down", "Shutting Down");
        RESTARTING = config.getString("restarting", "Restarting");
        RIGHT_NOW = config.getString("right-now", "&e&lRIGHT NOW!!!");

        PING_MESSAGE = config.getString("ping-message", "&cServer is {action} in {time}");
        KICK_MESSAGE = config.getString("kick-message", "Server is {action}\n{reason}");

        ACTIONBAR_TXT = config.getString("actionbar-txt", "&4&l{action} In {time}");
        TITLE_TXT = config.getString("title-txt", "{time}");
        SUBTITLE_TXT = config.getString("subtitle-txt", "&4&l{action}");
        CHAT_TXT = config.getString("chat-txt", "&4&l{action} in {time} &4&lfor &e{reason}");
        DISCORD_TXT = config.getString("discord-txt", ":warning: **{action}** in **{time}** for {reason}");

        VERSION = config.getString("version", "&d{plugin} v{version}");
        RELOAD = config.getString("reload", "&d{plugin} v{version} reloaded.");
    }

    public static void send(CommandSender recipient, String message) {
        if (message == null) {
            return; // do not send blank messages
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (ChatColor.stripColor(message).isEmpty()) {
            return; // do not send blank messages
        }

        for (String part : message.split("\n")) {
            recipient.sendMessage(part);
        }
    }
}
