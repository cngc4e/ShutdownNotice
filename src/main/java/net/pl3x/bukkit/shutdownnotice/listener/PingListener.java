package net.pl3x.bukkit.shutdownnotice.listener;

import net.pl3x.bukkit.shutdownnotice.ShutdownNotice;
import net.pl3x.bukkit.shutdownnotice.configuration.Config;
import net.pl3x.bukkit.shutdownnotice.configuration.Lang;
import net.pl3x.bukkit.shutdownnotice.task.Countdown;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class PingListener implements Listener {
    private final ShutdownNotice plugin;

    public PingListener(ShutdownNotice plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(ServerListPingEvent event) {
        if (!Config.UPDATE_PING_MOTD) {
            return;
        }

        Countdown countdown = plugin.getCountdown();
        if (countdown == null) {
            return; // not in a countdown
        }

        long timeLeft = countdown.getTimeLeft();
        event.setMotd(ChatColor.translateAlternateColorCodes('&', Lang.PING_MESSAGE
                .replace("{time}", timeLeft <= 0 ? Lang.RIGHT_NOW : Lang.TIME_FORMAT
                        .replace("{minutes}", String.format("%02d", timeLeft / 60))
                        .replace("{seconds}", String.format("%02d", timeLeft % 60)))
                .replace("{action}", countdown.isRestart() ? Lang.RESTARTING : Lang.SHUTTING_DOWN)
                .replace("{reason}", countdown.getReason())));
    }
}
