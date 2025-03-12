package net.anawesomguy.snowiergolems.util;

import net.anawesomguy.snowiergolems.enchant.GolemEnchantments;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.LevelReader;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class HolderCacher<T> implements Function<Object, Reference<T>>, Supplier<Reference<T>> {
    // hopefully this doesn't break when resources reload?
    public static final HolderCacher<Enchantment> AGGRESSIVE_ENCHANT = new HolderCacher<>(GolemEnchantments.AGGRESSIVE);

    public final ResourceKey<T> key;
    private Reference<T> tReference;

    public HolderCacher(ResourceKey<T> key) {
        this.key = Objects.requireNonNull(key);
    }

    /**
     * @see #getAsHolder
     */
    @UnknownNullability
    @Override
    public Reference<T> apply(Object obj) {
        Reference<T> t = tReference;
        if (t != null)
            return t;
        if ((t = getAsHolder(key, obj)) != null)
            tReference = t;
        return t;
    }

    @Override
    @Nullable
    public Reference<T> get() {
        return tReference;
    }

    /**
     * The parameter {@code obj} must be an {@link Entity}, {@link LevelReader}, {@link MinecraftServer}, {@link SharedSuggestionProvider}, {@link HolderLookup.Provider}, or {@link HolderGetter}. <br>
     * If it is none, a {@link HolderGetter} will be attempted to be resolved in a manner similar to {@link CommonHooks#resolveLookup}. <br>
     * If that also fails, {@code null} will be returned.
     *
     * @param key the key representing the holder to retrieve.
     * @param obj an object which you can retrieve a {@link Holder} from.
     * @return a {@link Holder} representing {@link #key}, or {@code null} if one cannot be resolved.
     */
    @SuppressWarnings("unchecked")
    @UnknownNullability
    public static <T> Reference<T> getAsHolder(ResourceKey<T> key, Object obj) {
        if (obj instanceof Holder.Reference<?>)
            obj = ((Reference<?>)obj).unwrapLookup();
        return (
            obj instanceof HolderGetter<?> ? Optional.of((HolderGetter<T>)obj) : Optional.ofNullable(switch (obj) {
                case Entity entity -> entity.registryAccess();
                case LevelReader levelReader -> levelReader.registryAccess();
                case MinecraftServer server -> server.registryAccess();
                case SharedSuggestionProvider suggestionProvider -> suggestionProvider.registryAccess();
                case HolderLookup.Provider provider -> provider;
                // could probably put more but im *lazy*
                case null, default -> {
                    // attempt to resolve
                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    if (server != null)
                        yield server.registryAccess();
                    else if (FMLEnvironment.dist.isClient()) {
                        LevelReader level = Minecraft.getInstance().level;
                        if (level != null)
                            yield level.registryAccess();
                    }
                    yield null;
                }
            }).flatMap(access -> access.lookup(key.registryKey()))
        ).flatMap(getter -> getter.get(key))
         .orElse(null);
    }
}
