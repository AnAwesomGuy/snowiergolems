package net.anawesomguy.snowiergolems;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Mod(SnowierGolems.MODID)
public final class SnowierGolems {
    public static final String MODID = "snowiergolems";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final ResourceLocation MOD_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, MODID);
    
    public static ResourceLocation id(String path) {
        return MOD_LOCATION.withPath(path);
    }

    public SnowierGolems(IEventBus eventBus) {
        eventBus.addListener(GolemObjects::register);
        eventBus.addListener(GolemObjects::addToCreativeTabs);
        eventBus.addListener(GolemObjects::commonSetup);
    }

    /**
     * The parameter {@code obj} must be an {@link Entity}, {@link LevelReader}, {@link MinecraftServer}, {@link SharedSuggestionProvider}, {@link HolderLookup.Provider}, or {@link HolderGetter}. <br>
     * If it is none, a {@link HolderGetter} will be attempted to be resolved in a manner similar to {@link CommonHooks#resolveLookup}. <br>
     * If that also fails, {@code null} will be returned.
     *
     * @param key the key representing the holder to retrieve.
     * @param obj an object which you can retrieve a {@link Holder} from.
     * @return a {@link Holder} representing {@code key}, or {@code null} if one cannot be resolved.
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
