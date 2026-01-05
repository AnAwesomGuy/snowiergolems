package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagsProvider extends net.minecraft.data.tags.EntityTypeTagsProvider {
    public EntityTypeTagsProvider(PackOutput output, CompletableFuture<Provider> provider) {
        super(output, provider, SnowierGolems.MODID);
    }

    @Override
    protected void addTags(@NotNull Provider provider) {
        tag(EntityTypeTags.ARROWS).add(GolemObjects.ENCHANTED_SNOWBALL);
    }
}
