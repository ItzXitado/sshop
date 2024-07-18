package io.github.xitadoo.sshop.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ItemBuilder {

    private static final Map<String, ItemStack> HEADS_BY_NAME = new HashMap<>();

    private final ItemStack itemStack;


    public static ItemBuilder fromName(String playerName) {
        if (HEADS_BY_NAME.containsKey(playerName)) {
            return new ItemBuilder(HEADS_BY_NAME.get(playerName));
        }

        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner(playerName);
        itemStack.setItemMeta(skullMeta);

        HEADS_BY_NAME.put(playerName, itemStack);

        return new ItemBuilder(itemStack);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder name(String name) {
        return applyItemMeta(itemMeta -> itemMeta.setDisplayName(name));
    }

    public ItemBuilder lore(String... lore) {
        return applyItemMeta(itemMeta -> itemMeta.setLore(java.util.Arrays.asList(lore)));
    }

    public ItemBuilder applyItemStack(Consumer<ItemStack> itemStackConsumer) {
        itemStackConsumer.accept(itemStack);
        return this;
    }

    public ItemBuilder applyItemMeta(Consumer<ItemMeta> metaConsumer) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            itemMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        }

        metaConsumer.accept(itemMeta);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }
}
