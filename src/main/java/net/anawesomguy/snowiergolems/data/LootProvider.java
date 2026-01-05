package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class LootProvider extends LootTableProvider {
    public LootProvider(PackOutput output,
                        CompletableFuture<Provider> registries) {
        super(output, Set.of(),
              List.of(
                  new SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK),
                  new SubProviderEntry(GolemTomeLoot::new, LootContextParamSets.EMPTY)),
              registries);
    }

    public static class BlockLoot extends BlockLootSubProvider {
        protected BlockLoot(Provider registries) {
            super(Set.of(GolemObjects.GOLEM_HAT_ITEM), FeatureFlags.DEFAULT_FLAGS, registries);
        }

        @Override
        @NotNull
        protected Iterable<Block> getKnownBlocks() {
            return BuiltInRegistries.BLOCK.entrySet()
                                          .stream()
                                          .filter(entry -> SnowierGolems.MODID.equals(
                                              entry.getKey().identifier().getNamespace()))
                                          .map(Map.Entry::getValue)::iterator;
        }

        @Override
        protected void generate() {
            // drop golem hat with name and enchants
            Block golemHat = GolemObjects.GOLEM_HAT;
            add(golemHat,
                LootTable.lootTable()
                         .withPool(
                             applyExplosionCondition(
                                 golemHat,
                                 LootPool.lootPool()
                                         .setRolls(ConstantValue.exactly(1F))
                                         .add(LootItem.lootTableItem(golemHat)
                                                      .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(
                                                                                       LootContextParams.BLOCK_ENTITY)
                                                                                   .include(DataComponents.CUSTOM_NAME)
                                                                                   .include(DataComponents.ENCHANTMENTS)
                                                                                   .include(GolemObjects.PUMPKIN_FACE)))
                             )
                         ));
        }
    }

    public record GolemTomeLoot(Provider registries) implements LootTableSubProvider {
        public static final ResourceKey<LootTable> GOLEM_TOME_TABLE =
            ResourceKey.create(Registries.LOOT_TABLE, SnowierGolems.id("golem_tome"));
        public static final ResourceKey<LootTable> GOLEM_HAT_TABLE =
            ResourceKey.create(Registries.LOOT_TABLE, SnowierGolems.id("golem_hat"));

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, Builder> output) {
            output.accept(
                GOLEM_TOME_TABLE,
                LootTable.lootTable()
                         .withPool(
                             LootPool.lootPool()
                                     .add(LootItem.lootTableItem(GolemObjects.GOLEM_TOME)
                                                  .apply(EnchantWithLevelsFunction.enchantWithLevels(
                                                      registries, UniformGenerator.between(24F, 30F)
                                                  ))
                                     )));
            output.accept(
                GOLEM_HAT_TABLE,
                LootTable.lootTable()
                         .withPool(
                             LootPool.lootPool()
                                     .add(LootItem.lootTableItem(GolemObjects.GOLEM_HAT)))
                         .withPool(
                             LootPool.lootPool()
                                     .add(LootItem.lootTableItem(Items.SNOWBALL)
                                                  .apply(
                                                      SetItemCountFunction.setCount(UniformGenerator.between(2F, 5F))))
                                     .setBonusRolls(UniformGenerator.between(0F, 3F))
                         ));
        }
    }
}