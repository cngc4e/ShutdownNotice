package net.pl3x.bukkit.shutdownnotice.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        String[] args = command.split(" ");
        if (args.length == 0 || !(args[0].equalsIgnoreCase("/restart") || args[0].equalsIgnoreCase("/rs"))) {
            return; // ignore
        }
        event.setMessage(command.replace("restart", "reboot"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onConsoleCommand(ServerCommandEvent event) {
        String command = event.getCommand();
        String[] args = command.split(" ");
        if (args.length == 0 || !(args[0].equalsIgnoreCase("/restart") || args[0].equalsIgnoreCase("/rs"))) {
            return; // ignore
        }
        event.setCommand(command.replace("restart", "reboot"));
    }
}
