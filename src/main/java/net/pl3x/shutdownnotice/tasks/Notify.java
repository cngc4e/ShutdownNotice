package net.pl3x.shutdownnotice.tasks;

import net.pl3x.shutdownnotice.ShutdownNotice;

public class Notify implements Runnable {
	private ShutdownNotice plugin;

	public Notify(ShutdownNotice plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		plugin.getShutdown().broadcastMessage(plugin.getConfig().getString("shutdown-message", "&1[&4ATTENTION&1] &eThe server will &4{shutdowntype} &ein &7{timeleft}&e for {reason}!"));
	}
}
