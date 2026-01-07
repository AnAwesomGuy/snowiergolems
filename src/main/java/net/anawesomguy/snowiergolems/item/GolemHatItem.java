package net.anawesomguy.snowiergolems.item;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.block.GolemHatBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GolemHatItem extends BlockItem {
    public GolemHatItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) || enchantment.is(GolemTomeItem.SUPPORTED_ENCHANTS);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        setPumpkinFace(stack);
        return stack;
    }

    public static void setPumpkinFace(ItemStack stack) {
        stack.set(GolemObjects.PUMPKIN_FACE, GolemHatBlockEntity.calculateFaceId(null, stack.getTagEnchantments()));
    }

    @Override
    public ItemStack applyEnchantments(ItemStack stack, List<EnchantmentInstance> enchantments) {
        ItemStack newStack = super.applyEnchantments(stack, enchantments);
        ItemEnchantments tagEnchantments = stack.getTagEnchantments();
        newStack.set(GolemObjects.PUMPKIN_FACE,
                     GolemHatBlockEntity.calculateFaceId(null, tagEnchantments));
        return newStack;
    }

    public ItemStack defaultStackWithoutFace() {
        ItemStack stack = super.getDefaultInstance();
        stack.remove(GolemObjects.PUMPKIN_FACE);
        return stack;
    }
}
