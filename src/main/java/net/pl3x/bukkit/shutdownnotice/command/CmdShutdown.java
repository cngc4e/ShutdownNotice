package net.pl3x.bukkit.shutdownnotice.command;

import net.pl3x.bukkit.shutdownnotice.Main;
import net.pl3x.bukkit.shutdownnotice.ServerStatus;
import net.pl3x.bukkit.shutdownnotice.ServerStatus.State;
import net.pl3x.bukkit.shutdownnotice.configuration.Lang;
import net.pl3x.bukkit.shutdownnotice.manager.ChatManager;
import net.pl3x.bukkit.shutdownnotice.task.Countdown;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdShutdown implements CommandExecutor {
	private Main plugin;

	public CmdShutdown(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("command.shutdown")) {
			ChatManager.sendChatMessage(sender, Lang.COMMAND_NO_PERMS.get());
			return true;
		}

		if (args.length < 1) {
			ChatManager.sendChatMessage(sender, Lang.COMMAND_MISSING_ARGS.get());
			return false;
		}

		ServerStatus status = plugin.getStatus();
		State state = status.getState();

		if (args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("abort") || args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("0")) {
			if (state.equals(State.RUNNING)) {
				ChatManager.sendChatMessage(sender, Lang.NOTHING_TO_CANCEL.get());
				return true;
			}
			status.setStatus(State.RUNNING, null, null);
			ChatManager.sendChatMessage(sender, Lang.PROCESS_CANCELLED.get());
			return true;
		}

		if (!state.equals(State.RUNNING)) {
			ChatManager.sendChatMessage(sender, Lang.PROCESS_ALREADY_IN_ACTION.get());
			return true;
		}

		int time = -1;
		try {
			time = Integer.valueOf(args[0]);
		} catch (NumberFormatException e) {
			ChatManager.sendChatMessage(sender, Lang.INVALID_TIME.get());
			return true;
		}

		if (time <= 0) {
			ChatManager.sendChatMessage(sender, Lang.TIME_NOT_POSITIVE.get());
			return true;
		}

		State newState;
		if (label.equalsIgnoreCase("shutdown")) {
			newState = State.SHUTDOWN;
		} else if (label.equalsIgnoreCase("restart")) {
			newState = State.RESTART;
		} else if (label.equalsIgnoreCase("reboot")) {
			newState = State.RESTART;
		} else {
			ChatManager.sendChatMessage(sender, Lang.UNKNOWN_COMMAND.get());
			return true;
		}

		String reason = null;
		if (args.length > 2) {
			StringBuilder sb = new StringBuilder();
			for (int i = 2; i < args.length; i++) {
				sb.append(args[i]);
			}
			reason = sb.toString();
		}

		status.setStatus(newState, time, reason);

		new Countdown(plugin).runTaskTimer(plugin, 0, 20);
		return true;
	}
}
