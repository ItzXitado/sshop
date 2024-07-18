package io.github.xitadoo.sshop.models;

import io.github.xitadoo.sshop.util.ItemBuilder;
import io.github.xitadoo.sshop.util.MobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Spawner {

    private EntityType entityType;
    private double cost;

    public ItemStack asItemStack() {
        return ItemBuilder.fromName(MobType.mobHeadTranslator(entityType))
                .name("§e"+transformTypeName(entityType.toString()) + " Spawner")
                .lore(
                        "",
                        "§7Price: §a" + String.format("%.0f", cost) + "$",
                        "",
                        "§eClick to buy §fx1",
                        "")
                .build();
    }

    private String transformTypeName(String s) {
        s = s.toLowerCase();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
