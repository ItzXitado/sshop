package io.github.xitadoo.sshop.listener;

import io.github.xitadoo.sshop.manager.SpawnerManager;
import io.github.xitadoo.sshop.manager.UserManager;
import io.github.xitadoo.sshop.util.InvAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class InventoryClick implements Listener {

    private final Economy economy;
    private final UserManager userManager;
    private final SpawnerManager spawnerManager;
    
    public InventoryClick(Economy economy, UserManager userManager, SpawnerManager spawnerManager) {
        this.economy = economy;
        this.userManager = userManager;
        this.spawnerManager = spawnerManager;
    }

    @EventHandler
    public void onInventoryClickChangeSort(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getClickedInventory() == null) return;
        if (inventoryClickEvent.getInventory().getName().equalsIgnoreCase("§8Spawner History")) {
            InvAPI.ScrollerHolder inventoryHolder = (InvAPI.ScrollerHolder) inventoryClickEvent.getInventory().getHolder();
            Player player = (Player) inventoryClickEvent.getWhoClicked();
            if (inventoryClickEvent.getSlot() == 40) {
                if (inventoryClickEvent.isLeftClick()) {
                    inventoryHolder.setSortMethod(inventoryHolder.getSortMethod().other());
                } else if (inventoryClickEvent.isRightClick()) {
                    inventoryHolder.setSortMethod(inventoryHolder.getSortMethod().reverse());
                }
                inventoryHolder.replaceInventoryItems(userManager.getSortedList(userManager.fetchUserWithId(inventoryHolder.getOwner()).getPlayerHistory(), inventoryHolder.getSortMethod()));
                inventoryClickEvent.getWhoClicked().closeInventory();
                player.openInventory(inventoryHolder.getInventory());
            }
        }
    }

    @EventHandler
    public void buyItemSpawner(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getClickedInventory() == null) return;
        if (inventoryClickEvent.getClickedInventory().getName().equalsIgnoreCase("§8Spawner Shop")) {
            if (inventoryClickEvent.getCurrentItem() == null || inventoryClickEvent.getCurrentItem().getType().equals(Material.AIR))
                return;
            if (inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName().contains("Spawner")) {
                EntityType spawnerEntityType = EntityType.valueOf(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName().replace("§e", "").replace(" Spawner", "").toUpperCase());
                if (economy.getBalance((Player) inventoryClickEvent.getWhoClicked()) < spawnerManager.findSpawnerByType(spawnerEntityType).getCost()) {
                    inventoryClickEvent.getWhoClicked().sendMessage("§cYou dont have enough money for that.");
                    inventoryClickEvent.getWhoClicked().closeInventory();
                } else {
                    ItemStack itemStack = new ItemStack(Material.MOB_SPAWNER, 1);
                    BlockStateMeta spawnerMeta = (BlockStateMeta) itemStack.getItemMeta();
                    spawnerMeta.setDisplayName(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName());
                    itemStack.setItemMeta(spawnerMeta);
                    inventoryClickEvent.getWhoClicked().getInventory().addItem(itemStack);
                    economy.withdrawPlayer((Player) inventoryClickEvent.getWhoClicked(), spawnerManager.findSpawnerByType(spawnerEntityType).getCost());
                    inventoryClickEvent.getWhoClicked().sendMessage("§aThank you for trusting us to buy your Spawners :D");
                    userManager.throwSpawnerInHistory(inventoryClickEvent.getWhoClicked().getUniqueId(), spawnerEntityType);
                }
            }
        }
    }
}
