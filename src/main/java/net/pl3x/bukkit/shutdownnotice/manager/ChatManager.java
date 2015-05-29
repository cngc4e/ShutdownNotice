package net.pl3x.bukkit.shutdownnotice.manager;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.pl3x.bukkit.shutdownnotice.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ChatManager {
	public static void sendTitleMessage(Player player, int in, int show, int out, String title, String subtitle) {
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, in, show, out);
		connection.sendPacket(packetPlayOutTimes);
		if (subtitle != null) {
			IChatBaseComponent component = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
			connection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, component));
		}
		if (title != null) {
			IChatBaseComponent component = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
			connection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, component));
		}
	}

	public static void sendActionbarMessage(Player player, String message) {
		if (message == null || ChatColor.stripColor(message).equals("")) {
			return;
		}
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		IChatBaseComponent component = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
		connection.sendPacket(new PacketPlayOutChat(component, (byte) 2));
	}

	public static void sendChatMessage(CommandSender sender, String message) {
		if (message == null || ChatColor.stripColor(message).equals("")) {
			return;
		}
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public static void broadcastTitleMessage(int in, int show, int out, String title, String subtitle) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			sendTitleMessage(player, in, show, out, title, subtitle);
		}
	}

	public static void broadcastActionbarMessage(String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			sendActionbarMessage(player, message);
		}
	}

	public static void broadcastChatMessage(String message) {
		Logger.log(message); // use logger to honor color-codes config setting
		for (Player player : Bukkit.getOnlinePlayers()) {
			sendChatMessage(player, message);
		}
	}
}
