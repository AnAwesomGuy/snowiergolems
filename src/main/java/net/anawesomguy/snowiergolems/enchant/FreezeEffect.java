package net.anawesomguy.snowiergolems.enchant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNullByDefault;

@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public record FreezeEffect(LevelBasedValue freezeProgress) implements EnchantmentEntityEffect {
    public static final Identifier ID = SnowierGolems.id("freeze");
    public static final MapCodec<FreezeEffect> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
                                LevelBasedValue.CODEC.fieldOf("freeze_progress").forGetter(FreezeEffect::freezeProgress))
                            .apply(instance, FreezeEffect::new));

    @Override
    public void apply(ServerLevel level, int lvl, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        entity.setTicksFrozen(Math.min(entity.getTicksRequiredToFreeze(),
                                       (int)(freezeProgress.calculate(lvl) / 20F) + entity.getTicksFrozen()));
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
