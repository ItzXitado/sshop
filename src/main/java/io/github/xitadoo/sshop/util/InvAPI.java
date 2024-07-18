package io.github.xitadoo.sshop.util;


import io.github.xitadoo.sshop.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class InvAPI {

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                if (e.getInventory().getHolder() instanceof ScrollerHolder) {
                    e.setCancelled(true);
                    ScrollerHolder holder = (ScrollerHolder) e.getInventory().getHolder();
                    if (e.getSlot() == holder.getScroller().previousPage) {
                        if (holder.getScroller().hasPage(holder.getPage() - 1)) {
                            holder.getScroller().open((Player) e.getWhoClicked(), holder.getPage() - 1);
                        }
                    } else if (e.getSlot() == holder.getScroller().nextPage) {
                        if (holder.getScroller().hasPage(holder.getPage() + 1)) {
                            holder.getScroller().open((Player) e.getWhoClicked(), holder.getPage() + 1);
                        }
                    } else if (e.getSlot() == holder.getScroller().backSlot) {
                        e.getWhoClicked().closeInventory();
                        holder.getScroller().backRunnable.run((Player) e.getWhoClicked());
                    } else if (holder.getScroller().slots.contains(e.getSlot())
                            && holder.getScroller().onClickRunnable != null) {
                        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                            return;
                        holder.getScroller().onClickRunnable.run((Player) e.getWhoClicked(), e.getCurrentItem());
                    }
                }
            }
        }, Main.getPlugin(Main.class));
    }

    private ArrayList<ItemStack> items;
    private HashMap<Integer, Inventory> pages;
    private String name;
    private int inventorySize;
    private List<Integer> slots;
    private int backSlot;
    private int previousPage;
    private int nextPage;
    private UUID owner;
    private Sort sortMethod;

    private PlayerRunnable backRunnable;
    private ChooseItemRunnable onClickRunnable;

    public InvAPI(ScrollerBuilder builder) {
        this.items = builder.items;
        this.pages = new HashMap<>();
        this.name = builder.name;
        this.inventorySize = builder.inventorySize;
        this.slots = builder.slots;
        this.backSlot = builder.backSlot;
        this.backRunnable = builder.backRunnable;
        this.previousPage = builder.previousPage;
        this.nextPage = builder.nextPage;
        this.onClickRunnable = builder.clickRunnable;
        this.owner = builder.owner;
        this.sortMethod = builder.sortMethod;
        createInventories();
    }

    private void createInventories() {
        List<List<ItemStack>> lists = getPages(items, slots.size());
        int page = 1;
        for (List<ItemStack> list : lists) {
            Inventory inventory = Bukkit.createInventory(new ScrollerHolder(this, page, owner, sortMethod), inventorySize, name);
            ((ScrollerHolder) inventory.getHolder()).setInventory(inventory);
            int slot = 0;
            for (ItemStack it : list) {
                inventory.setItem(slots.get(slot), it);
                slot++;
            }
            if (page != 1)
                inventory.setItem(previousPage, getPageFlecha(page - 1)); // se for a primeira página, não tem pra onde
            // voltar
            inventory.setItem(nextPage, getPageFlecha(page + 1));
            if (backRunnable != null)
                inventory.setItem(backSlot, getBackFlecha());
            pages.put(page, inventory);
            page++;
        }
        pages.get(pages.size()).setItem(nextPage, new ItemStack(Material.AIR)); // vai na última página e remove a
        // flecha de ir pra frente
    }

    private ItemStack getBackFlecha() {
        ItemStack item = SkullAPI.getByUrl("http://textures.minecraft.net/texture/37aee9a75bf0df7897183015cca0b2a7d755c63388ff01752d5f4419fc645");
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Voltar");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getPageFlecha(int page) {
        ItemStack item = SkullAPI.getByUrl("http://textures.minecraft.net/texture/682ad1b9cb4dd21259c0d75aa315ff389c3cef752be3949338164bac84a96e");
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Página " + page);
        item.setItemMeta(meta);
        return item;
    }

    public int getPages() {
        return pages.size();
    }

    public boolean hasPage(int page) {
        return pages.containsKey(page);
    }

    public void open(Player player) {
        open(player, 1);
    }

    public void open(Player player, int page) {
        // player.closeInventory();
        player.openInventory(pages.get(page));
    }

    private <T> List<List<T>> getPages(Collection<T> c, Integer pageSize) { // créditos a
        // https://stackoverflow.com/users/2813377/pscuderi
        List<T> list = new ArrayList<T>(c);
        if (pageSize == null || pageSize <= 0 || pageSize > list.size())
            pageSize = list.size();
        int numPages = (int) Math.ceil((double) list.size() / (double) pageSize);
        List<List<T>> pages = new ArrayList<List<T>>(numPages);
        for (int pageNum = 0; pageNum < numPages; )
            pages.add(list.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, list.size())));
        return pages;
    }

    public class ScrollerHolder implements InventoryHolder {
        private InvAPI scroller;
        private int page;
        private UUID owner;
        private Sort sortMethod;
        private Inventory inventory;

        public ScrollerHolder(InvAPI scroller, int page, UUID owner, Sort sortMethod) {
            super();
            this.scroller = scroller;
            this.page = page;
            this.owner = owner;
            this.sortMethod = sortMethod;
        }

        @Override
        public Inventory getInventory() {
            return inventory; // Return the associated inventory
        }

        public void setInventory(Inventory inventory) {
            this.inventory = inventory; // Set the associated inventory
        }

        /**
         * @return the scroller
         */
        public InvAPI getScroller() {
            return scroller;
        }

        /**
         * @return the page
         */
        public int getPage() {
            return page;
        }

        public UUID getOwner() {
            return owner;
        }

        public Sort getSortMethod() {
            return sortMethod;
        }

        public void setSortMethod(Sort sortMethod) {
            this.sortMethod = sortMethod;
        }

        //NEW METHOD EXTRA API
        public void replaceInventoryItems(List<ItemStack> newInventoryItems) {
            if (inventory == null || newInventoryItems == null) {
                return; // Inventory or new items not set, nothing to replace
            }
            inventory.clear();
            for (int slot = 0; slot < newInventoryItems.size() && slot < slots.size(); slot++) {
                inventory.setItem(slots.get(slot), newInventoryItems.get(slot));
            }

            // Update arrow items for navigating between pages
            if (previousPage >= 0 && scroller.hasPage(page - 1)) {
                inventory.setItem(previousPage, getPageFlecha(page - 1));
            }
            if (nextPage >= 0 && scroller.hasPage(page + 1)) {
                inventory.setItem(nextPage, getPageFlecha(page + 1));
            }
            // Update the inventory in the pages map
            if (scroller != null && scroller.pages.containsKey(page)) {
                scroller.pages.put(page, inventory);
            }
            // Update items in the next pages
            int nextPageIndex = page + 1;
            List<List<ItemStack>> pages = getPages(newInventoryItems, slots.size());
            for (int i = nextPageIndex; i <= pages.size(); i++) {
                if (scroller.hasPage(i)) {
                    Inventory nextInventory = scroller.pages.get(i);
                    List<ItemStack> nextPageItems = pages.get(i - 1);
                    for (int slot : slots) {
                        int slotIndex = slot - slots.get(0); // Calculate the index within the slot list
                        if (slotIndex >= 0 && slotIndex < nextPageItems.size()) {
                            nextInventory.setItem(slot, nextPageItems.get(slotIndex));
                        }
                    }
                }
            }
        }
    }

    public interface PlayerRunnable {

        public void run(Player player);

    }

    public interface ChooseItemRunnable {
        public void run(Player player, ItemStack item);
    }

    public static class ScrollerBuilder {
        private ArrayList<ItemStack> items;
        private String name;
        private List<Integer> slots;
        private int backSlot;
        private int previousPage;
        private int nextPage;
        private int inventorySize;
        private PlayerRunnable backRunnable;
        private ChooseItemRunnable clickRunnable;

        private UUID owner;
        private Sort sortMethod;

        private final static List<Integer> ALLOWED_SLOTS = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23,
                24, 25, 28, 29, 30, 31, 32, 33, 34
                /* ,37,38,39,40,41,42,43 */); // slots para caso o inventário tiver 6 linhas

        public ScrollerBuilder() {
            // default values
            this.items = new ArrayList<>();
            this.name = "";
            this.inventorySize = 45;
            this.slots = ALLOWED_SLOTS;
            this.backSlot = -1;
            this.previousPage = 18;
            this.nextPage = 26;
            this.owner = null;
            this.sortMethod = Sort.AMOUNT_REVERSED;
        }

        public ScrollerBuilder withItems(ArrayList<ItemStack> items) {
            this.items = items;
            return this;
        }

        public ScrollerBuilder withOwnerUUID(UUID ownerUUID) {
            this.owner = ownerUUID;
            return this;
        }

        public ScrollerBuilder withSortMethod(Sort sortMethod) {
            this.sortMethod = sortMethod;
            return this;
        }

        public ScrollerBuilder withOnClick(ChooseItemRunnable clickRunnable) {
            this.clickRunnable = clickRunnable;
            return this;
        }

        public ScrollerBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ScrollerBuilder withSize(int size) {
            this.inventorySize = size;
            return this;
        }

        public ScrollerBuilder withArrowsSlots(int previousPage, int nextPage) {
            this.previousPage = previousPage;
            this.nextPage = nextPage;
            return this;
        }

        public ScrollerBuilder withBackItem(int slot, PlayerRunnable runnable) {
            this.backSlot = slot;
            this.backRunnable = runnable;
            return this;
        }

        public ScrollerBuilder withItemsSlots(Integer... slots) {
            this.slots = Arrays.asList(slots);
            return this;
        }

        public InvAPI build() {
            return new InvAPI(this);
        }

    }
}







