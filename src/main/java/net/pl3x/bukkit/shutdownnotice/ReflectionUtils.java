package net.pl3x.bukkit.shutdownnotice;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class ReflectionUtils {
    private final static Map<String, Class<?>> cache = new HashMap<>();

    public static Class<?> getNMSClass(String nmsClassName) throws ClassNotFoundException {
        if (!cache.containsKey(nmsClassName)) {
            String nmsClassPath = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName;
            cache.put(nmsClassName, Class.forName(nmsClassPath));
        }
        return cache.get(nmsClassName);
    }
}
