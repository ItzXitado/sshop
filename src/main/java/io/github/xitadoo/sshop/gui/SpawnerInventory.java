package io.github.xitadoo.sshop.gui;

import io.github.xitadoo.sshop.models.Spawner;
import io.github.xitadoo.sshop.util.InvAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpawnerInventory {

    private final ArrayList<ItemStack> inventoryItems = new ArrayList<>();

    public SpawnerInventory(@NotNull List<Spawner> spawners, Player player) {

        spawners.stream()
                .sorted(Comparator.comparingDouble(Spawner::getCost).reversed())
                .map(Spawner::asItemStack)
                .forEach(inventoryItems::add);

        InvAPI mainInventory = new InvAPI.ScrollerBuilder().withName("§8Spawner Shop").withItems(inventoryItems).build();
        mainInventory.open(player);
    }
}
