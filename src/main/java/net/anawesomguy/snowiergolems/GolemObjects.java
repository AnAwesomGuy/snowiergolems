package net.anawesomguy.snowiergolems;

import com.mojang.serialization.codecs.PrimitiveCodec;
import net.anawesomguy.snowiergolems.block.GolemHeadBlock;
import net.anawesomguy.snowiergolems.block.GolemHeadBlockEntity;
import net.anawesomguy.snowiergolems.enchant.FreezeEffect;
import net.anawesomguy.snowiergolems.entity.EnchantedSnowball;
import net.anawesomguy.snowiergolems.item.GolemHeadItem;
import net.anawesomguy.snowiergolems.item.GolemTomeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import static net.anawesomguy.snowiergolems.SnowierGolems.id;

public final class GolemObjects {
    private GolemObjects() {
        throw new AssertionError();
    }

    public static final ResourceLocation GOLEM_HEAD_ID = id("golem_head");
    public static final GolemHeadBlock GOLEM_HEAD = new GolemHeadBlock(
        Block.Properties.of() // copied from carved pumpkin
                        .mapColor(MapColor.COLOR_ORANGE)
                        .strength(1F)
                        .sound(SoundType.WOOD)
                        .isValidSpawn(Blocks::always)
                        .pushReaction(PushReaction.DESTROY));
    public static final BlockItem GOLEM_HEAD_ITEM = new GolemHeadItem(
        GOLEM_HEAD,
        new Item.Properties().stacksTo(1)
                             .attributes(ItemAttributeModifiers.EMPTY.withTooltip(false)));
    @SuppressWarnings("DataFlowIssue")
    public static final BlockEntityType<GolemHeadBlockEntity> GOLEM_HEAD_TYPE =
        BlockEntityType.Builder.of(GolemHeadBlockEntity::new, GOLEM_HEAD).build(null);


    public static final DataComponentType<Byte> PUMPKIN_FACE =
        DataComponentType.<Byte>builder()
                         .networkSynchronized(ByteBufCodecs.BYTE)
                         .persistent(PrimitiveCodec.BYTE)
                         .build();
    public static final GolemTomeItem GOLEM_TOME = new GolemTomeItem(
        new Item.Properties().stacksTo(1)
                             .rarity(Rarity.UNCOMMON)
                             .component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                             .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                             .component(PUMPKIN_FACE, (byte)-1));

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

        event.register(Registries.DATA_COMPONENT_TYPE, helper -> {
            helper.register(id("pumpkin_face_id"), PUMPKIN_FACE);
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

    static {
        DispenserBlock.registerBehavior(GOLEM_HEAD_ITEM, new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Level level = source.level();
                BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                GolemHeadBlock golemHead = GOLEM_HEAD;
                if (level.isEmptyBlock(pos) && golemHead.canSpawnGolem(level, pos)) {
                    if (!level.isClientSide) {
                        level.setBlock(pos, golemHead.defaultBlockState(), 2 | 1);
                        level.gameEvent(null, GameEvent.BLOCK_PLACE, pos);
                    }

                    stack.shrink(1);
                    this.setSuccess(true);
                } else
                    this.setSuccess(ArmorItem.dispenseArmor(source, stack));
                return stack;
            }
        });
    }
}
