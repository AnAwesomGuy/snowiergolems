package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.anawesomguy.snowiergolems.enchant.GolemEnchantments;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ItemTagsProvider extends BlockTagCopyingItemTagProvider {
    public ItemTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
                            CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags, SnowierGolems.MODID);
    }

    @Override
    protected void addTags(@NotNull Provider provider) {
        this.tag(GolemEnchantments.GOLEM_HAT_ENCHANTABLE)
            .add(GolemObjects.GOLEM_HAT_ITEM);
    }
}
