package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.anawesomguy.snowiergolems.SnowierGolems.id;
import static net.minecraft.advancements.Advancement.Builder.recipeAdvancement;
import static net.minecraft.advancements.criterion.EntityEquipmentPredicate.Builder.equipment;
import static net.minecraft.advancements.criterion.EntityPredicate.Builder.entity;
import static net.minecraft.advancements.criterion.ItemPredicate.Builder.item;
import static net.minecraft.advancements.criterion.SummonedEntityTrigger.TriggerInstance.summonedEntity;
import static net.minecraft.resources.Identifier.withDefaultNamespace;

public class AdvancementProvider extends net.minecraft.data.advancements.AdvancementProvider {
    public AdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, List.of(AdvancementProvider::generate));
    }

    @SuppressWarnings("removal")
    public static void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver) {
        ItemStack golemHat = GolemObjects.GOLEM_HAT_ITEM.defaultStackWithoutFace();
        golemHat.set(GolemObjects.PUMPKIN_FACE, (byte)2);

        HolderLookup.RegistryLookup<EntityType<?>> entityLookup = registries.lookupOrThrow(Registries.ENTITY_TYPE);

        recipeAdvancement()
            .parent(withDefaultNamespace("adventure/root"))
            .display(golemHat,
                     Component.translatable("snowiergolems.advancements.build_enchanted_golem"),
                     Component.translatable(
                         "snowiergolems.advancements.build_enchanted_golem.description"),
                     null,
                     AdvancementType.TASK,
                     true,
                     true,
                     false)
            .addCriterion("summoned_enchanted_golem",
                          summonedEntity(
                              entity().of(entityLookup, EntityType.SNOW_GOLEM)
                                      .equipment(
                                          equipment().head(
                                              item().withComponents(
                                                        DataComponentMatchers.Builder.components()
                                                                                     .partial(
                                                                                         DataComponentPredicates.ENCHANTMENTS,
                                                                                         EnchantmentsPredicate.enchantments(
                                                                                             List.of(new EnchantmentPredicate(
                                                                                                 Optional.empty(),
                                                                                                 MinMaxBounds.Ints.ANY
                                                                                             ))
                                                                                         )
                                                                                     ).build()
                                                    ))))).save(saver, id("build_enchanted_golem"));
    }
}
