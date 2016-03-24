package net.pl3x.bukkit.shutdownnotice;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ActionBar {
    private final String text;

    public ActionBar(String text) {
        this.text = text;
    }

    public void send(Player player) {
        if (text != null && !text.isEmpty()) {
            try {

                Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
                Object plrConnection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
                plrConnection.getClass().getMethod("sendPacket", ReflectionUtils.getNMSClass("Packet")).invoke(plrConnection,
                        ReflectionUtils.getNMSClass("PacketPlayOutChat")
                                .getConstructor(
                                        ReflectionUtils.getNMSClass("IChatBaseComponent"),
                                        byte.class)
                                .newInstance(
                                        ReflectionUtils.getNMSClass("IChatBaseComponent$ChatSerializer")
                                                .getMethod("a", String.class)
                                                .invoke(null, "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', text) + "\"}"),
                                        (byte) 2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcast() {
        Bukkit.getOnlinePlayers().forEach(this::send);
    }
}
