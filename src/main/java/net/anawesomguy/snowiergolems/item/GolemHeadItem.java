package net.anawesomguy.snowiergolems.item;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class GolemHeadItem extends BlockItem {
    private static final Holder<SoundEvent> EQUIP_SOUND = Holder.direct(SoundEvents.SNOW_HIT);
    public static final Equipable EQUIPPABLE = new Equipable() {

        @Override
        public EquipmentSlot getEquipmentSlot() {
            return EquipmentSlot.HEAD;
        }

        @Override
        public Holder<SoundEvent> getEquipSound() {
            return EQUIP_SOUND;
        }
    };

    public GolemHeadItem(Block block, Properties properties) {
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
}
