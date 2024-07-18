package io.github.xitadoo.sshop.listener;

import io.github.xitadoo.sshop.util.InvAPI.ScrollerHolder;
import io.github.xitadoo.sshop.util.SkullAPI;
import io.github.xitadoo.sshop.util.Sort;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class InventoryOpen implements Listener {

    private ItemStack sortItem(Sort sort) {
        ItemStack itemStack = SkullAPI.getByUrl("http://textures.minecraft.net/texture/1c6b9316ad145e6e63c7ef546a8cbcbfb28224293b3b6539d5725753a1cbdb26");
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("§aSort settings");

        switch (sort) {
            case DATE:
                meta.setLore(Arrays.asList(
                        "",
                        "§a• Sort by Date §d[→]",
                        "§7• Sort by Amount",
                        ""
                ));
                break;
            case DATE_REVERSED:
                meta.setLore(Arrays.asList(
                        "",
                        "§a• Sort by Date §d[←]",
                        "§7• Sort by Amount",
                        ""
                ));
                break;
            case AMOUNT:
                meta.setLore(Arrays.asList(
                        "",
                        "§7• Sort by Date",
                        "§a• Sort by Amount §d[-]",
                        ""));
                break;
            case AMOUNT_REVERSED:
                meta.setLore(Arrays.asList(
                        "",
                        "§7• Sort by Date",
                        "§a• Sort by Amount §d[+]",
                        ""));
                break;
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @EventHandler
    public void onHistoryInventoryOpen(InventoryOpenEvent inventoryOpenEvent) {
        if (inventoryOpenEvent.getInventory().getName().equalsIgnoreCase("§8Spawner History")) {
            ScrollerHolder scrollerHolder = (ScrollerHolder) inventoryOpenEvent.getInventory().getHolder();
            inventoryOpenEvent.getInventory().setItem(40, sortItem(scrollerHolder.getSortMethod()));
        }
    }
}
