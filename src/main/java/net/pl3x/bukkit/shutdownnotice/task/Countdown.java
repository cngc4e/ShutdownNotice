package net.pl3x.bukkit.shutdownnotice.task;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.pl3x.bukkit.shutdownnotice.Main;
import net.pl3x.bukkit.shutdownnotice.ServerStatus;
import net.pl3x.bukkit.shutdownnotice.ServerStatus.State;
import net.pl3x.bukkit.shutdownnotice.configuration.Config;
import net.pl3x.bukkit.shutdownnotice.configuration.Lang;
import net.pl3x.bukkit.shutdownnotice.manager.ChatManager;

import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {
	private Main plugin;
	private boolean firstRun = true;

	public Countdown(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		ServerStatus status = plugin.getStatus();
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
		String time = Lang.TIME_FORMAT.get().replace("{minutes}", minutes).replace("{seconds}", seconds);
		String action = state.equals(State.SHUTDOWN) ? Lang.SHUTTING_DOWN.get() : Lang.RESTARTING.get();

		if (timeLeft <= 0) {
			String rightNow = Lang.RIGHT_NOW.get();

			String actionBarTxt = Lang.ACTIONBAR_TXT.get().replace("{action}", action).replace("{time}", rightNow).replace("{reason}", reason);
			String titleTxt = Lang.TITLE_TXT.get().replace("{action}", action).replace("{time}", rightNow).replace("{reason}", reason);
			String subtitleTxt = Lang.SUBTITLE_TXT.get().replace("{action}", action).replace("{time}", rightNow).replace("{reason}", reason);
			String chatTxt = Lang.CHAT_TXT.get().replace("{action}", action).replace("{time}", rightNow).replace("{reason}", reason);

			ChatManager.broadcastActionbarMessage(actionBarTxt);
			ChatManager.broadcastTitleMessage(5, 60, 10, titleTxt, subtitleTxt);
			ChatManager.broadcastChatMessage(chatTxt);

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

		String actionBarTxt = Lang.ACTIONBAR_TXT.get().replace("{action}", action).replace("{time}", time).replace("{reason}", reason);
		String titleTxt = Lang.TITLE_TXT.get().replace("{action}", action).replace("{time}", time).replace("{reason}", reason);
		String subtitleTxt = Lang.SUBTITLE_TXT.get().replace("{action}", action).replace("{time}", time).replace("{reason}", reason);
		String chatTxt = Lang.CHAT_TXT.get().replace("{action}", action).replace("{time}", time).replace("{reason}", reason);

		// broadcast actionbar timer always
		ChatManager.broadcastActionbarMessage(actionBarTxt);

		// broadcast chat and title message
		if (broadcast) {
			ChatManager.broadcastTitleMessage(5, 60, 10, titleTxt, subtitleTxt);
			ChatManager.broadcastChatMessage(chatTxt);
		}

		status.setStatus(state, timeLeft - 1, reason);
		firstRun = false;
	}

}
