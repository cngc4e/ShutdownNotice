package net.pl3x.bukkit.shutdownnotice.configuration;

import java.util.List;

import net.pl3x.bukkit.shutdownnotice.Main;

public enum Config {
	COLOR_LOGS(true),
	DEBUG_MODE(false),
	LANGUAGE_FILE("lang-en.yml"),
	UPDATE_PING_MOTD(true),
	SHUTDOWN_COMMANDS(null),
	DISPLAY_INTERVALS(null);

	private Main plugin;
	private final Object def;

	private Config(Object def) {
		this.plugin = Main.getInstance();
		this.def = def;
	}

	public String getKey() {
		return name().toLowerCase().replace("_", "-");
	}

	public String getString() {
		return plugin.getConfig().getString(getKey(), (String) def);
	}

	public boolean getBoolean() {
		return plugin.getConfig().getBoolean(getKey(), (Boolean) def);
	}

	public List<String> getStringList() {
		return plugin.getConfig().getStringList(getKey());
	}
}
