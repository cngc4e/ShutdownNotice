package net.pl3x.bukkit.shutdownnotice.task;

import net.pl3x.bukkit.shutdownnotice.ShutdownNotice;
import net.pl3x.bukkit.shutdownnotice.configuration.Config;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public class InternalClock extends BukkitRunnable {
    private final ShutdownNotice plugin;
    private LocalDateTime timeNow;

    public InternalClock(ShutdownNotice plugin) {
        this.plugin = plugin;
        this.runTaskTimerAsynchronously(plugin, 20L, 20L);
    }

    @Override
    public void run() {
        this.timeNow = LocalDateTime.now();

        if (!Config.AUTO_RESTART_ENABLED) {
            return; // auto restart disabled
        }

        LocalDateTime restartTime = Config.AUTO_RESTART_TIME;
        if (restartTime == null) {
            return; // no configured restart time
        }

        long timeLeft = timeNow.until(restartTime, SECONDS);
        if (timeLeft > Config.AUTO_RESTART_COUNTDOWN) {
            return; // not ready for countdown
        }

        if (plugin.getCountdown() != null) {
            return; // already performing a countdown
        }

        Countdown countdown = new Countdown(plugin, Config.AUTO_RESTART_REASON, true, timeLeft);
        plugin.setCountdown(countdown);
        countdown.runTaskTimerAsynchronously(plugin, 0, 20L);
    }

    public LocalDateTime getTimeNow() {
        return timeNow;
    }
}
