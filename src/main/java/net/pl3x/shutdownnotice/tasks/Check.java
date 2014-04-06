package net.pl3x.shutdownnotice.tasks;

import net.pl3x.shutdownnotice.Shutdown;
import net.pl3x.shutdownnotice.ShutdownNotice;

import org.bukkit.Bukkit;

public class Check implements Runnable {
	private ShutdownNotice plugin;

	public Check(ShutdownNotice plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		Shutdown shutdown = plugin.getShutdown();

		Integer timeLeft = shutdown.getTime();

		if (timeLeft == null)
			return;

		if (plugin.getConfig().getBoolean("use-scoreboard", true))
			shutdown.broadcastScoreboard();

		if (timeLeft <= 0) {
			Bukkit.getScheduler().runTask(plugin, new ShutdownTask(plugin));
			shutdown.setTime(null);
			return;
		}

		if (timeLeft % plugin.getConfig().getInt("notify-every", 30) == 0) {
			Bukkit.getScheduler().runTask(plugin, new Notify(plugin));
		}

		shutdown.setTime(timeLeft - 1);
	}
}
