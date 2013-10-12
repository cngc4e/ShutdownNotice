package net.pl3x.shutdownnotice.tasks;

import net.pl3x.shutdownnotice.ShutdownNotice;

import org.bukkit.Bukkit;

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
		if (timeLeft <= 0) {
			//plugin.log("SHUTDOWN! timeleft: " + timeLeft);
			Bukkit.getScheduler().runTask(plugin, new Shutdown(plugin));
			plugin.setTimeLeft(null);
			return;
		}
		if (timeLeft % plugin.getConfig().getInt("notify-every", 30) == 0) {
			//plugin.log("NOTIFY! timeleft: " + timeLeft);
			Bukkit.getScheduler().runTask(plugin, new Notify(plugin, timeLeft));
		}
		plugin.setTimeLeft(timeLeft - 1);
	}
}
