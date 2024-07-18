package io.github.xitadoo.sshop.manager;

import io.github.xitadoo.sshop.Main;
import io.github.xitadoo.sshop.models.Spawner;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class SpawnerManager {


    @Getter
    private final List<Spawner> spawnerList;

    public SpawnerManager() {
        this.spawnerList = new ArrayList<>();
        loadSpawners();
    }

    private void loadSpawners() {
        for (String key : Main.getInstance().config.getConfig().getConfigurationSection("Spawners").getKeys(false)) {
            try {
                Spawner spawner = new Spawner(EntityType.valueOf(key), Main.getInstance().config.getConfig().getInt("Spawners." + key));
                spawnerList.add(spawner);
            } catch (IllegalArgumentException e) {
                Bukkit.getConsoleSender().sendMessage("§a[SSHOP] §cConfig.yml Error: '§e" + key + "§c' Value in spawners config is not valid.");
            }
        }
        Bukkit.getConsoleSender().sendMessage("§a[SSHOP] §e" + spawnerList.size() + "§a spawners loaded.");
    }

    public Spawner findSpawnerByType(EntityType entityType) {
        return spawnerList.stream().filter(spawner -> spawner.getEntityType().equals(entityType)).findFirst().orElse(null);
    }
}
