package net.pl3x.bukkit.shutdownnotice;

import java.util.logging.Level;

import net.pl3x.bukkit.shutdownnotice.configuration.Config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {
	public static void log(String msg) {
		msg = ChatColor.translateAlternateColorCodes('&', "&3[&d" + Main.getInstance().getName() + "&3]&r " + msg);
		if (Config.COLOR_LOGS.getBoolean()) {
			Bukkit.getConsoleSender().sendMessage(msg);
			return;
		}
		Bukkit.getLogger().log(Level.INFO, ChatColor.stripColor(msg));
	}

	public static void debug(String msg) {
		if (Config.DEBUG_MODE.getBoolean()) {
			log("&7[&eDEBUG&7]&r" + msg);
		}
	}
}
