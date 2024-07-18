package io.github.xitadoo.sshop.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;

public class CommandMapProvider {
    private static CommandMap commandMap;


    public static CommandMap getCommandMap() {
        if (commandMap != null) {
            return commandMap;
        }

        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);

            return (commandMap = (CommandMap) field.get(Bukkit.getServer()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get command map", e);
        }
    }
}
