package net.pl3x.shutdownnotice.commands;

import net.pl3x.shutdownnotice.ShutdownNotice;
import net.pl3x.shutdownnotice.ShutdownType;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdShutdown implements CommandExecutor {
	private ShutdownNotice plugin;
	
	public CmdShutdown(ShutdownNotice plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("shutdown"))
			return false;
		if (!cs.hasPermission("shutdownnotice.command.shutdown")) {
			cs.sendMessage(plugin.colorize("&4You do not have permission for that command!"));
			plugin.log(cs.getName() + " was denied access to that command!");
			return true;
		}
		if (args.length == 0) {
			cs.sendMessage(plugin.colorize("&4Must specify a time to delay!"));
			return true;
		}
		if (args[0].equalsIgnoreCase("cancel")) {
			if (plugin.getTimeLeft() == null) {
				cs.sendMessage(plugin.colorize("&4Nothing to cancel!"));
				return true;
			}
			plugin.setTimeLeft(null);
			String defaultMsg = "&1[&4ATTENTION&1] &eThe &4{shutdowntype} &ehas been cancelled!";
			String message = plugin.getConfig().getString("broadcast-message", defaultMsg);
			message = plugin.formatMessage(message, 0, plugin.getShutdownType());
			Bukkit.getServer().broadcastMessage(plugin.colorize(message));
			return true;
		}
		if (plugin.getTimeLeft() != null) {
			cs.sendMessage(plugin.colorize("&4There is a shutdown already in progress!"));
			return true;
		}
		Integer delay;
		try {
			delay = Integer.valueOf(args[0]);
		} catch (NumberFormatException e) {
			cs.sendMessage(plugin.colorize("&4Unknown subcommand!"));
			return true;
		}
		ShutdownType shutdownType = ShutdownType.SHUTDOWN;
		if (label.equalsIgnoreCase("RESTART"))
			shutdownType = ShutdownType.RESTART;
		if (label.equalsIgnoreCase("REBOOT"))
			shutdownType = ShutdownType.REBOOT;
		plugin.setTimeLeft(delay);
		plugin.setShutdownType(shutdownType);
		cs.sendMessage(plugin.colorize("&dShutdown task started!"));
		return true;
	}
}
