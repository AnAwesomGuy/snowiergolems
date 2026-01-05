package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BlockTagsProvider extends net.neoforged.neoforge.common.data.BlockTagsProvider {
    public BlockTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider) {
        super(output, lookupProvider, SnowierGolems.MODID);
    }

    @Override
    protected void addTags(@NotNull Provider provider) {
    }
}
