package net.pl3x.bukkit.shutdownnotice;

import net.pl3x.bukkit.shutdownnotice.command.CmdShutdown;
import net.pl3x.bukkit.shutdownnotice.configuration.Config;
import net.pl3x.bukkit.shutdownnotice.configuration.Lang;
import net.pl3x.bukkit.shutdownnotice.hook.DiscordHook;
import net.pl3x.bukkit.shutdownnotice.listener.CommandListener;
import net.pl3x.bukkit.shutdownnotice.listener.PingListener;
import net.pl3x.bukkit.shutdownnotice.task.Countdown;
import net.pl3x.bukkit.shutdownnotice.task.InternalClock;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ShutdownNotice extends JavaPlugin {
    private DiscordHook discordHook;
    private InternalClock internalClock;
    private Countdown countdown;

    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        new File(getDataFolder(), "restart").delete();

        if (getServer().getPluginManager().isPluginEnabled("Discord4Bukkit")) {
            discordHook = new DiscordHook();
        }

        internalClock = new InternalClock(this);

        Bukkit.getPluginManager().registerEvents(new CommandListener(), this);
        Bukkit.getPluginManager().registerEvents(new PingListener(this), this);

        getCommand("shutdown").setExecutor(new CmdShutdown(this));
    }

    public static ShutdownNotice getPlugin() {
        return ShutdownNotice.getPlugin(ShutdownNotice.class);
    }

    public InternalClock getInternalClock() {
        return internalClock;
    }

    public Countdown getCountdown() {
        return countdown;
    }

    public void setCountdown(Countdown countdown) {
        this.countdown = countdown;
    }

    public DiscordHook getDiscordHook() {
        return discordHook;
    }
}
