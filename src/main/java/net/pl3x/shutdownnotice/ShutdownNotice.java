package net.pl3x.shutdownnotice;

import java.io.IOException;
import java.util.logging.Level;

import net.pl3x.shutdownnotice.commands.CmdShutdown;
import net.pl3x.shutdownnotice.tasks.Check;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class ShutdownNotice extends JavaPlugin {
	private Shutdown shutdown;

	public void onEnable() {
		saveDefaultConfig();

		shutdown = new Shutdown(this, null, null, null);

		Bukkit.getScheduler().runTaskTimer(this, new Check(this), 20, 20);

		getCommand("shutdown").setExecutor(new CmdShutdown(this));

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			log("&4[ERROR] &rFailed to start Metrics: " + e.getMessage());
		}

		log(getName() + " v" + getDescription().getVersion() + " by BillyGalbreath enabled!");
	}

	public void onDisable() {
		log(getName() + " Disabled.");
	}

	public void log(Object obj) {
		if (getConfig().getBoolean("color-logs", true)) {
			getServer().getConsoleSender().sendMessage(colorize("&3[&d" + getName() + "&3]&r " + obj));
		} else {
			Bukkit.getLogger().log(Level.INFO, "[" + getName() + "] " + ((String) obj).replaceAll("(?)\u00a7([a-f0-9k-or])", ""));
		}
	}

	public String colorize(String str) {
		return str.replaceAll("(?i)&([a-f0-9k-or])", "\u00a7$1");
	}

	public Shutdown getShutdown() {
		return shutdown;
	}

	public void setShutdown(Shutdown shutdown) {
		this.shutdown = shutdown;
	}
}
