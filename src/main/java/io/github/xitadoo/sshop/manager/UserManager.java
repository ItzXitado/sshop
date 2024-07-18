package io.github.xitadoo.sshop.manager;

import io.github.xitadoo.sshop.models.History;
import io.github.xitadoo.sshop.models.User;
import io.github.xitadoo.sshop.util.Players;
import io.github.xitadoo.sshop.util.Sort;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    @Getter
    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    public User fetchUserWithId(UUID playerId) {
        return users.get(playerId);
    }

    public User fetchUserWithName(String playerName) {
        return users.get(Players.fetchPlayerUniqueId(playerName));
    }

    public void createUser(UUID playerId) { // Method creates a Non-existing user
        users.putIfAbsent(playerId, new User(playerId, new ArrayList<>()));
    }

    public History fetchHistoryByEntity(UUID playerId,EntityType entityType) {
        return users.get(playerId).getPlayerHistory().stream().filter(history -> history.getSpawnerType().equals(entityType)).findFirst().orElse(null);
    }

    public void register(User user) { //Method registers a user that is already on the database
        users.putIfAbsent(user.getPlayerId(), user);
    }

    public void throwSpawnerInHistory(UUID playerId, EntityType type) {
        if (fetchHistoryByEntity(playerId, type) == null) {
            fetchUserWithId(playerId).getPlayerHistory().add(new History(type, System.currentTimeMillis(), 1));
        }else {
            fetchHistoryByEntity(playerId, type).incrementAmountBought();
            fetchHistoryByEntity(playerId, type).setDateBought(System.currentTimeMillis());
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
