package net.pl3x.bukkit.shutdownnotice;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class Title {
    private final String title;
    private final String subtitle;

    public Title(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public void send(Player player) {
        try {
            Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object plrConnection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);

            resetTitle(plrConnection);

            sendTimings(plrConnection);
            sendTitle(plrConnection);

            if (subtitle != null && !subtitle.equals("")) {
                sendSubtitle(plrConnection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadcast() {
        Bukkit.getOnlinePlayers().forEach(this::send);
    }

    private void resetTitle(Object plrConnection) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        plrConnection.getClass().getMethod("sendPacket", ReflectionUtils.getNMSClass("Packet")).invoke(plrConnection,
                ReflectionUtils.getNMSClass("PacketPlayOutTitle")
                        .getConstructor(
                                ReflectionUtils.getNMSClass("PacketPlayOutTitle$EnumTitleAction"),
                                ReflectionUtils.getNMSClass("IChatBaseComponent"))
                        .newInstance(
                                ReflectionUtils.getNMSClass("PacketPlayOutTitle$EnumTitleAction")
                                        .getEnumConstants()[4],
                                null));
    }

    private void sendTimings(Object plrConnection) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        plrConnection.getClass().getMethod("sendPacket", ReflectionUtils.getNMSClass("Packet")).invoke(plrConnection,
                ReflectionUtils.getNMSClass("PacketPlayOutTitle")
                        .getConstructor(
                                ReflectionUtils.getNMSClass("PacketPlayOutTitle$EnumTitleAction"),
                                ReflectionUtils.getNMSClass("IChatBaseComponent"),
                                int.class,
                                int.class,
                                int.class)
                        .newInstance(
                                ReflectionUtils.getNMSClass("PacketPlayOutTitle$EnumTitleAction")
                                        .getEnumConstants()[2],
                                null,
                                5,
                                60,
                                10));
    }

    private void sendTitle(Object plrConnection) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        plrConnection.getClass().getMethod("sendPacket", ReflectionUtils.getNMSClass("Packet")).invoke(plrConnection,
                ReflectionUtils.getNMSClass("PacketPlayOutTitle")
                        .getConstructor(
                                ReflectionUtils.getNMSClass("PacketPlayOutTitle$EnumTitleAction"),
                                ReflectionUtils.getNMSClass("IChatBaseComponent"))
                        .newInstance(
                                ReflectionUtils.getNMSClass("PacketPlayOutTitle$EnumTitleAction")
                                        .getEnumConstants()[0],
                                ReflectionUtils.getNMSClass("IChatBaseComponent$ChatSerializer")
                                        .getMethod("a", String.class)
                                        .invoke(null, "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}")));
    }

    private void sendSubtitle(Object plrConnection) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        plrConnection.getClass().getMethod("sendPacket", ReflectionUtils.getNMSClass("Packet")).invoke(plrConnection,
                ReflectionUtils.getNMSClass("PacketPlayOutTitle")
                        .getConstructor(
                                ReflectionUtils.getNMSClass("PacketPlayOutTitle$EnumTitleAction"),
                                ReflectionUtils.getNMSClass("IChatBaseComponent"))
                        .newInstance(
                                ReflectionUtils.getNMSClass("PacketPlayOutTitle$EnumTitleAction")
                                        .getEnumConstants()[1],
                                ReflectionUtils.getNMSClass("IChatBaseComponent$ChatSerializer")
                                        .getMethod("a", String.class)
                                        .invoke(null, "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}")));
    }
}
