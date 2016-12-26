package net.pl3x.bukkit.shutdownnotice.configuration;

import net.pl3x.bukkit.shutdownnotice.Logger;
import net.pl3x.bukkit.shutdownnotice.ShutdownNotice;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.LocalTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;

public class Config {
    public static boolean COLOR_LOGS = true;
    public static boolean DEBUG_MODE = false;
    public static String LANGUAGE_FILE = "lang-en.yml";
    public static boolean UPDATE_PING_MOTD = true;
    public static List<String> SHUTDOWN_COMMANDS;
    public static List<String> DISPLAY_INTERVALS;
    public static int DISPLAY_ACTIONBAR;
    public static LocalTime AUTO_RESTART_TIME;
    public static String AUTO_RESTART_REASON;
    public static int AUTO_RESTART_COUNTDOWN;

    public static void reload() {
        ShutdownNotice plugin = ShutdownNotice.getPlugin();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        COLOR_LOGS = config.getBoolean("color-logs", true);
        DEBUG_MODE = config.getBoolean("debug-mode", false);
        LANGUAGE_FILE = config.getString("language-file", "lang-en.yml");
        UPDATE_PING_MOTD = config.getBoolean("update-ping-list", true);
        SHUTDOWN_COMMANDS = config.getStringList("shutdown-commands");
        DISPLAY_INTERVALS = config.getStringList("display-intervals");
        DISPLAY_ACTIONBAR = config.getInt("display-actionbar", 900);

        try {
            AUTO_RESTART_TIME = LocalTime.parse(config.getString("auto-restart.time"));
            if (LocalTime.now().until(AUTO_RESTART_TIME, SECONDS) < 0) {
                AUTO_RESTART_TIME = AUTO_RESTART_TIME.plus(24, HOURS);
            }
        } catch (Exception ignore) {
            Logger.warn("No auto-restart time specified. Will not schedule an auto restart.");
        }
        AUTO_RESTART_REASON = config.getString("auto-restart.reason", "Nightly Reboot");
        AUTO_RESTART_COUNTDOWN = config.getInt("auto-restart.countdown", 900);
    }
}
