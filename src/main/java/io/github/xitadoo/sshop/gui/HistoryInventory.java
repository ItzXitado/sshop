package io.github.xitadoo.sshop.gui;

import io.github.xitadoo.sshop.util.InvAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class HistoryInventory {

    public HistoryInventory(@NotNull ArrayList<ItemStack> inventoryItems, Player player, UUID owner) {
        InvAPI historyInventory;

        if (inventoryItems.isEmpty()) {
            inventoryItems.add(new ItemStack(Material.WEB) {{
                ItemMeta meta = getItemMeta();
                meta.setDisplayName("§cNo history to display yet.");
                setItemMeta(meta);
            }});
            historyInventory = new InvAPI.ScrollerBuilder().withName("§8Spawner History").withItems(inventoryItems).withItemsSlots(22).withOwnerUUID(owner).build();
        } else {
            historyInventory = new InvAPI.ScrollerBuilder().withName("§8Spawner History").withItems(inventoryItems).withOwnerUUID(owner).build();
        }
        historyInventory.open(player);
    }
}
