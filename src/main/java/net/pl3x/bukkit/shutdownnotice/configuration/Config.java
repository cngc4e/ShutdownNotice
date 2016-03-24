package net.pl3x.bukkit.shutdownnotice.configuration;

import net.pl3x.bukkit.shutdownnotice.Main;

import java.util.List;

public enum Config {
    COLOR_LOGS(true),
    DEBUG_MODE(false),
    LANGUAGE_FILE("lang-en.yml"),
    UPDATE_PING_MOTD(true),
    SHUTDOWN_COMMANDS(null),
    DISPLAY_INTERVALS(null);

    private final Main plugin;
    private final Object def;

    Config(Object def) {
        this.plugin = Main.getPlugin(Main.class);
        this.def = def;
    }

    public static void reload() {
        Main.getPlugin(Main.class).reloadConfig();
    }

    private String getKey() {
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
