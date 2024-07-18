package io.github.xitadoo.sshop.util;

import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.Map;

public class MobType {

    private static final Map<EntityType, String> entityTypeMap = new HashMap<>();
    static {
        entityTypeMap.put(EntityType.SPIDER, "MHF_Spider");
        entityTypeMap.put(EntityType.OCELOT, "Ozelot");
        entityTypeMap.put(EntityType.RABBIT, "MHF_Rabbit");
        entityTypeMap.put(EntityType.BLAZE, "MHF_Blaze");
        entityTypeMap.put(EntityType.CAVE_SPIDER, "MHF_CaveSpider");
        entityTypeMap.put(EntityType.PIG_ZOMBIE, "ZombiePigman");
        entityTypeMap.put(EntityType.IRON_GOLEM, "MHF_Golem");
        entityTypeMap.put(EntityType.PIG, "Pig");
        entityTypeMap.put(EntityType.SHEEP, "MHF_Sheep");
        entityTypeMap.put(EntityType.ZOMBIE, "Zombie");
        entityTypeMap.put(EntityType.COW, "MHF_Cow");
        entityTypeMap.put(EntityType.SKELETON, "MHF_Skeleton");
        entityTypeMap.put(EntityType.SLIME, "MHF_Slime");
        entityTypeMap.put(EntityType.MUSHROOM_COW, "MHF_Mushroom");
        entityTypeMap.put(EntityType.ENDERMAN, "MHF_Enderman");
        entityTypeMap.put(EntityType.CHICKEN, "MHF_Chicken");
        entityTypeMap.put(EntityType.CREEPER, "MHF_Creeper");
        entityTypeMap.put(EntityType.WITHER, "WitherSkeleton");
        entityTypeMap.put(EntityType.GHAST, "MHF_Ghast");
        entityTypeMap.put(EntityType.BAT, "MHF_Bat");
        entityTypeMap.put(EntityType.ENDERMITE, "MHF_Endermite");
        entityTypeMap.put(EntityType.GUARDIAN, "MHF_Guardian");
        entityTypeMap.put(EntityType.WITCH, "MHF_Witch");
        entityTypeMap.put(EntityType.VILLAGER, "MHF_Villager");
        entityTypeMap.put(EntityType.SQUID, "Squid");
        entityTypeMap.put(EntityType.WOLF, "MHF_Wolf");
        entityTypeMap.put(EntityType.HORSE, "MHF_Horse");
        entityTypeMap.put(EntityType.MAGMA_CUBE, "MHF_LavaSlime");
    }

    public static String mobHeadTranslator(EntityType entityType) {
        return entityTypeMap.getOrDefault(entityType, "MHF_" + entityType.getName());
    }

   /* public static String mobHeadTranslator(EntityType entityType) {
        String ret = entityTypeMap.get(entityType);
        if (ret == null) {
            ret = "MHF_" + entityType.getName();
        }
        return ret;
    }*/
}
