package net.pl3x.bukkit.shutdownnotice;

import net.pl3x.bukkit.shutdownnotice.command.CmdShutdown;
import net.pl3x.bukkit.shutdownnotice.configuration.Config;
import net.pl3x.bukkit.shutdownnotice.configuration.Lang;
import net.pl3x.bukkit.shutdownnotice.listener.CommandListener;
import net.pl3x.bukkit.shutdownnotice.listener.PingListener;
import net.pl3x.bukkit.shutdownnotice.task.Countdown;
import net.pl3x.bukkit.shutdownnotice.task.InternalClock;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ShutdownNotice extends JavaPlugin {
    private InternalClock internalClock;
    private Countdown countdown;

    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        if (new File(getDataFolder(), "restart").delete()) {
            Logger.info("Cleaning up after restart.");
        }

        internalClock = new InternalClock(this);

        Bukkit.getPluginManager().registerEvents(new CommandListener(), this);
        Bukkit.getPluginManager().registerEvents(new PingListener(this), this);

        getCommand("shutdown").setExecutor(new CmdShutdown(this));

        Logger.info(getName() + " v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        Logger.info(getName() + " Disabled.");
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
}
