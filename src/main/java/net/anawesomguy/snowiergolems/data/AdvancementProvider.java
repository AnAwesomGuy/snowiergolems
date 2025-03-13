package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.anawesomguy.snowiergolems.SnowierGolems.id;
import static net.minecraft.advancements.Advancement.Builder.recipeAdvancement;
import static net.minecraft.advancements.critereon.EntityEquipmentPredicate.Builder.equipment;
import static net.minecraft.advancements.critereon.EntityPredicate.Builder.entity;
import static net.minecraft.advancements.critereon.ItemPredicate.Builder.item;
import static net.minecraft.advancements.critereon.SummonedEntityTrigger.TriggerInstance.summonedEntity;
import static net.minecraft.resources.ResourceLocation.withDefaultNamespace;

public class AdvancementProvider extends net.neoforged.neoforge.common.data.AdvancementProvider {
    public AdvancementProvider(PackOutput output,
                               CompletableFuture<Provider> registries,
                               ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(AdvancementProvider::generate));
    }

    @SuppressWarnings("removal")
    public static void generate(Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
        ItemStack golemHat = GolemObjects.GOLEM_HAT_ITEM.defaultStackWithoutFace();
        golemHat.set(GolemObjects.PUMPKIN_FACE, (byte)2);

        recipeAdvancement().parent(withDefaultNamespace("adventure/root"))
                           .display(golemHat,
                                    Component.translatable("snowiergolems.advancements.build_enchanted_golem"),
                                    Component.translatable("snowiergolems.advancements.build_enchanted_golem.description"),
                                    null,
                                    AdvancementType.TASK,
                                    true,
                                    true,
                                    false)
                           .addCriterion("summoned_enchanted_golem",
                                         summonedEntity(
                                             entity().of(EntityType.SNOW_GOLEM)
                                                     .equipment(
                                                         equipment().head(
                                                             item().withSubPredicate(ItemSubPredicates.ENCHANTMENTS,
                                                                                     ItemEnchantmentsPredicate.enchantments(
                                                                                         List.of(
                                                                                             new EnchantmentPredicate(
                                                                                                 Optional.empty(),
                                                                                                 Ints.ANY)
                                                                                         )))))))
                           .save(saver, id("build_enchanted_golem"), existingFileHelper);
    }

    public static AdvancementHolder getAdvancement(HolderGetter<Advancement> lookup, ResourceLocation id) {
        return new AdvancementHolder(id, lookup.getOrThrow(ResourceKey.create(Registries.ADVANCEMENT, id)).value());
    }
}
