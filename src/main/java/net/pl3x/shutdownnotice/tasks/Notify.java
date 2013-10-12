package net.pl3x.shutdownnotice.tasks;

import net.pl3x.shutdownnotice.ShutdownNotice;

import org.bukkit.Bukkit;

public class Notify implements Runnable {
	private ShutdownNotice plugin;
	private int timeLeft;
	
	public Notify(ShutdownNotice plugin, int timeLeft) {
		this.plugin = plugin;
		this.timeLeft = timeLeft;
	}
	
	@Override
	public void run() {
		String defaultMsg = "&1[&4ATTENTION&1] &eThe server will &4{shutdowntype} &ein &7{timeleft}&e!";
		String message = plugin.getConfig().getString("broadcast-message", defaultMsg);
		message = plugin.formatMessage(message, timeLeft, plugin.getShutdownType());
		Bukkit.getServer().broadcastMessage(plugin.colorize(message));
	}
}
