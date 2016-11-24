package net.pl3x.bukkit.shutdownnotice.command;

import net.pl3x.bukkit.shutdownnotice.Chat;
import net.pl3x.bukkit.shutdownnotice.ServerStatus;
import net.pl3x.bukkit.shutdownnotice.ServerStatus.State;
import net.pl3x.bukkit.shutdownnotice.ShutdownNotice;
import net.pl3x.bukkit.shutdownnotice.configuration.Config;
import net.pl3x.bukkit.shutdownnotice.configuration.Lang;
import net.pl3x.bukkit.shutdownnotice.task.Countdown;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CmdShutdown implements TabExecutor {
    private final ShutdownNotice plugin;

    public CmdShutdown(ShutdownNotice plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 0) {
            list.addAll(Stream.of("cancel", "abort", "stop", "off", "0", "version", "reload")
                    .filter(possible -> possible.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList()));
        }
        return list;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("command.shutdown")) {
            new Chat(Lang.COMMAND_NO_PERMS).send(sender);
            return true;
        }

        if (args.length < 1) {
            new Chat(Lang.COMMAND_MISSING_ARGS).send(sender);
            return false;
        }

        ServerStatus status = ServerStatus.getStatus();
        State state = status.getState();

        if (args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("abort") || args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("0")) {
            switch (state) {
                case RUNNING:
                    new Chat(Lang.NOTHING_TO_CANCEL).send(sender);
                    return true;
                case SHUTDOWN:
                    new Chat(Lang.SHUTDOWN_CANCELLED).broadcast();
                    break;
                case RESTART:
                    new Chat(Lang.RESTART_CANCELLED).broadcast();
                    break;
            }
            status.setStatus(State.RUNNING, null, null);
            return true;
        }

        if (args[0].equalsIgnoreCase("version")) {
            new Chat(Lang.VERSION
                    .replace("{plugin}", plugin.getName())
                    .replace("{version}", plugin.getDescription().getVersion()))
                    .send(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("command.shutdown.reload")) {
                new Chat(Lang.COMMAND_NO_PERMS).send(sender);
                return true;
            }

            Config.reload();
            Lang.reload(true);

            status.setStatus(State.RUNNING, null, null);

            new Chat(Lang.RELOAD
                    .replace("{plugin}", plugin.getName())
                    .replace("{version}", plugin.getDescription().getVersion()))
                    .send(sender);
            return true;
        }

        if (!state.equals(State.RUNNING)) {
            new Chat(Lang.PROCESS_ALREADY_IN_ACTION).send(sender);
            return true;
        }

        int time;
        try {
            time = Integer.valueOf(args[0]);
        } catch (NumberFormatException e) {
            new Chat(Lang.INVALID_TIME).send(sender);
            return true;
        }

        if (time <= 0) {
            new Chat(Lang.TIME_NOT_POSITIVE).send(sender);
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
            new Chat(Lang.UNKNOWN_COMMAND).send(sender);
            return true;
        }

        String reason = null;
        if (args.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            reason = sb.toString().trim();
        }

        status.setStatus(newState, time, reason);

        new Countdown(plugin).runTaskTimer(plugin, 0, 20);
        return true;
    }
}
