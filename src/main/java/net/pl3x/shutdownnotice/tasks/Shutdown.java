package net.pl3x.shutdownnotice.tasks;

import net.pl3x.shutdownnotice.ShutdownNotice;

import org.bukkit.Bukkit;

public class Shutdown  implements Runnable {
	private ShutdownNotice plugin;
	
	public Shutdown(ShutdownNotice plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		String defaultMsg = "&1[&4ATTENTION&1] &eThe server will &4{shutdowntype} &ein &7{timeleft}&e!";
		String message = plugin.getConfig().getString("broadcast-message", defaultMsg);
		message = plugin.formatMessage(message, 0, plugin.getShutdownType());
		Bukkit.getServer().broadcastMessage(plugin.colorize(message));
		for (String command : plugin.getConfig().getStringList("shutdown-commands")) {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
		}
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
	}
}
