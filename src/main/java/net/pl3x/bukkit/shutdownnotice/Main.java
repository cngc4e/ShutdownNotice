package net.pl3x.bukkit.shutdownnotice;

import java.io.File;
import java.io.IOException;

import net.pl3x.bukkit.shutdownnotice.ServerStatus.State;
import net.pl3x.bukkit.shutdownnotice.command.CmdShutdown;
import net.pl3x.bukkit.shutdownnotice.listener.CommandListener;
import net.pl3x.bukkit.shutdownnotice.listener.PingListener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class Main extends JavaPlugin {
	private static Main instance;
	private ServerStatus serverStatus;

	public static Main getInstance() {
		return instance;
	}

	public void onEnable() {
		instance = this;

		serverStatus = new ServerStatus();
		serverStatus.setStatus(State.RUNNING, null, null);

		new File(getDataFolder(), "restart").delete();

		saveDefaultConfig();

		Bukkit.getPluginManager().registerEvents(new CommandListener(), this);
		Bukkit.getPluginManager().registerEvents(new PingListener(this), this);

		getCommand("shutdown").setExecutor(new CmdShutdown(this));

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			Logger.log("&4Failed to start Metrics: &e" + e.getMessage());
		}

		Logger.log(getName() + " v" + getDescription().getVersion() + " enabled!");
	}

	public void onDisable() {
		Logger.log(getName() + " Disabled.");
		instance = null;
	}

	public ServerStatus getStatus() {
		return serverStatus;
	}
}
