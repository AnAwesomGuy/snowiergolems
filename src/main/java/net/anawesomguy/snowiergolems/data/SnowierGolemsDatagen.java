package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.enchant.FreezeEffect;
import net.anawesomguy.snowiergolems.enchant.GolemEnchantments;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static net.anawesomguy.snowiergolems.GolemObjects.GOLEM_HEAD_ITEM;
import static net.anawesomguy.snowiergolems.SnowierGolems.MODID;
import static net.anawesomguy.snowiergolems.SnowierGolems.id;
import static net.minecraft.world.item.enchantment.Enchantment.*;

@EventBusSubscriber(modid = MODID, bus = Bus.MOD)
public final class SnowierGolemsDatagen {
    @SubscribeEvent
    private static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        RegistrySetBuilder registryBuilder =
            new RegistrySetBuilder().add(Registries.ENCHANTMENT, SnowierGolemsDatagen::datagenEnchantments);
        @SuppressWarnings("DataFlowIssue")
        DatapackBuiltinEntriesProvider registriesProvider = new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), registryBuilder, null);
        CompletableFuture<Provider> lookupProvider = registriesProvider.getRegistryProvider();

        BlockTagsProvider blockTags = new BlockTagsProvider(output, lookupProvider, existingFileHelper);
        addProviders(event.includeServer(), gen,
                     blockTags,
                     registriesProvider,
                     new ItemTagsProvider(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper),
                     new EnchantmentTagsProvider(output, lookupProvider, existingFileHelper),
                     new RecipeProvider(output, lookupProvider));
        addProviders(event.includeClient(), gen, new LanguageProvider(output));
    }

    @SuppressWarnings("deprecation")
    private static void datagenEnchantments(BootstrapContext<Enchantment> ctx) {
        HolderGetter<Item> itemLookup = ctx.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantLookup = ctx.lookup(Registries.ENCHANTMENT);

        registerEnchant(
            ctx,
            GolemEnchantments.ACCURACY,
            enchantment(
                definition(
                    itemLookup.getOrThrow(GolemEnchantments.GOLEM_HEAD_ENCHANTABLE),
                    HolderSet.direct(GOLEM_HEAD_ITEM.builtInRegistryHolder()),
                    3,
                    3,
                    dynamicCost(20, 18),
                    dynamicCost(65, 20),
                    3,
                    EquipmentSlotGroup.HEAD
                )
            ).withEffect(
                EnchantmentEffectComponents.ATTRIBUTES,
                new EnchantmentAttributeEffect(
                    id("enchantment.projectile_accuracy"),
                    GolemEnchantments.PROJECTILE_ACCURACY,
                    LevelBasedValue.perLevel(3F),
                    Operation.ADD_VALUE
                )
            )
        );

        registerEnchant(
            ctx,
            GolemEnchantments.FROST,
            enchantment(
                definition(
                    itemLookup.getOrThrow(GolemEnchantments.GOLEM_HEAD_ENCHANTABLE),
                    HolderSet.direct(GOLEM_HEAD_ITEM.builtInRegistryHolder()),
                    3,
                    4,
                    dynamicCost(15, 15),
                    dynamicCost(55, 20),
                    2,
                    EquipmentSlotGroup.HEAD
                )
            ).withEffect(
                EnchantmentEffectComponents.POST_ATTACK,
                EnchantmentTarget.ATTACKER,
                EnchantmentTarget.VICTIM,
                new FreezeEffect(LevelBasedValue.perLevel(1.5F, 0.8F))
            ).exclusiveWith(HolderSet.direct(enchantLookup.getOrThrow(Enchantments.FLAME)))
        );
    }

    private static void registerEnchant(BootstrapContext<Enchantment> ctx, ResourceKey<Enchantment> key,
                                        Enchantment.Builder builder) {
        ctx.register(key, builder.build(key.location()));
    }

    private static void addProviders(boolean run, DataGenerator gen, DataProvider... providers) {
        if (run)
            for (DataProvider provider : providers)
                gen.addProvider(true, provider);
    }
}
