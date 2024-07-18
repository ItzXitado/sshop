package io.github.xitadoo.sshop.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.xitadoo.sshop.models.History;
import io.github.xitadoo.sshop.models.User;
import io.github.xitadoo.sshop.util.Players;
import io.github.xitadoo.sshop.util.Sort;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class UserManagerCaffeine {
    private final Cache<UUID, User> users = Caffeine.newBuilder()
            .build();

    public User fetchUserWithId(UUID playerId) {
        return users.getIfPresent(playerId);
    }

    public User fetchUserWithName(String playerName) {
        return users.getIfPresent(Players.fetchPlayerUniqueId(playerName));
    }

    public void teste(User user) {

    }

    public void createUser(UUID playerId) {
        users.put(playerId, new User(playerId, new ArrayList<>()));
    }

    public History fetchHistoryByEntity(UUID playerId, EntityType entityType) {
        User user = users.getIfPresent(playerId);
        if (user != null) {
            return user.getPlayerHistory().stream()
                    .filter(history -> history.getSpawnerType().equals(entityType))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public void register(User user) {
        users.put(user.getPlayerId(), user);
    }

    public void throwSpawnerInHistory(UUID playerId, EntityType type) {
        User user = users.getIfPresent(playerId);
        if (user != null) {
            History history = fetchHistoryByEntity(playerId, type);
            if (history == null) {
                user.getPlayerHistory().add(new History(type, System.currentTimeMillis(), 1));
            } else {
                history.incrementAmountBought();
                history.setDateBought(System.currentTimeMillis());
            }
        }
    }

    public ArrayList<ItemStack> getSortedList(List<History> playerHistory, Sort sort) {
        ArrayList<ItemStack> inventoryItems = new ArrayList<>();
        switch (sort) {
            case DATE_REVERSED:
                playerHistory.stream()
                        .sorted(Comparator.comparingLong(History::getDateBought).reversed())
                        .map(History::asItemStack)
                        .forEach(inventoryItems::add);
                break;
            case DATE:
                playerHistory.stream()
                        .sorted(Comparator.comparingLong(History::getDateBought))
                        .map(History::asItemStack)
                        .forEach(inventoryItems::add);
                break;
            case AMOUNT:
                playerHistory.stream()
                        .sorted(Comparator.comparingLong(History::getAmountBought))
                        .map(History::asItemStack)
                        .forEach(inventoryItems::add);
                break;
            case AMOUNT_REVERSED:
                playerHistory.stream()
                        .sorted(Comparator.comparingLong(History::getAmountBought).reversed())
                        .map(History::asItemStack)
                        .forEach(inventoryItems::add);
                break;
        }
        return inventoryItems;
    }
}