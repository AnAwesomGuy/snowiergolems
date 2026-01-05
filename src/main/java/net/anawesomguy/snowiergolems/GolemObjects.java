package net.anawesomguy.snowiergolems;

import com.mojang.serialization.codecs.PrimitiveCodec;
import net.anawesomguy.snowiergolems.block.GolemHatBlock;
import net.anawesomguy.snowiergolems.block.GolemHatBlockEntity;
import net.anawesomguy.snowiergolems.enchant.FreezeEffect;
import net.anawesomguy.snowiergolems.entity.EnchantedSnowball;
import net.anawesomguy.snowiergolems.item.GolemHatItem;
import net.anawesomguy.snowiergolems.item.GolemTomeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.EquipmentDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;

import static net.anawesomguy.snowiergolems.SnowierGolems.GOLEM_HAT_ID;
import static net.anawesomguy.snowiergolems.SnowierGolems.id;

public final class GolemObjects {
    private GolemObjects() {
        throw new AssertionError();
    }

    public static final DataComponentType<Byte> PUMPKIN_FACE =
        DataComponentType.<Byte>builder()
                         .networkSynchronized(ByteBufCodecs.BYTE)
                         .persistent(PrimitiveCodec.BYTE)
                         .build();

    @SuppressWarnings("deprecation") // otherwise the drops will also be overridden
    public static final GolemHatBlock GOLEM_HAT = new GolemHatBlock(
        Block.Properties.ofLegacyCopy(Blocks.CARVED_PUMPKIN)
                        .setId(ResourceKey.create(Registries.BLOCK, GOLEM_HAT_ID)));
    public static final GolemHatItem GOLEM_HAT_ITEM = new GolemHatItem(
        GOLEM_HAT,
        new Item.Properties().stacksTo(1)
                             .useBlockDescriptionPrefix()
                             .attributes(ItemAttributeModifiers.EMPTY)
                             .component(PUMPKIN_FACE, (byte)-1)
                             .component(DataComponents.EQUIPPABLE,
                                        Equippable.builder(EquipmentSlot.HEAD)
                                                  .setCanBeSheared(true)
                                                  .setEquipSound(Holder.direct(SoundEvents.SNOW_HIT))
                                                  .build())
                             .setId(ResourceKey.create(Registries.ITEM, GOLEM_HAT_ID)));
    public static final BlockEntityType<GolemHatBlockEntity> GOLEM_HAT_TYPE =
        new BlockEntityType<>(GolemHatBlockEntity::new, GOLEM_HAT);

    public static final Identifier GOLEM_TOME_ID = id("golem_tome");
    public static final GolemTomeItem GOLEM_TOME = new GolemTomeItem(
        new Item.Properties().stacksTo(1)
                             .rarity(Rarity.UNCOMMON)
                             .component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                             .setId(ResourceKey.create(Registries.ITEM, GOLEM_TOME_ID)));

    public static final Identifier ENCHANTED_SNOWBALL_ID = id("enchanted_snowball");
    public static final EntityType<EnchantedSnowball> ENCHANTED_SNOWBALL =
        EntityType.Builder.<EnchantedSnowball>of(EnchantedSnowball::new, MobCategory.MISC)
                          .sized(0.25F, 0.25F)
                          .clientTrackingRange(4)
                          .updateInterval(10)
                          .build(ResourceKey.create(Registries.ENTITY_TYPE, ENCHANTED_SNOWBALL_ID));

    public static final AttachmentType<@Nullable EntityReference<LivingEntity>> SNOW_GOLEM_OWNER =
        AttachmentType.<@Nullable EntityReference<LivingEntity>>builder(() -> null)
                      .serialize(EntityReference.<LivingEntity>codec().fieldOf("snow_golem_owner"))
                      .build();

    static void register(RegisterEvent event) {
        event.register(Registries.BLOCK, helper -> {
            helper.register(GOLEM_HAT_ID, GOLEM_HAT);
        });

        event.register(Registries.ITEM, helper -> {
            helper.register(GOLEM_HAT_ID, GOLEM_HAT_ITEM);
            helper.register(GOLEM_TOME_ID, GOLEM_TOME);
        });

        event.register(Registries.BLOCK_ENTITY_TYPE, helper -> {
            helper.register(GOLEM_HAT_ID, GOLEM_HAT_TYPE);
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

        event.register(Keys.ATTACHMENT_TYPES, helper -> {
            helper.register(id("snow_golem_owner"), SNOW_GOLEM_OWNER);
        });
    }

    static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS)
            event.accept(GOLEM_HAT_ITEM);

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            event.accept(GOLEM_TOME);
    }

    static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> DispenserBlock.registerBehavior(GOLEM_HAT_ITEM, new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Level level = source.level();
                Direction direction = source.state().getValue(DispenserBlock.FACING);
                BlockPos pos = source.pos().relative(direction);
                if (level.isEmptyBlock(pos) && GOLEM_HAT.canSpawnGolem(level, pos)) {
                    setSuccess(((BlockItem)stack.getItem()).place(
                        new DirectionalPlaceContext(level, pos, direction, stack, Direction.UP)
                    ).consumesAction());
                    stack.shrink(1);
                } else
                    this.setSuccess(EquipmentDispenseItemBehavior.dispenseEquipment(source, stack));
                return stack;
            }
        }));
    }

}
