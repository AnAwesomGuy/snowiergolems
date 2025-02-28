package net.anawesomguy.snowiergolems.data;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementProvider extends net.neoforged.neoforge.common.data.AdvancementProvider {
    public AdvancementProvider(PackOutput output,
                               CompletableFuture<Provider> registries,
                               ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(AdvancementProvider::generate));
    }

    public static void generate(Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {

    }
}
