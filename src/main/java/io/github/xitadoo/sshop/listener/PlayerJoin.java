package io.github.xitadoo.sshop.listener;

import io.github.xitadoo.sshop.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void loadPlayerOnJoin(PlayerJoinEvent playerJoinEvent) { // Either loads or creates the user when he joins
        Main.getInstance().mongoDB.fetchUser(playerJoinEvent.getPlayer().getUniqueId().toString()).thenAccept(user -> {
            if (user == null) {
                Main.getInstance().userManager.createUser(playerJoinEvent.getPlayer().getUniqueId());
            } else {
                Main.getInstance().userManager.register(user);
            }
        });
    }
}
