package net.pl3x.bukkit.shutdownnotice.hook;

import net.pl3x.bukkit.discord4bukkit.D4BPlugin;

public class DiscordHook {
    public void sendToDiscord(String message) {
        D4BPlugin.getInstance().getBot().sendMessageToDiscord(message);
    }
}
