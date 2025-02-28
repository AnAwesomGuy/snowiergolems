package net.anawesomguy.snowiergolems.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@NonExtendable
public interface OwnableSnowGolem extends OwnableEntity {
    String NBT_TAG_KEY = "snowiergolems:owner";

    void snowiergolems$setOwner(@Nullable UUID uuid);

    default void snowiergolems$setOwner(@Nullable LivingEntity entity) {
        if (entity != null)
            snowiergolems$setOwner(entity.getUUID());
    }

    default boolean snowiergolems$isOwner(@Nullable Entity entity) {
        // implicit null-check
        return entity instanceof LivingEntity && entity.getUUID() == getOwnerUUID();
    }
}
