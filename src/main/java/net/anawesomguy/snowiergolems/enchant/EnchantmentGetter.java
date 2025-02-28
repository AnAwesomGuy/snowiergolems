package net.anawesomguy.snowiergolems.enchant;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Function;

public class EnchantmentGetter implements Function<Entity, Holder<Enchantment>> {
    private Holder<Enchantment> enchant;
    private final ResourceKey<Enchantment> enchantKey;

    public EnchantmentGetter(ResourceKey<Enchantment> enchantKey) {
        this.enchantKey = enchantKey;
    }

    @Override
    public Holder<Enchantment> apply(Entity entity) {
        Holder<Enchantment> enchant = this.enchant;
        return enchant == null ?
            (this.enchant = entity.registryAccess().holderOrThrow(enchantKey)) :
            enchant;
    }
}
