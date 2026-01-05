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

import static net.anawesomguy.snowiergolems.GolemObjects.GOLEM_HAT_ITEM;
import static net.anawesomguy.snowiergolems.SnowierGolems.id;
import static net.minecraft.world.item.enchantment.Enchantment.*;

public final class EnchantmentDatagen {
    @SuppressWarnings("deprecation")
    public static void datagenEnchantments(BootstrapContext<Enchantment> ctx) {
        HolderGetter<Item> itemLookup = ctx.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantLookup = ctx.lookup(Registries.ENCHANTMENT);

        HolderSet<Item> golemHatEnchantable = itemLookup.getOrThrow(GolemEnchantments.GOLEM_HAT_ENCHANTABLE);
        HolderSet<Item> golemHat = HolderSet.direct(GOLEM_HAT_ITEM.builtInRegistryHolder());

        register(ctx,
                 GolemEnchantments.ACCURACY,
                 enchantment(definition(
                     golemHatEnchantable,
                     golemHat,
                     3,
                     3,
                     dynamicCost(15, 15),
                     dynamicCost(45, 18),
                     2,
                     EquipmentSlotGroup.HEAD
                 )).withEffect(
                     EnchantmentEffectComponents.ATTRIBUTES,
                     new EnchantmentAttributeEffect(
                         id("enchantment.projectile_accuracy"),
                         GolemEnchantments.PROJECTILE_ACCURACY,
                         LevelBasedValue.perLevel(3F),
                         Operation.ADD_VALUE
                     )
                 )
        );

        register(ctx,
                 GolemEnchantments.AGGRESSIVE,
                 enchantment(definition(
                     golemHatEnchantable,
                     golemHat,
                     2,
                     3,
                     dynamicCost(15, 15),
                     dynamicCost(50, 20),
                     3,
                     EquipmentSlotGroup.HEAD
                 ))
        );

        register(ctx,
                 GolemEnchantments.FROST,
                 enchantment(definition(
                     golemHatEnchantable,
                     golemHat,
                     2,
                     4,
                     dynamicCost(20, 15),
                     dynamicCost(60, 20),
                     3,
                     EquipmentSlotGroup.HEAD
                 )).withEffect(
                     EnchantmentEffectComponents.POST_ATTACK,
                     EnchantmentTarget.ATTACKER,
                     EnchantmentTarget.VICTIM,
                     new FreezeEffect(LevelBasedValue.perLevel(1.3F, 1F))
                 ).exclusiveWith(HolderSet.direct(enchantLookup.getOrThrow(Enchantments.FLAME)))
        );

        register(ctx,
                 GolemEnchantments.HEAT_RESISTANT,
                 enchantment(definition(
                     golemHatEnchantable,
                     golemHat,
                     3,
                     1,
                     dynamicCost(25, 0),
                     dynamicCost(45, 0),
                     4,
                     EquipmentSlotGroup.HEAD
                 ))
        );

        register(ctx,
                 GolemEnchantments.SNOWY_LOYALTY,
                 enchantment(definition(
                     golemHatEnchantable,
                     golemHat,
                     4,
                     1,
                     dynamicCost(30, 0),
                     dynamicCost(50, 0),
                     5,
                     EquipmentSlotGroup.HEAD
                 ))
        );
    }

    public static void register(BootstrapContext<Enchantment> ctx, ResourceKey<Enchantment> key,
                                Enchantment.Builder builder) {
        ctx.register(key, builder.build(key.identifier()));
    }
}
