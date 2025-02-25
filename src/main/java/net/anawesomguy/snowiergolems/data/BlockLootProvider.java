package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction.Source;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public class BlockLootProvider extends BlockLootSubProvider {
    protected BlockLootProvider(Provider registries) {
        super(Set.of(), FeatureFlagSet.of(), registries);
    }

    @Override
    protected void generate() {
        // drop golem head with name and enchants
        Block golemHead = GolemObjects.GOLEM_HEAD;
        add(golemHead,
            LootTable.lootTable()
                     .withPool(
                         applyExplosionCondition(
                             golemHead,
                             LootPool.lootPool()
                                     .setRolls(ConstantValue.exactly(1F))
                                     .add(LootItem.lootTableItem(golemHead)
                                                  .apply(CopyComponentsFunction.copyComponents(Source.BLOCK_ENTITY)
                                                                               .include(DataComponents.CUSTOM_NAME)
                                                                               .include(DataComponents.ENCHANTMENTS)))
                         )
                     ));
    }
}
