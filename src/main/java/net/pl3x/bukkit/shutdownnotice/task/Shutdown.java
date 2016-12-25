package net.pl3x.bukkit.shutdownnotice.task;

import net.pl3x.bukkit.shutdownnotice.Logger;
import net.pl3x.bukkit.shutdownnotice.ShutdownNotice;
import net.pl3x.bukkit.shutdownnotice.configuration.Config;
import net.pl3x.bukkit.shutdownnotice.configuration.Lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class Shutdown extends BukkitRunnable {
    private final ShutdownNotice plugin;
    private final String reason;
    private final boolean reboot;

    public Shutdown(ShutdownNotice plugin, String reason, boolean reboot) {
        this.plugin = plugin;
        this.reason = reason;
        this.reboot = reboot;
    }

    @Override
    public void run() {
        if (reboot) {
            try {
                if (!new File(plugin.getDataFolder(), "restart").createNewFile()) {
                    Logger.error("Unable to create restart file!");
                }
            } catch (IOException e) {
                Logger.error("Unable to create restart file!");
                Logger.error(e.getMessage());
                e.printStackTrace();
            }
        }

        String action = ChatColor.translateAlternateColorCodes('&', reboot ? Lang.RESTARTING : Lang.SHUTTING_DOWN);

        for (String command : Config.SHUTDOWN_COMMANDS) {
            command = command
                    .replace("{action}", action)
                    .replace("{reason}", reason);
            Logger.debug("Performing command: " + command);
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
        }

        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "minecraft:save-all");

        String kickMessage = ChatColor.translateAlternateColorCodes('&', Lang.KICK_MESSAGE
                .replace("{action}", action)
                .replace("{reason}", reason));
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Logger.debug("Kicking player: " + player.getName() + "(" + kickMessage + ")");
            player.kickPlayer(kickMessage);
        }

        Logger.debug("Performing command: stop");
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "minecraft:stop");
    }
}
