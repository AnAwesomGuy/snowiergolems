package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.enchant.GolemEnchantments;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {
    public ItemTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
                            CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, SnowierGolems.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull Provider provider) {
        this.tag(GolemEnchantments.GOLEM_HEAD_ENCHANTABLE)
            .add(GolemObjects.GOLEM_HEAD_ITEM);
    }
}
