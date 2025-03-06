package net.anawesomguy.snowiergolems.enchant;

import net.anawesomguy.snowiergolems.util.HolderCacher;
import net.minecraft.world.item.enchantment.Enchantment;

public interface EnchantmentCachers {
    HolderCacher<Enchantment> AGGRESSIVE_ENCHANT = new HolderCacher<>(GolemEnchantments.AGGRESSIVE);
    HolderCacher<Enchantment> SNOWY_LOYALTY_ENCHANT = new HolderCacher<>(GolemEnchantments.SNOWY_LOYALTY);
    HolderCacher<Enchantment> HEAT_RESIST_ENCHANT = new HolderCacher<>(GolemEnchantments.HEAT_RESISTANT);
}
