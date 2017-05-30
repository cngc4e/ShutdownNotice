package net.pl3x.bukkit.shutdownnotice.command;

import net.pl3x.bukkit.shutdownnotice.DateUtil;
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
            list.addAll(Stream.of("cancel", "abort", "stop", "reload")
                    .filter(possible -> possible.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList()));
        }
        return list;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("command.shutdown")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMS);
            return true;
        }

        if (args.length < 1) {
            Lang.send(sender, Lang.VERSION
                    .replace("{version}", plugin.getDescription().getVersion())
                    .replace("{plugin}", plugin.getName()));
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("command.shutdown.reload")) {
                Lang.send(sender, Lang.COMMAND_NO_PERMS);
                return true;
            }

            Config.reload();
            Lang.reload();

            Lang.send(sender, Lang.RELOAD
                    .replace("{version}", plugin.getDescription().getVersion())
                    .replace("{plugin}", plugin.getName()));
            return true;
        }

        if (args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("abort") || args[0].equalsIgnoreCase("stop")) {
            if (plugin.getCountdown() == null) {
                Lang.send(sender, Lang.NOTHING_TO_CANCEL);
                return true;
            }

            boolean restart = plugin.getCountdown().isRestart();
            plugin.getCountdown().cancel();
            plugin.setCountdown(null);

            Lang.send(sender, restart ? Lang.RESTART_CANCELLED : Lang.SHUTDOWN_CANCELLED);
            return true;
        }

        if (plugin.getCountdown() != null) {
            Lang.send(sender, Lang.PROCESS_ALREADY_IN_ACTION);
            return true;
        }

        boolean restart = label.equalsIgnoreCase("restart") || label.equalsIgnoreCase("reboot");

        long now = System.currentTimeMillis();
        long date = DateUtil.parseDateDiff(args[0]);

        if (date < 1) {
            Lang.send(sender, Lang.INVALID_TIME);
            return true;
        }

        long timeLeft = (date - now) / 1000;
        if (timeLeft < 1) {
            Lang.send(sender, Lang.TIME_NOT_POSITIVE);
            return true;
        }

        String reason = null;
        if (args.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            reason = sb.toString();
        }

        Countdown countdown = new Countdown(plugin, reason, restart, timeLeft);
        plugin.setCountdown(countdown);
        countdown.runTaskTimerAsynchronously(plugin, 0, 20L);
        return true;
    }
}
