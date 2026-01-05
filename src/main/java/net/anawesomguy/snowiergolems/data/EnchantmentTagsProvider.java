package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.SnowierGolems;
import net.anawesomguy.snowiergolems.enchant.GolemEnchantments;
import net.anawesomguy.snowiergolems.item.GolemTomeItem;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EnchantmentTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.item.enchantment.Enchantments.*;

public class EnchantmentTagsProvider extends net.minecraft.data.tags.EnchantmentTagsProvider {
    public EnchantmentTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider) {
        super(output, lookupProvider, SnowierGolems.MODID);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(@NotNull Provider provider) {
        this.tag(GolemTomeItem.SUPPORTED_ENCHANTS)
            .add(GolemEnchantments.ACCURACY,
                 GolemEnchantments.AGGRESSIVE,
                 GolemEnchantments.FROST,
                 GolemEnchantments.HEAT_RESISTANT,
                 GolemEnchantments.SNOWY_LOYALTY,
                 FROST_WALKER,
                 PROTECTION,
                 MULTISHOT,
                 POWER,
                 PUNCH,
                 FLAME,
                 KNOCKBACK);
        this.tag(EnchantmentTags.IN_ENCHANTING_TABLE)
            .add(GolemEnchantments.ACCURACY,
                 GolemEnchantments.AGGRESSIVE,
                 GolemEnchantments.FROST,
                 GolemEnchantments.HEAT_RESISTANT,
                 GolemEnchantments.SNOWY_LOYALTY);
    }
}
