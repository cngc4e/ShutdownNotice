package net.pl3x.shutdownnotice;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Shutdown {
	private ShutdownNotice plugin;

	private ScoreboardController scoreboardController;

	private ShutdownType type;
	private String reason;
	private Integer time;

	public Shutdown(ShutdownNotice plugin, ShutdownType type, String reason, Integer time) {
		this.plugin = plugin;
		this.type = type;
		this.reason = (reason == null || reason == "") ? plugin.getConfig().getString("default-reason", "scheduled maintenance") : reason;
		this.time = time;

		if (plugin.getConfig().getBoolean("use-scoreboard", true))
			scoreboardController = new ScoreboardController();
	}

	public ShutdownType getType() {
		return type;
	}

	public String getReason() {
		return reason;
	}

	public Integer getTime() {
		return time;
	}

	public String getTypeStr() {
		switch (type) {
			case SHUTDOWN:
				return plugin.getConfig().getString("type-shutdown", "Shutdown");
			case RESTART:
				return plugin.getConfig().getString("type-restart", "Restart");
			case REBOOT:
				return plugin.getConfig().getString("type-reboot", "Reboot");
		}
		return "Shutdown";
	}

	public ScoreboardController getScoreboardController() {
		return scoreboardController;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setType(ShutdownType type) {
		this.type = type;
	}

	public String formatTime(int seconds) {
		if (seconds <= 0)
			return "NOW";
		int hours = seconds / 60 / 60;
		int minutes = seconds / 60;
		seconds = seconds - (hours * 60 * 60) - (minutes * 60);
		String msg = "";
		if (hours > 0)
			msg += hours + " hour" + ((hours == 1) ? "" : "s");
		if (minutes > 0)
			msg += ((msg == "") ? "" : ", ") + minutes + " minute" + ((minutes == 1) ? "" : "s");
		if (seconds > 0)
			msg += ((msg == "") ? "" : ", ") + seconds + " second" + ((seconds == 1) ? "" : "s");
		return msg;
	}

	public void broadcastMessage(String message) {
		message = message.replaceAll("(?i)\\{timeleft\\}", formatTime((time != null) ? time + 1 : 0));
		message = message.replaceAll("(?i)\\{shutdowntype\\}", getTypeStr());
		message = message.replaceAll("(?i)\\{reason\\}", getReason());
		Bukkit.getServer().broadcastMessage(plugin.colorize(message));
	}

	public void broadcastScoreboard() {
		String title = plugin.getConfig().getString("title-scoreboard", "&e!! &4{shutdowntype} &e!!");
		title = title.replaceAll("(?i)\\{shutdowntype\\}", getTypeStr());
		scoreboardController.getObjective().setDisplayName(plugin.colorize(title));
		scoreboardController.setScore(scoreboardController.getObjective().getScore(Bukkit.getOfflinePlayer(plugin.colorize(plugin.getConfig().getString("timeleft-scoreboard", "&aIn:")))));
		scoreboardController.getScore().setScore(time);
		for (Player player : Bukkit.getOnlinePlayers())
			player.setScoreboard(scoreboardController.getScoreboard());
	}
}
