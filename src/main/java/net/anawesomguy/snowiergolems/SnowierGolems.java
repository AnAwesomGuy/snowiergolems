package net.anawesomguy.snowiergolems;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.anawesomguy.snowiergolems.item.GolemHatItem;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.LevelReader;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Optional;

@Mod(SnowierGolems.MODID)
public final class SnowierGolems {
    public static final String MODID = "snowiergolems";
    // public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final Identifier MOD_LOCATION = Identifier.fromNamespaceAndPath(MODID, "");
    public static final Identifier GOLEM_HAT_ID = id("golem_hat");

    public static Identifier id(String path) {
        return MOD_LOCATION.withPath(path);
    }

    public SnowierGolems(IEventBus eventBus) {
        eventBus.addListener(GolemObjects::register);
        eventBus.addListener(GolemObjects::addToCreativeTabs);
        eventBus.addListener(GolemObjects::commonSetup);
        NeoForge.EVENT_BUS.addListener((AnvilUpdateEvent event) -> {
            ItemStack stack = event.getVanillaResult().output();
            if (stack.is(GolemObjects.GOLEM_HAT_ITEM))
                GolemHatItem.setPumpkinFace(stack);
        });
    }

    public static ItemEnchantments getEnchantments(ItemStack stack) {
        HolderLookup.RegistryLookup<Enchantment> lookup = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
        return lookup == null ? stack.getTagEnchantments() : stack.getAllEnchantments(lookup);
    }

    public static boolean hasEnchantment(ItemEnchantments enchantments, ResourceKey<Enchantment> enchantment) {
        for (Holder<Enchantment> holder : enchantments.keySet())
            if (holder.is(enchantment))
                return true;
        return false;
    }

    public static int getEnchantmentLevel(ItemEnchantments enchantments, ResourceKey<Enchantment> enchantment) {
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet())
            if (entry.getKey().is(enchantment))
                return entry.getIntValue();
        return 0;
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
    public static <T> Holder.@UnknownNullability Reference<T> getAsHolder(ResourceKey<T> key, @Nullable Object obj) {
        if (obj instanceof Holder.Reference<?>)
            obj = ((Holder.Reference<?>)obj).unwrapLookup();
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
                    else if (FMLEnvironment.getDist().isClient()) {
                        LevelReader level = Minecraft.getInstance().level;
                        if (level != null)
                            yield level.registryAccess();
                    }
                    yield null;
                }
            }).<HolderGetter<T>>flatMap(access -> access.lookup(key.registryKey()))
        ).flatMap(getter -> getter.get(key))
         .orElse(null);
    }
}
