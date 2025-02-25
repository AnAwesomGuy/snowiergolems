package net.anawesomguy.snowiergolems.block;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.anawesomguy.snowiergolems.mixin.ItemEnchantmentsAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class GolemHeadBlockEntity extends BlockEntity implements Nameable {
    private static final Codec<Object2IntOpenHashMap<Holder<Enchantment>>> ENCHANTS_CODEC =
        ItemEnchantmentsAccessor.getLEVELS_CODEC();
    public static final String ENCHANTMENTS_TAG = "enchantments";

    @NotNull // can always be assumed to be mutable
    protected Object2IntOpenHashMap<Holder<Enchantment>> enchantments = new Object2IntOpenHashMap<>();
    @Nullable
    protected Component name;

    public GolemHeadBlockEntity(BlockPos pos, BlockState state) {
        super(GolemObjects.GOLEM_HEAD_TYPE, pos, state);
    }

    public ItemStack getAsStack() {
        ItemStack stack = new ItemStack(GolemObjects.GOLEM_HEAD_ITEM);
        stack.applyComponents(this.collectComponents());
        return stack;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        if (this.name != null)
            tag.putString("CustomName", Component.Serializer.toJson(this.name, registries));

        if (!enchantments.isEmpty())
            tag.put(ENCHANTMENTS_TAG,
                    ENCHANTS_CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), enchantments)
                                  .getOrThrow());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("CustomName", Tag.TAG_STRING))
            this.name = parseCustomNameSafe(tag.getString("CustomName"), registries);

        if (tag.contains(ENCHANTMENTS_TAG))
            ENCHANTS_CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get(ENCHANTMENTS_TAG))
                          .resultOrPartial(err -> SnowierGolems.LOGGER.error("Failed to load golem head enchantments: '{}'", err))
                          .ifPresent(enchants -> this.enchantments = enchants);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        ItemEnchantments enchants = componentInput.get(DataComponents.ENCHANTMENTS);
        if (enchants != null && !enchants.isEmpty())
            this.enchantments = ((ItemEnchantmentsAccessor)enchants).getEnchantments();
        this.name = componentInput.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.ENCHANTMENTS,
                       ItemEnchantmentsAccessor.createInstance(this.enchantments, true));
        components.set(DataComponents.CUSTOM_NAME, this.name);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(ENCHANTMENTS_TAG);
        tag.remove("CustomName");
    }

    public void setCustomName(@Nullable Component customName) {
        this.name = customName;
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

    public int getLevel(Holder<Enchantment> enchantment) {
        return this.enchantments.getInt(enchantment);
    }

    public int setLevel(Holder<Enchantment> enchantment, int level) {
        return this.enchantments.put(enchantment, level);
    }

    public int removeEnchant(Holder<Enchantment> enchantment) {
        return this.enchantments.removeInt(enchantment);
    }

    public void removeIf(Predicate<Holder<Enchantment>> predicate) {
        this.enchantments.keySet().removeIf(predicate);
    }

    public void setEnchantments(@Nullable Object2IntMap<Holder<Enchantment>> enchants) {
        enchantments.clear();
        if (enchants != null && !enchants.isEmpty())
            enchantments.putAll(enchants);
        enchantments.trim();
    }

    public Object2IntMap<Holder<Enchantment>> getEnchantments() {
        return enchantments;
    }
}
