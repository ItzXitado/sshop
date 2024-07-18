package io.github.xitadoo.sshop.util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Players {

    public static UUID fetchPlayerUniqueId(String playerName) {
        OfflinePlayer offlinePlayer = getOfflinePlayer(playerName);
        return offlinePlayer == null ? null : offlinePlayer.getUniqueId();
    }

    public static String fetchPlayerName(UUID playerUniqueId) {
        OfflinePlayer offlinePlayer = getOfflinePlayer(playerUniqueId);
        return offlinePlayer == null ? "Unknown" : offlinePlayer.getName();
    }

    public static OfflinePlayer getOfflinePlayer(String playerName) {
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            String offlinePlayerName = offlinePlayer.getName();
            if (offlinePlayerName != null && offlinePlayerName.equalsIgnoreCase(playerName)) {
                return offlinePlayer;
            }
        }

        return null;
    }

    public static OfflinePlayer getOfflinePlayer(UUID playerUniqueId) {
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getUniqueId().equals(playerUniqueId)) {
                return offlinePlayer;
            }
        }

        return null;
    }
}
