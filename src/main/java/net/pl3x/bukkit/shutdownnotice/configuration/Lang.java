package net.pl3x.bukkit.shutdownnotice.configuration;

import java.io.File;

import net.pl3x.bukkit.shutdownnotice.Logger;
import net.pl3x.bukkit.shutdownnotice.Main;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Lang {
	COMMAND_NO_PERMS("&4You do not have permission for that!"),
	COMMAND_MISSING_ARGS("&4Not enough arguments supplied!"),
	UNKNOWN_COMMAND("&4Unknown command!"),

	NOTHING_TO_CANCEL("&4Nothing to cancel here!"),
	PROCESS_CANCELLED("&dProcess cancelled!"),
	PROCESS_ALREADY_IN_ACTION("&4Process already in action!"),
	INVALID_TIME("&4Not a valid value for time!"),
	TIME_NOT_POSITIVE("&4Time must be a positive number!"),

	TIME_FORMAT("&7{minutes}&e:&7{seconds}"),

	SHUTTING_DOWN("Shutting Down"),
	RESTARTING("Restarting"),
	RIGHT_NOW("&e&lRIGHT NOW!!!"),

	PING_MESSAGE("&cServer is {action} in {time}"),
	KICK_MESSAGE("Server is {action}"),
	
	ACTIONBAR_TXT("&4&l{action} In {time}"),
	TITLE_TXT("{time}"),
	SUBTITLE_TXT("&4&l{action}"),
	CHAT_TXT("&4&l{action} in {time}");

	private Main plugin;
	private String def;

	private File configFile;
	private FileConfiguration config;

	private Lang(String def) {
		this.plugin = Main.getInstance();
		this.def = def;
		configFile = new File(plugin.getDataFolder(), Config.LANGUAGE_FILE.getString());
		saveDefault();
		reload();
	}

	public void reload() {
		config = YamlConfiguration.loadConfiguration(configFile);
	}

	public void saveDefault() {
		if (!configFile.exists()) {
			plugin.saveResource(Config.LANGUAGE_FILE.getString(), false);
		}
	}

	public String getKey() {
		return name().toLowerCase().replace("_", "-");
	}

	public String get() {
		String value = config.getString(getKey(), def);
		if (value == null) {
			Logger.log("Missing lang data: " + getKey());
			value = "&c[missing lang data]";
		}
		return ChatColor.translateAlternateColorCodes('&', value);
	}
}
