package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.SnowierGolems;
import net.anawesomguy.snowiergolems.data.LootProvider.GolemTomeLoot;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class LootModifierProvider extends GlobalLootModifierProvider {
    public LootModifierProvider(PackOutput output, CompletableFuture<Provider> registries) {
        super(output, registries, SnowierGolems.MODID);
    }

    @Override
    protected void start() {
        add("add_enchanted_golem_tomes",
            new AddTableLootModifier(
                new LootItemCondition[]{tableIdCondition("chests/igloo_chest")},
                GolemTomeLoot.GOLEM_TOME_TABLE
            ));
        add("add_golem_hat",
            new AddTableLootModifier(
                new LootItemCondition[]{tableIdCondition("chests/village/village_snowy_house")},
                GolemTomeLoot.GOLEM_TOME_TABLE
            ));
    }

    protected LootTableIdCondition tableIdCondition(String id) {
        return (LootTableIdCondition)new LootTableIdCondition.Builder(Identifier.parse(id)).build();
    }
}
