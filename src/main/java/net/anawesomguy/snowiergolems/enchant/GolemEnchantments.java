package net.anawesomguy.snowiergolems.enchant;

import net.anawesomguy.snowiergolems.SnowierGolems;
import net.anawesomguy.snowiergolems.data.EnchantmentDatagen;
import net.anawesomguy.snowiergolems.entity.SnowGolemFollowOwnerGoal;
import net.anawesomguy.snowiergolems.entity.SnowGolemOwnerHurtByTargetGoal;
import net.anawesomguy.snowiergolems.entity.SnowGolemOwnerHurtTargetGoal;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attribute.Sentiment;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import static net.anawesomguy.snowiergolems.SnowierGolems.id;

/**
 * @see EnchantmentDatagen
 */
public interface GolemEnchantments {
    Identifier PROJECTILE_ACCURACY_ID = SnowierGolems.id("projectile_accuracy");
    Holder.Reference<Attribute> PROJECTILE_ACCURACY = Registry.registerForHolder(
        BuiltInRegistries.ATTRIBUTE,
        PROJECTILE_ACCURACY_ID,
        new RangedAttribute("snowiergolems.attribute.name.projectile_accuracy", 0, -256, 256)
            .setSyncable(true)
            .setSentiment(Sentiment.POSITIVE)
    );

    TagKey<Item> GOLEM_HAT_ENCHANTABLE = itemTag("enchantable/golem_hat");

    /**
     * Increases the accuracy of the snow golem's snowballs.
     * (3 per level for three levels, base is -10.5)
     *
     * @see #PROJECTILE_ACCURACY
     */
    ResourceKey<Enchantment> ACCURACY = key("accuracy");
    /**
     * Makes snow golems aggressive.
     * <ul>
     *     <li> default: targets only {@link Enemy}s
     *     <li> level 1: targets those that attack it
     *     <li> level 2: targets those that attack it and alerts others to do so
     *     <li> level 3: automatically targets everything but its owner
     * </ul>
     *
     * @see HurtByTargetGoal
     * @see NearestAttackableTargetGoal
     */
    ResourceKey<Enchantment> AGGRESSIVE = key("aggressive");
    /**
     * Makes the snow golem's projectile give "frost" (the powdered snow effect) when it hits an entity.
     * <p>
     * Gives a base of 1.3 seconds plus 1 second per level after the first.
     * <p>
     * Incompatible with {@link Enchantments#FLAME}.
     *
     * @see FreezeEffect
     */
    ResourceKey<Enchantment> FROST = key("frost");
    /**
     * Makes snow golems resistant to "hot" biomes.
     *
     * @see EnvironmentAttributes#SNOW_GOLEM_MELTS
     */
    ResourceKey<Enchantment> HEAT_RESISTANT = key("heat_resistance");
    /**
     * Makes snow golems follow its owner (the player closest to it during its creation) and
     * target mobs that have attacked its owner and mobs that its owner has attacked.
     *
     * @see SnowGolemFollowOwnerGoal
     * @see SnowGolemOwnerHurtByTargetGoal
     * @see SnowGolemOwnerHurtTargetGoal
     */
    ResourceKey<Enchantment> SNOWY_LOYALTY = key("snowy_loyalty");

    static ResourceKey<Enchantment> key(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, id(path));
    }

    static TagKey<Item> itemTag(String path) {
        return TagKey.create(Registries.ITEM, id(path));
    }
}
