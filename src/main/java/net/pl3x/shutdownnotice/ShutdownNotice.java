package net.pl3x.shutdownnotice;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import net.pl3x.shutdownnotice.commands.CmdShutdown;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class ShutdownNotice extends JavaPlugin {
	public void onEnable() {
		if (!new File(getDataFolder() + File.separator + "config.yml").exists())
			saveDefaultConfig();
		
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
	
	public void log (Object obj) {
		if (getConfig().getBoolean("color-logs", true)) {
			getServer().getConsoleSender().sendMessage(colorize("&3[&d" +  getName() + "&3]&r " + obj));
		} else {
			Bukkit.getLogger().log(Level.INFO, "[" + getName() + "] " + ((String) obj).replaceAll("(?)\u00a7([a-f0-9k-or])", ""));
		}
	}
	
	public String colorize(String str) {
		return str.replaceAll("(?i)&([a-f0-9k-or])", "\u00a7$1");
	}
}
