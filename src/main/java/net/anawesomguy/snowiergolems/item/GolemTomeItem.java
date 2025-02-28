package net.anawesomguy.snowiergolems.item;

import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class GolemTomeItem extends Item {
    public static final TagKey<Enchantment> SUPPORTED_ENCHANTS =
        TagKey.create(Registries.ENCHANTMENT, SnowierGolems.id("supports_golem_tome"));

    public GolemTomeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) { // no enchants
        return !hasEnchantments(stack);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasEnchantments(stack);
    }

    @Override
    public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
        return super.isPrimaryItemFor(stack, enchantment) || enchantment.is(SUPPORTED_ENCHANTS);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) || enchantment.is(SUPPORTED_ENCHANTS);
    }

    public static boolean hasEnchantments(ItemStack stack) {
        return !stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty();
    }
}
