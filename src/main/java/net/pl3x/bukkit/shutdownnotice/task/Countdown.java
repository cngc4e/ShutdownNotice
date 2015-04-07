package net.pl3x.bukkit.shutdownnotice.task;

import net.pl3x.bukkit.shutdownnotice.Logger;
import net.pl3x.bukkit.shutdownnotice.Main;
import net.pl3x.bukkit.shutdownnotice.ServerStatus;
import net.pl3x.bukkit.shutdownnotice.ServerStatus.State;
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
		String time = "&7&l" + minutes + "&e&l:&7&l" + seconds;
		String action = state.equals(State.SHUTDOWN) ? "Shutting Down" : "Restarting";

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
		} else if (timeLeft % 60 == 0) {
			broadcast = true; // chat and title notice every minute
		} else if (timeLeft == 30) {
			broadcast = true; // chat and title notice 30 second warning
		} else if (timeLeft <= 10) {
			broadcast = true; // chat and title notice 10 second warning (repeat every second until finished)
		}

		String actionBarTxt = Lang.ACTIONBAR_TXT.get().replace("{action}", action).replace("{time}", time).replace("{reason}", reason);
		String titleTxt = Lang.TITLE_TXT.get().replace("{action}", action).replace("{time}", time).replace("{reason}", reason);
		String subtitleTxt = Lang.SUBTITLE_TXT.get().replace("{action}", action).replace("{time}", time).replace("{reason}", reason);
		String chatTxt = Lang.CHAT_TXT.get().replace("{action}", action).replace("{time}", time).replace("{reason}", reason);

		Logger.log(actionBarTxt);
		Logger.log(titleTxt);
		Logger.log(subtitleTxt);
		Logger.log(chatTxt);

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
