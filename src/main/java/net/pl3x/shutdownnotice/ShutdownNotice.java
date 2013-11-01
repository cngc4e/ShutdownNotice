package net.pl3x.shutdownnotice;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import net.pl3x.shutdownnotice.commands.CmdShutdown;
import net.pl3x.shutdownnotice.tasks.Check;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class ShutdownNotice extends JavaPlugin {
	private Integer timeLeft;
	private ShutdownType shutdownType;
	private String reason;
	
	public void onEnable() {
		if (!new File(getDataFolder() + File.separator + "config.yml").exists())
			saveDefaultConfig();
		
		setReason(null);
		
		getCommand("shutdown").setExecutor(new CmdShutdown(this));
		
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			log("&4[ERROR] &rFailed to start Metrics: " + e.getMessage());
		}
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Check(this), 20, 20);
		
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
	
	public Integer getTimeLeft() {
		return timeLeft;
	}
	
	public void setTimeLeft(Integer time) {
		timeLeft = time;
	}
	
	public ShutdownType getShutdownType() {
		return shutdownType;
	}
	
	public void setShutdownType(ShutdownType type) {
		shutdownType = type;
	}
	
	public String getReason() {
		return reason;
	}
	
	public void setReason(String string) {
		if (string == null || string == "") {
			reason = getConfig().getString("default-reason", "scheduled maintenance");
			return;
		}
		reason = string;
	}
	
	public String formatMessage(String message, int timeLeft, ShutdownType shutdownType) {
		message = message.replaceAll("(?i)\\{timeleft\\}", formatTime(timeLeft));
		message = message.replaceAll("(?i)\\{shutdowntype\\}", shutdownType.getName());
		message = message.replaceAll("(?i)\\{reason\\}", getReason());
		return message;
	}
	
	private String formatTime(int seconds) {
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
}
