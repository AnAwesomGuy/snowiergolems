package net.anawesomguy.snowiergolems;

import net.anawesomguy.snowiergolems.block.GolemHeadBlock;
import net.anawesomguy.snowiergolems.block.GolemHeadBlockEntity;
import net.anawesomguy.snowiergolems.enchant.FreezeEffect;
import net.anawesomguy.snowiergolems.entity.EnchantedSnowball;
import net.anawesomguy.snowiergolems.item.GolemHeadItem;
import net.anawesomguy.snowiergolems.item.GolemTomeItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import static net.anawesomguy.snowiergolems.SnowierGolems.id;

@SuppressWarnings("deprecation")
public final class GolemObjects {
    private GolemObjects() {
        throw new AssertionError();
    }

    public static final ResourceLocation GOLEM_HEAD_ID = id("golem_head");
    public static final GolemHeadBlock GOLEM_HEAD = new GolemHeadBlock(Block.Properties.ofLegacyCopy(Blocks.CARVED_PUMPKIN));
    public static final BlockItem GOLEM_HEAD_ITEM = new GolemHeadItem(
        GOLEM_HEAD,
        new Item.Properties().stacksTo(1)
                             .attributes(ItemAttributeModifiers.EMPTY.withTooltip(false)));
    @SuppressWarnings("DataFlowIssue")
    public static final BlockEntityType<GolemHeadBlockEntity> GOLEM_HEAD_TYPE =
        Builder.of(GolemHeadBlockEntity::new, GOLEM_HEAD).build(null);

    public static final GolemTomeItem GOLEM_TOME = new GolemTomeItem(
        new Item.Properties().stacksTo(1)
                             .rarity(Rarity.UNCOMMON)
                             .component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                             .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    );

    public static final ResourceLocation ENCHANTED_SNOWBALL_ID = id("enchanted_snowball");
    public static final EntityType<EnchantedSnowball> ENCHANTED_SNOWBALL =
        EntityType.Builder.<EnchantedSnowball>of(EnchantedSnowball::new, MobCategory.MISC)
                          .sized(0.25F, 0.25F)
                          .clientTrackingRange(4)
                          .updateInterval(10)
                          .build(ENCHANTED_SNOWBALL_ID.getPath());

    static void register(RegisterEvent event) {
        event.register(Registries.BLOCK, helper -> {
            helper.register(GOLEM_HEAD_ID, GOLEM_HEAD);
        });

        event.register(Registries.ITEM, helper -> {
            helper.register(GOLEM_HEAD_ID, GOLEM_HEAD_ITEM);
            helper.register(id("golem_tome"), GOLEM_TOME);
        });

        event.register(Registries.BLOCK_ENTITY_TYPE, helper -> {
            helper.register(GOLEM_HEAD_ID, GOLEM_HEAD_TYPE);
        });

        event.register(Registries.ENTITY_TYPE, helper -> {
            helper.register(ENCHANTED_SNOWBALL_ID, ENCHANTED_SNOWBALL);
        });

        event.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, helper -> {
            helper.register(FreezeEffect.ID, FreezeEffect.CODEC);
        });
    }

    static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(GOLEM_HEAD_ITEM);
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(GOLEM_TOME);
        }
    }
}
