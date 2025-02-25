package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.enchant.GolemEnchantments;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.anawesomguy.snowiergolems.item.GolemHeadItem;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.item.enchantment.Enchantments.*;

public class EnchantmentTagsProvider extends net.minecraft.data.tags.EnchantmentTagsProvider {
    public EnchantmentTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper helper) {
        super(output, lookupProvider, SnowierGolems.MODID, helper);
    }

    @Override
    protected void addTags(@NotNull Provider provider) {
        this.tag(GolemHeadItem.SUPPORTED_ENCHANTS)
            .add(GolemEnchantments.ACCURACY,
                 GolemEnchantments.FROST,
                 FROST_WALKER,
                 PROTECTION,
                 FIRE_PROTECTION,
                 MULTISHOT,
                 POWER,
                 PUNCH,
                 FLAME,
                 KNOCKBACK);
    }
}
