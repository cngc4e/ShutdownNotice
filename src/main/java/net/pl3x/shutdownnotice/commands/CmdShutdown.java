package net.pl3x.shutdownnotice.commands;

import net.pl3x.shutdownnotice.ShutdownNotice;

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
		if (cs.hasPermission("shutdownnotice.command.shutdown")) {
			cs.sendMessage(plugin.colorize("&4You do not have permission for that command!"));
			plugin.log(cs.getName() + " was denied access to that command!");
			return true;
		}
		
		// TODO
		
		return true;
	}
}
