package net.anawesomguy.snowiergolems.item;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.block.GolemHeadBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        Byte b = stack.get(GolemObjects.PUMPKIN_FACE);
        if (b != null && !GolemHeadBlockEntity.isValidFaceId(b))
            updatePumpkinFace(stack);
    }

    public static void updatePumpkinFace(ItemStack stack) {
        stack.set(GolemObjects.PUMPKIN_FACE, GolemHeadBlockEntity.calculateFaceId(null, stack.getTagEnchantments()));
    }

    @Override
    public ItemStack applyEnchantments(ItemStack stack, List<EnchantmentInstance> enchantments) {
        ItemStack newStack = super.applyEnchantments(stack, enchantments);
        Object2IntMap<Holder<Enchantment>> enchants = enchantments.stream()
                                                                  .collect(Object2IntOpenHashMap::new,
                                                                           (map, enchant) -> map.put(enchant.enchantment,
                                                                                                     enchant.level),
                                                                           Object2IntMap::putAll);
        newStack.set(GolemObjects.PUMPKIN_FACE,
                     GolemHeadBlockEntity.calculateFaceId(null, enchants::getInt, enchants.keySet(), null));
        return newStack;
    }
}
