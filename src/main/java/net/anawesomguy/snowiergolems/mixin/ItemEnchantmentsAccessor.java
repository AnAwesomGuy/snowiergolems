package net.anawesomguy.snowiergolems.mixin;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemEnchantments.class)
public interface ItemEnchantmentsAccessor {
    @Accessor
    static Codec<Object2IntOpenHashMap<Holder<Enchantment>>> getLEVELS_CODEC() {
        throw new AssertionError();
    }

    @Invoker("<init>")
    static ItemEnchantments createInstance(Object2IntOpenHashMap<Holder<Enchantment>> enchantments, boolean showInTooltip) {
        throw new AssertionError();
    }
}
