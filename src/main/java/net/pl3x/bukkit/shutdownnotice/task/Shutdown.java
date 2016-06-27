package net.pl3x.bukkit.shutdownnotice.task;

import net.pl3x.bukkit.shutdownnotice.Logger;
import net.pl3x.bukkit.shutdownnotice.ServerStatus;
import net.pl3x.bukkit.shutdownnotice.ServerStatus.State;
import net.pl3x.bukkit.shutdownnotice.ShutdownNotice;
import net.pl3x.bukkit.shutdownnotice.configuration.Config;
import net.pl3x.bukkit.shutdownnotice.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Shutdown extends BukkitRunnable {
    private final ShutdownNotice plugin;

    public Shutdown(ShutdownNotice plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        ServerStatus status = ServerStatus.getStatus();
        State state = status.getState();
        String reason = status.getReason();
        String action;

        if (reason == null) {
            reason = "";
        }

        if (state.equals(State.RESTART)) {
            // create blank restart file
            Logger.debug("Creating restart file.");
            try {
                if (new File(plugin.getDataFolder(), "restart").createNewFile()) {
                    Logger.debug("Creating restart file for startup script.");
                } else {
                    Logger.warn("Something went wrong with trying to create the restart file!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            action = Lang.RESTARTING.toString();
        } else {
            action = Lang.SHUTTING_DOWN.toString();
        }

        // run configured commands before shutting down
        List<String> shutdownCommands = Config.SHUTDOWN_COMMANDS.getStringList();
        for (String command : shutdownCommands) {
            command = command.replace("{action}", action).replace("{reason}", reason);
            Logger.debug("Performing command: " + command);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        // always perform save-all command
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "save-all");

        // always kick all players
        String kickMessage = ChatColor.translateAlternateColorCodes('&', Lang.KICK_MESSAGE.toString().replace("{action}", action).replace("{reason}", reason));
        for (Player player : Bukkit.getOnlinePlayers()) {
            Logger.debug("Kicking player: " + player.getName() + "(" + kickMessage + ")");
            player.kickPlayer(kickMessage);
        }

        // finally stop the server
        Logger.debug("Performing command: stop");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:stop");
    }
}
