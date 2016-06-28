package net.pl3x.bukkit.shutdownnotice.task;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.pl3x.bukkit.chatapi.ComponentSender;
import net.pl3x.bukkit.shutdownnotice.ServerStatus;
import net.pl3x.bukkit.shutdownnotice.ServerStatus.State;
import net.pl3x.bukkit.shutdownnotice.ShutdownNotice;
import net.pl3x.bukkit.shutdownnotice.configuration.Config;
import net.pl3x.bukkit.shutdownnotice.configuration.Lang;
import net.pl3x.bukkit.titleapi.Title;
import net.pl3x.bukkit.titleapi.api.TitleType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Countdown extends BukkitRunnable {
    private final ShutdownNotice plugin;
    private boolean firstRun = true;

    public Countdown(ShutdownNotice plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        ServerStatus status = ServerStatus.getStatus();
        State state = status.getState();
        Integer timeLeft = status.getTimeLeft();
        String reason = status.getReason();

        if (state == null || state.equals(State.RUNNING) || timeLeft == null) {
            status.setStatus(State.RUNNING, null, null);
            this.cancel();
            return;
        }

        if (reason == null) {
            reason = "";
        }

        String time = Lang.TIME_FORMAT
                .replace("{minutes}", String.format("%02d", timeLeft / 60))
                .replace("{seconds}", String.format("%02d", timeLeft % 60));
        String action = state.equals(State.SHUTDOWN) ? Lang.SHUTTING_DOWN.toString() : Lang.RESTARTING.toString();

        if (timeLeft <= 0) {
            String rightNow = Lang.RIGHT_NOW.toString();

            broadcastActionbar(action, rightNow, reason);
            broadcastTitle(action, rightNow, reason);
            broadcastChat(action, rightNow, reason);

            new Shutdown(plugin).runTaskLater(plugin, 20);
            this.cancel();
            return;
        }

        boolean broadcast = false;
        if (firstRun) {
            broadcast = true; // always show on first run
        } else {
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("JavaScript");
            for (String condition : Config.DISPLAY_INTERVALS.getStringList()) {
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

        // broadcast actionbar timer always
        broadcastActionbar(action, time, reason);

        // broadcast chat and title message
        if (broadcast) {
            broadcastTitle(action, time, reason);
            broadcastChat(action, time, reason);
        }

        status.setStatus(state, timeLeft - 1, reason);
        firstRun = false;
    }

    private void broadcastActionbar(String action, String time, String reason) {
        BaseComponent[] actionComponent = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                Lang.ACTIONBAR_TXT
                        .replace("{action}", action)
                        .replace("{time}", time)
                        .replace("{reason}", reason)));
        for (Player online : Bukkit.getOnlinePlayers()) {
            ComponentSender.sendMessage(online, ChatMessageType.ACTION_BAR, new TextComponent(BaseComponent.toLegacyText(actionComponent)));
        }
    }

    private void broadcastTitle(String action, String time, String reason) {
        Title titleReset = new Title(TitleType.RESET, null);
        Title titleTimings = new Title(5, 60, 10);
        Title titleText = new Title(TitleType.TITLE, ChatColor.translateAlternateColorCodes('&',
                Lang.TITLE_TXT
                        .replace("{action}", action)
                        .replace("{time}", time)
                        .replace("{reason}", reason)),
                5, 60, 10);
        Title titleSubtext = new Title(TitleType.SUBTITLE, ChatColor.translateAlternateColorCodes('&',
                Lang.SUBTITLE_TXT
                        .replace("{action}", action)
                        .replace("{time}", time)
                        .replace("{reason}", reason)),
                5, 60, 10);
        for (Player online : Bukkit.getOnlinePlayers()) {
            titleReset.send(online);
            titleTimings.send(online);
            titleText.send(online);
            titleSubtext.send(online);
        }
    }

    private void broadcastChat(String action, String time, String reason) {
        String chatTxt = Lang.CHAT_TXT
                .replace("{action}", action)
                .replace("{time}", time)
                .replace("{reason}", reason);
        BaseComponent[] chatText = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', chatTxt));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', chatTxt)); // always include console
        for (Player online : Bukkit.getOnlinePlayers()) {
            ComponentSender.sendMessage(online, chatText);
        }
    }
}
