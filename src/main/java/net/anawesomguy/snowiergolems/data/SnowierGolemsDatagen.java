package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.data.client.BlockStateProvider;
import net.anawesomguy.snowiergolems.data.client.ItemModelProvider;
import net.anawesomguy.snowiergolems.data.client.SnowierLanguageProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static net.anawesomguy.snowiergolems.SnowierGolems.MODID;

@EventBusSubscriber(modid = MODID, bus = Bus.MOD)
public final class SnowierGolemsDatagen {
    @SubscribeEvent
    private static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        RegistrySetBuilder registrySet =
            new RegistrySetBuilder().add(Registries.ENCHANTMENT, EnchantmentDatagen::datagenEnchantments);
        @SuppressWarnings("DataFlowIssue")
        DatapackBuiltinEntriesProvider registriesProvider =
            new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), registrySet, null);
        CompletableFuture<Provider> lookupProvider = registriesProvider.getRegistryProvider();

        BlockTagsProvider blockTags = new BlockTagsProvider(output, lookupProvider, existingFileHelper);
        addProviders(event.includeServer(), gen,
                     registriesProvider,
                     blockTags,
                     new ItemTagsProvider(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper),
                     new EntityTypeTagsProvider(output, lookupProvider, existingFileHelper),
                     new EnchantmentTagsProvider(output, lookupProvider, existingFileHelper),
                     new RecipeProvider(output, lookupProvider),
                     new LootProvider(output, lookupProvider),
                     new LootModifierProvider(output, lookupProvider),
                     new AdvancementProvider(output, lookupProvider, existingFileHelper));
        addProviders(event.includeClient(), gen,
                     new BlockStateProvider(output, existingFileHelper),
                     new ItemModelProvider(output, existingFileHelper));
        addProviders(event.includeClient(), gen,
                     SnowierLanguageProvider.getAllLanguageProviders(output));
    }

    private static void addProviders(boolean run, DataGenerator gen, DataProvider... providers) {
        if (run)
            for (DataProvider provider : providers)
                gen.addProvider(true, provider);
    }
}
