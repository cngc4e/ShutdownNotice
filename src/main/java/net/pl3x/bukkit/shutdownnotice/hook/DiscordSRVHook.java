package net.pl3x.bukkit.shutdownnotice.hook;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;

public class DiscordSRVHook {
    public void sendToDiscord(String message) {
        DiscordUtil.sendMessage(DiscordSRV.getPlugin().getMainTextChannel(), DiscordUtil.stripColor(message));
    }
}
