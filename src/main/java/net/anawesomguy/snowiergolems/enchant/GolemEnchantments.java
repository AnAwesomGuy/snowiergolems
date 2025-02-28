package net.anawesomguy.snowiergolems.enchant;

import net.anawesomguy.snowiergolems.SnowierGolems;
import net.anawesomguy.snowiergolems.data.EnchantmentDatagen;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attribute.Sentiment;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import static net.anawesomguy.snowiergolems.SnowierGolems.id;

/**
 * @see EnchantmentDatagen
 */
public interface GolemEnchantments {
    ResourceLocation PROJECTILE_ACCURACY_ID = SnowierGolems.id("projectile_accuracy");
    Holder.Reference<Attribute> PROJECTILE_ACCURACY = Registry.registerForHolder(
        BuiltInRegistries.ATTRIBUTE,
        PROJECTILE_ACCURACY_ID,
        new RangedAttribute("snowiergolems.attribute.name.projectile_accuracy", 0, -256, 256)
            .setSyncable(true)
            .setSentiment(Sentiment.POSITIVE)
    );

    TagKey<Item> GOLEM_HEAD_ENCHANTABLE = itemTag("enchantable/golem_head");

    ResourceKey<Enchantment> ACCURACY = key("accuracy");
    ResourceKey<Enchantment> AGGRESSIVE = key("aggressive");
    ResourceKey<Enchantment> FROST = key("frost");
    ResourceKey<Enchantment> HEAT_RESISTANT = key("heat_resistance");
    ResourceKey<Enchantment> SNOWY_LOYALTY = key("snowy_loyalty");

    static ResourceKey<Enchantment> key(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, id(path));
    }

    static TagKey<Item> itemTag(String path) {
        return TagKey.create(Registries.ITEM, id(path));
    }
}
