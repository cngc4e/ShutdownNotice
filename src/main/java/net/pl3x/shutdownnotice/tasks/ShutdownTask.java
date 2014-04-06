package net.pl3x.shutdownnotice.tasks;

import net.pl3x.shutdownnotice.ShutdownNotice;

import org.bukkit.Bukkit;

public class ShutdownTask implements Runnable {
	private ShutdownNotice plugin;

	public ShutdownTask(ShutdownNotice plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		plugin.getShutdown().broadcastMessage(plugin.getConfig().getString("shutdown-message", "&1[&4ATTENTION&1] &eThe server will &4{shutdowntype} &ein &7{timeleft}&e for {reason}!"));

		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				for (String command : plugin.getConfig().getStringList("shutdown-commands")) {
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
				}
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
			}
		}, 60);
	}
}
