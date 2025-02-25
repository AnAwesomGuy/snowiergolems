package net.anawesomguy.snowiergolems.item;

import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

public class GolemHeadItem extends BlockItem {
    public static final TagKey<Enchantment> SUPPORTED_ENCHANTS =
        TagKey.create(Registries.ENCHANTMENT, SnowierGolems.id("supports_golem_head"));

    public GolemHeadItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) || enchantment.is(SUPPORTED_ENCHANTS);
    }

    public static final class EquippableInstance implements Equipable {
        private static final Holder<SoundEvent> EQUIP_SOUND = Holder.direct(SoundEvents.SNOW_HIT);
        public static final EquippableInstance INSTANCE = new EquippableInstance();

        private EquippableInstance() {
        }

        @Override
        public EquipmentSlot getEquipmentSlot() {
            return EquipmentSlot.HEAD;
        }

        @Override
        public Holder<SoundEvent> getEquipSound() {
            return EQUIP_SOUND;
        }
    }
}
