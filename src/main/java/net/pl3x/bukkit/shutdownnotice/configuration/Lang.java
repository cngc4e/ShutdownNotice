package net.pl3x.bukkit.shutdownnotice.configuration;

import net.pl3x.bukkit.shutdownnotice.Logger;
import net.pl3x.bukkit.shutdownnotice.ShutdownNotice;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public enum Lang {
    COMMAND_NO_PERMS("&4You do not have permission for that!"),
    COMMAND_MISSING_ARGS("&4Not enough arguments supplied!"),
    UNKNOWN_COMMAND("&4Unknown command!"),

    NOTHING_TO_CANCEL("&4Nothing to cancel here!"),
    PROCESS_CANCELLED("&dProcess cancelled!"),
    PROCESS_ALREADY_IN_ACTION("&4Process already in action!"),
    INVALID_TIME("&4Not a valid value for time!"),
    TIME_NOT_POSITIVE("&4Time must be a positive number!"),

    TIME_FORMAT("&7&l{minutes}&e&l:&7&l{seconds}"),

    SHUTTING_DOWN("Shutting Down"),
    RESTARTING("Restarting"),
    RIGHT_NOW("&e&lRIGHT NOW!!!"),

    PING_MESSAGE("&cServer is {action} in {time}"),
    KICK_MESSAGE("Server is {action}\n{reason}"),

    ACTIONBAR_TXT("&4&l{action} In {time}"),
    TITLE_TXT("{time}"),
    SUBTITLE_TXT("&4&l{action}"),
    CHAT_TXT("&4&l{action} in {time} &4&lfor &e{reason}"),

    VERSION("&d{plugin} v{version}"),
    RELOAD("&d{plugin} v{version} reloaded.");

    private final String def;

    private static File configFile;
    private static FileConfiguration config;

    Lang(String def) {
        this.def = def;
        reload();
    }

    public static void reload() {
        reload(false);
    }

    public static void reload(boolean force) {
        if (configFile == null || force) {
            String lang = Config.LANGUAGE_FILE.getString();
            Logger.debug("Loading language file: " + lang);
            configFile = new File(ShutdownNotice.getPlugin(ShutdownNotice.class).getDataFolder(), lang);
            if (!configFile.exists()) {
                ShutdownNotice.getPlugin(ShutdownNotice.class).saveResource(Config.LANGUAGE_FILE.getString(), false);
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private String getKey() {
        return name().toLowerCase().replace("_", "-");
    }

    @Override
    public String toString() {
        String value = config.getString(name());
        if (value == null) {
            value = config.getString(getKey());
        }
        if (value == null) {
            Logger.warn("Missing lang data in file: " + getKey());
            value = def;
        }
        if (value == null) {
            Logger.error("Missing default lang data: " + getKey());
            value = "&c[missing lang data]";
        }
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public String replace(String find, String replace) {
        return toString().replace(find, replace);
    }
}
