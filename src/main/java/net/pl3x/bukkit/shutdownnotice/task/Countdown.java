package net.pl3x.bukkit.shutdownnotice.task;

import net.pl3x.bukkit.shutdownnotice.ActionBar;
import net.pl3x.bukkit.shutdownnotice.Chat;
import net.pl3x.bukkit.shutdownnotice.ServerStatus;
import net.pl3x.bukkit.shutdownnotice.ServerStatus.State;
import net.pl3x.bukkit.shutdownnotice.ShutdownNotice;
import net.pl3x.bukkit.shutdownnotice.Title;
import net.pl3x.bukkit.shutdownnotice.configuration.Config;
import net.pl3x.bukkit.shutdownnotice.configuration.Lang;
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

        String seconds = String.format("%02d", timeLeft % 60);
        String minutes = String.format("%02d", timeLeft / 60);
        String time = Lang.TIME_FORMAT.toString().replace("{minutes}", minutes).replace("{seconds}", seconds);
        String action = state.equals(State.SHUTDOWN) ? Lang.SHUTTING_DOWN.toString() : Lang.RESTARTING.toString();

        if (timeLeft <= 0) {
            String rightNow = Lang.RIGHT_NOW.toString();

            String actionBarTxt = Lang.ACTIONBAR_TXT.replace("{action}", action).replace("{time}", rightNow).replace("{reason}", reason);
            String titleTxt = Lang.TITLE_TXT.replace("{action}", action).replace("{time}", rightNow).replace("{reason}", reason);
            String subtitleTxt = Lang.SUBTITLE_TXT.replace("{action}", action).replace("{time}", rightNow).replace("{reason}", reason);
            String chatTxt = Lang.CHAT_TXT.replace("{action}", action).replace("{time}", rightNow).replace("{reason}", reason);

            new ActionBar(actionBarTxt).broadcast();
            new Title(titleTxt, subtitleTxt).broadcast();
            new Chat(chatTxt).broadcast();

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

        String actionBarTxt = Lang.ACTIONBAR_TXT.replace("{action}", action).replace("{time}", time).replace("{reason}", reason);
        String titleTxt = Lang.TITLE_TXT.replace("{action}", action).replace("{time}", time).replace("{reason}", reason);
        String subtitleTxt = Lang.SUBTITLE_TXT.replace("{action}", action).replace("{time}", time).replace("{reason}", reason);
        String chatTxt = Lang.CHAT_TXT.replace("{action}", action).replace("{time}", time).replace("{reason}", reason);

        // broadcast actionbar timer always
        new ActionBar(actionBarTxt).broadcast();

        // broadcast chat and title message
        if (broadcast) {
            new Title(titleTxt, subtitleTxt).broadcast();
            new Chat(chatTxt).broadcast();
        }

        status.setStatus(state, timeLeft - 1, reason);
        firstRun = false;
    }

}
