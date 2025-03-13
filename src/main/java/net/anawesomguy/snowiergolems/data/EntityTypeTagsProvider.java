package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagsProvider extends net.minecraft.data.tags.EntityTypeTagsProvider {
    public EntityTypeTagsProvider(PackOutput output, CompletableFuture<Provider> provider,
                                  @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, SnowierGolems.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider provider) {
        tag(EntityTypeTags.ARROWS).add(GolemObjects.ENCHANTED_SNOWBALL);
    }
}
