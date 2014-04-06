package net.pl3x.shutdownnotice.tasks;

import net.pl3x.shutdownnotice.ShutdownNotice;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Check implements Runnable {
	private ShutdownNotice plugin;
	
	public Check(ShutdownNotice plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		Integer timeLeft = plugin.getTimeLeft();
		if (timeLeft == null)
			return;
		if (plugin.getConfig().getBoolean("use-scoreboard", true)) {
			plugin.getObjective().setDisplayName(plugin.colorize("&4" + plugin.getShutdownType().getName().toUpperCase()));
			plugin.setScore(plugin.getObjective().getScore(Bukkit.getOfflinePlayer(plugin.colorize("&aIn:"))));
			plugin.getScore().setScore(timeLeft);
			for (Player player : Bukkit.getOnlinePlayers())
				player.setScoreboard(plugin.getScoreboard());
		}
		if (timeLeft <= 0) {
			Bukkit.getScheduler().runTask(plugin, new Shutdown(plugin));
			plugin.setTimeLeft(null);
			return;
		}
		if (timeLeft % plugin.getConfig().getInt("notify-every", 30) == 0) {
			Bukkit.getScheduler().runTask(plugin, new Notify(plugin, timeLeft));
		}
		plugin.setTimeLeft(timeLeft - 1);
	}
}
