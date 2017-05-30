package net.pl3x.bukkit.shutdownnotice.task;

import net.pl3x.bukkit.shutdownnotice.ShutdownNotice;
import net.pl3x.bukkit.shutdownnotice.configuration.Config;
import net.pl3x.bukkit.shutdownnotice.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

public class Countdown extends BukkitRunnable {
    private final ShutdownNotice plugin;
    private final String reason;
    private final boolean restart;
    private long timeLeft;
    private boolean firstRun;

    public Countdown(ShutdownNotice plugin, String reason, boolean restart, long timeLeft) {
        this.plugin = plugin;
        this.reason = reason == null || reason.isEmpty() ? "" : ChatColor.translateAlternateColorCodes('&', reason);
        this.restart = restart;
        this.timeLeft = timeLeft;
        this.firstRun = true;
    }

    public String getReason() {
        return reason;
    }

    public boolean isRestart() {
        return restart;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    @Override
    public void run() {
        boolean broadcast = firstRun;
        firstRun = false;
        List<String> displayIntervals = Config.DISPLAY_INTERVALS;
        if (displayIntervals != null && !displayIntervals.isEmpty()) {
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("JavaScript");
            for (String condition : displayIntervals) {
                try {
                    engine.eval("timeLeft = " + timeLeft);
                    if ((Boolean) engine.eval(condition)) {
                        broadcast = true;
                        break;
                    }
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }
        }

        String action = ChatColor.translateAlternateColorCodes('&', restart ? Lang.RESTARTING : Lang.SHUTTING_DOWN);
        String time = timeLeft <= 0 ? Lang.RIGHT_NOW : Lang.TIME_FORMAT
                .replace("{minutes}", String.format("%02d", timeLeft / 60))
                .replace("{seconds}", String.format("%02d", timeLeft % 60));

        if (broadcast) {
            String title = ChatColor.translateAlternateColorCodes('&', Lang.TITLE_TXT
                    .replace("{action}", action)
                    .replace("{time}", time)
                    .replace("{reason}", reason));
            String subTitle = ChatColor.translateAlternateColorCodes('&', Lang.SUBTITLE_TXT
                    .replace("{action}", action)
                    .replace("{time}", time)
                    .replace("{reason}", reason));
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendTitle(title, subTitle);
            }

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Lang.CHAT_TXT
                    .replace("{action}", action)
                    .replace("{time}", time)
                    .replace("{reason}", reason)));

            if (plugin.getDiscordSRVHook() != null) {
                plugin.getDiscordSRVHook().sendToDiscord(Lang.DISCORD_TXT
                        .replace("{action}", action)
                        .replace("{time}", time)
                        .replace("{reason}", reason));
            }
        }

        if (timeLeft <= Config.DISPLAY_ACTIONBAR) {
            String actionBar = ChatColor.translateAlternateColorCodes('&', Lang.ACTIONBAR_TXT
                    .replace("{action}", action)
                    .replace("{time}", time)
                    .replace("{reason}", reason));
            for (Player online : plugin.getServer().getOnlinePlayers()) {
                online.sendActionBar(actionBar);
            }
        }

        if (timeLeft <= 0) {
            new Shutdown(plugin, reason, restart).runTask(plugin);
            cancel();
        }

        timeLeft--;
    }

    @Override
    public void cancel() {
        super.cancel();

        plugin.setCountdown(null);
    }
}
