package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.data.client.ModelProvider;
import net.anawesomguy.snowiergolems.data.client.SnowierLanguageProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static net.anawesomguy.snowiergolems.SnowierGolems.MODID;

@EventBusSubscriber(modid = MODID)
public final class SnowierGolemsDatagen {
    @SubscribeEvent
    private static void gatherData(GatherDataEvent.Client event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();

        // client
        addProviders(gen, new ModelProvider(output));
        addProviders(gen, SnowierLanguageProvider.getAllLanguageProviders(output));

        // server
        RegistrySetBuilder registrySet =
            new RegistrySetBuilder().add(Registries.ENCHANTMENT, EnchantmentDatagen::datagenEnchantments);
        @SuppressWarnings("DataFlowIssue")
        DatapackBuiltinEntriesProvider registriesProvider =
            new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), registrySet, null);
        CompletableFuture<Provider> lookupProvider = registriesProvider.getRegistryProvider();

        BlockTagsProvider blockTags = new BlockTagsProvider(output, lookupProvider);
        addProviders(gen,
                     registriesProvider,
                     blockTags,
                     new ItemTagsProvider(output, lookupProvider, blockTags.contentsGetter()),
                     new EntityTypeTagsProvider(output, lookupProvider),
                     new EnchantmentTagsProvider(output, lookupProvider),
                     new RecipeProvider.Runner(output, lookupProvider),
                     new LootProvider(output, lookupProvider),
                     new LootModifierProvider(output, lookupProvider),
                     new AdvancementProvider(output, lookupProvider));
    }

    private static void addProviders(DataGenerator gen, DataProvider... providers) {
        for (DataProvider provider : providers)
            gen.addProvider(true, provider);
    }
}
