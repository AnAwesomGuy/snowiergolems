package net.anawesomguy.snowiergolems.block;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.anawesomguy.snowiergolems.enchant.GolemEnchantments;
import net.anawesomguy.snowiergolems.mixin.ItemEnchantmentsAccessor;
import net.anawesomguy.snowiergolems.util.HolderCacher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.function.ToIntFunction;

public class GolemHatBlockEntity extends BlockEntity implements Nameable {
    @SuppressWarnings("deprecation")
    private static final RandomSource FALLBACK_RANDOM = RandomSource.createThreadSafe();

    public static final byte NORMAL_FACE_COUNT = 4;
    public static final byte ANGRY_FACE_COUNT = 2;
    public static final byte LIT_FACE_COUNT = 2;
    // and then frost face, one eyed, and three eyed
    public static final byte TOTAL_FACES = NORMAL_FACE_COUNT + ANGRY_FACE_COUNT + LIT_FACE_COUNT + 3;

    public static final String ENCHANTMENTS_TAG = "enchantments";
    public static final String FACE_ID_TAG = "pumpkin_face_id";

    private static final Codec<Object2IntOpenHashMap<Holder<Enchantment>>> ENCHANTS_CODEC =
        ItemEnchantmentsAccessor.getLEVELS_CODEC();

    @NotNull // can always be assumed to be mutable
    protected final Object2IntOpenHashMap<Holder<Enchantment>> enchantments = new Object2IntOpenHashMap<>();
    @Nullable
    protected Component name;
    protected byte faceId;

    public GolemHatBlockEntity(BlockPos pos, BlockState state) {
        super(GolemObjects.GOLEM_HAT_TYPE, pos, state);
    }

    public ItemStack getAsStack() {
        ItemStack stack = new ItemStack(GolemObjects.GOLEM_HAT_ITEM);
        stack.applyComponents(this.collectComponents());
        return stack;
    }

    public void update(@Nullable Level level) {
        if (level == null)
            level = this.level;

        if (level != null && !level.isClientSide) {
            BlockPos pos = this.getBlockPos();
            AuxiliaryLightManager auxLight = level.getAuxLightManager(pos);
            if (auxLight != null)
                if (hasEnchantment(HolderCacher.getAsHolder(Enchantments.FLAME, level))) {
                    auxLight.setLightAt(pos, 15);
                    level.getChunkSource().getLightEngine().checkBlock(pos);
                } else
                    auxLight.removeLightAt(pos);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);

        if (!enchantments.isEmpty())
            tag.put(ENCHANTMENTS_TAG,
                    ENCHANTS_CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), enchantments)
                                  .getOrThrow());

        if (this.name != null)
            tag.putString("CustomName", Serializer.toJson(this.name, registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains(ENCHANTMENTS_TAG))
            ENCHANTS_CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get(ENCHANTMENTS_TAG))
                          .resultOrPartial(err -> SnowierGolems.LOGGER.error("Failed to load golem hat enchantments: '{}'", err))
                          .ifPresent(this::setEnchantments);

        if (tag.contains(FACE_ID_TAG, Tag.TAG_BYTE))
            this.faceId = tag.getByte(FACE_ID_TAG);

        if (tag.contains("CustomName", Tag.TAG_STRING))
            this.name = parseCustomNameSafe(tag.getString("CustomName"), registries);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        ItemEnchantments enchants = componentInput.get(DataComponents.ENCHANTMENTS);
        if (enchants != null && !enchants.isEmpty())
            setEnchantments(enchants.entrySet());
        this.faceId = componentInput.getOrDefault(GolemObjects.PUMPKIN_FACE, (byte)-1);
        this.name = componentInput.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.ENCHANTMENTS,
                       ItemEnchantmentsAccessor.createInstance(this.enchantments, true));
        components.set(GolemObjects.PUMPKIN_FACE, this.faceId);
        components.set(DataComponents.CUSTOM_NAME, this.name);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(ENCHANTMENTS_TAG);
        tag.remove(FACE_ID_TAG);
        tag.remove("CustomName");
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public Component getName() {
        return name == null ? getBlockState().getBlock().getName() : name;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return name;
    }

    public void setCustomName(@Nullable Component customName) {
        this.name = customName;
    }


    public byte getOrCreateFaceId() {
        if (isValidFaceId(faceId))
            return faceId;
        return faceId = calculateFaceId(level == null ? null : level.random, this::getLevel, enchantments.keySet(), level);
    }

    public static boolean isValidFaceId(byte id) {
        return id < TOTAL_FACES && id >= 0;
    }

    // zero-indexed
    public static byte calculateFaceId(@Nullable RandomSource random, ToIntFunction<Holder<Enchantment>> levelGetter,
                                       @Nullable Set<Holder<Enchantment>> keySet,
                                       @Nullable Object obj /*obj to get the lookup from, null is ok*/) {
        if (random == null)
            random = FALLBACK_RANDOM;
        if (keySet != null && !keySet.isEmpty()) {
            if (obj == null)
                for (Holder<Enchantment> enchantment : keySet)
                    if (enchantment instanceof Reference<Enchantment>) {
                        obj = enchantment;
                        break;
                    }

            float f = random.nextFloat();
            int frostLevel = levelGetter.applyAsInt(HolderCacher.getAsHolder(GolemEnchantments.FROST, obj));
            if (frostLevel > 0 && f < (1 - 0.7F / (2 + frostLevel))) // 1 => 65%, 3 => 82.5% :)
                return TOTAL_FACES - 1; // frost face is the last face (its also 0-indexed)

            boolean b = random.nextBoolean();
            int aggressiveLevel = levelGetter.applyAsInt(HolderCacher.AGGRESSIVE_ENCHANT.apply(obj));
            if (aggressiveLevel > 0 && f < (1 - 0.7F / (2 + aggressiveLevel))) // same math thingy as above
                return b ? NORMAL_FACE_COUNT : NORMAL_FACE_COUNT + 1;

            boolean hasFlame = keySet.contains(HolderCacher.getAsHolder(Enchantments.FLAME, obj));
            if (hasFlame && f > 0.3F) // 70%
                return NORMAL_FACE_COUNT + ANGRY_FACE_COUNT; // first lit face is jack-o-lantern

            if (random.nextFloat() > 0.35F) // 65% chance
                if (keySet.contains(HolderCacher.getAsHolder(Enchantments.MULTISHOT, obj)))
                    // lit 3-eyed is 8th and normal 3-eyed is 2nd to last
                    return (byte)(hasFlame ? 7 : TOTAL_FACES - 2);
                else if (keySet.contains(HolderCacher.getAsHolder(GolemEnchantments.ACCURACY, obj)))
                    // 1-eyed is the first face not in any categories
                    return NORMAL_FACE_COUNT + ANGRY_FACE_COUNT + LIT_FACE_COUNT;
        }

        return (byte)random.nextInt(NORMAL_FACE_COUNT);
    }

    public static byte calculateFaceId(@Nullable RandomSource random, @Nullable ItemEnchantments enchantments) {
        return calculateFaceId(random, enchantments != null ? enchantments::getLevel : __ -> 0,
                               enchantments != null ? enchantments.keySet() : null, null);
    }

    public boolean hasEnchantment(ResourceKey<Enchantment> enchantment) {
        Set<Holder<Enchantment>> keySet = this.enchantments.keySet();
        for (Holder<Enchantment> key : keySet)
            if (key.is(enchantment))
                return true;
        // double check
        Holder<Enchantment> theirHolder = toHolder(enchantment);
        if (theirHolder != null) {
            Enchantment holderValue = theirHolder.value();
            for (Holder<Enchantment> ourHolder : keySet)
                if (ourHolder.value() == holderValue)
                    return true;
        }
        return false;
    }

    public int getLevel(ResourceKey<Enchantment> enchantment) {
        return this.enchantments.getInt(toHolder(enchantment));
    }

    public boolean hasEnchantment(Holder<Enchantment> enchantment) {
        return this.enchantments.containsKey(enchantment);
    }

    public int getLevel(Holder<Enchantment> enchantment) {
        return this.enchantments.getInt(enchantment);
    }

    public Object2IntMap<Holder<Enchantment>> getEnchantments() {
        return this.enchantments;
    }

    public void setEnchantments(@Nullable Object2IntMap<Holder<Enchantment>> enchants) {
        Object2IntOpenHashMap<Holder<Enchantment>> enchantments = this.enchantments;
        enchantments.clear();
        if (enchants != null && !enchants.isEmpty())
            enchantments.putAll(enchants);
        enchantments.trim();
    }

    public void setEnchantments(@Nullable Set<Entry<Holder<Enchantment>>> enchants) {
        Object2IntOpenHashMap<Holder<Enchantment>> enchantments = this.enchantments;
        enchantments.clear();
        if (enchants != null && !enchants.isEmpty())
            for (Entry<Holder<Enchantment>> entry : enchants)
                enchantments.put(entry.getKey(), entry.getIntValue());
        enchantments.trim();
    }

    public Holder<Enchantment> toHolder(ResourceKey<Enchantment> key) {
        Objects.requireNonNull(key);
        if (level != null)
            return level.registryAccess().holderOrThrow(key);
        RegistryLookup<Enchantment> lookup = CommonHooks.resolveLookup(key.registryKey());
        if (lookup != null)
            return lookup.getOrThrow(key);
        return null;
    }
}
