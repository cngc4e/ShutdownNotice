package net.pl3x.bukkit.shutdownnotice.configuration;

import net.pl3x.bukkit.shutdownnotice.ShutdownNotice;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;

public class Config {
    public static String LANGUAGE_FILE = "lang-en.yml";
    public static boolean UPDATE_PING_MOTD = true;
    public static List<String> SHUTDOWN_COMMANDS;
    public static List<String> DISPLAY_INTERVALS;
    public static int DISPLAY_ACTIONBAR;
    public static boolean AUTO_RESTART_ENABLED;
    public static LocalDateTime AUTO_RESTART_TIME;
    public static String AUTO_RESTART_REASON;
    public static int AUTO_RESTART_COUNTDOWN;

    public static void reload() {
        ShutdownNotice plugin = ShutdownNotice.getPlugin();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        LANGUAGE_FILE = config.getString("language-file", "lang-en.yml");
        UPDATE_PING_MOTD = config.getBoolean("update-ping-list", true);
        SHUTDOWN_COMMANDS = config.getStringList("shutdown-commands");
        DISPLAY_INTERVALS = config.getStringList("display-intervals");
        DISPLAY_ACTIONBAR = config.getInt("display-actionbar", 900);

        AUTO_RESTART_ENABLED = config.getBoolean("auto-restart.enabled", false);
        try {
            AUTO_RESTART_TIME = LocalTime.parse(config.getString("auto-restart.time")).atDate(LocalDate.now());
            if (LocalTime.now().until(AUTO_RESTART_TIME, SECONDS) < 0) {
                AUTO_RESTART_TIME = AUTO_RESTART_TIME.plus(24, HOURS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        AUTO_RESTART_REASON = config.getString("auto-restart.reason", "Nightly Reboot");
        AUTO_RESTART_COUNTDOWN = config.getInt("auto-restart.countdown", 900);
    }
}
