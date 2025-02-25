package net.anawesomguy.snowiergolems.data;

import net.anawesomguy.snowiergolems.enchant.FreezeEffect;
import net.anawesomguy.snowiergolems.enchant.GolemEnchantments;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
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

import static net.anawesomguy.snowiergolems.GolemObjects.GOLEM_HEAD_ITEM;
import static net.anawesomguy.snowiergolems.SnowierGolems.id;
import static net.minecraft.world.item.enchantment.Enchantment.*;
import static net.minecraft.world.item.enchantment.Enchantment.dynamicCost;

public final class EnchantmentsDatagen {
    @SuppressWarnings("deprecation")
    public static void datagenEnchantments(BootstrapContext<Enchantment> ctx) {
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
                    dynamicCost(60, 20),
                    3,
                    EquipmentSlotGroup.HEAD
                )
            ).withEffect(
                EnchantmentEffectComponents.POST_ATTACK,
                EnchantmentTarget.ATTACKER,
                EnchantmentTarget.VICTIM,
                new FreezeEffect(LevelBasedValue.perLevel(1.5F, 1.1F))
            ).exclusiveWith(HolderSet.direct(enchantLookup.getOrThrow(Enchantments.FLAME)))
        );
    }

    public static void registerEnchant(BootstrapContext<Enchantment> ctx, ResourceKey<Enchantment> key,
                                       Enchantment.Builder builder) {
        ctx.register(key, builder.build(key.location()));
    }
}
