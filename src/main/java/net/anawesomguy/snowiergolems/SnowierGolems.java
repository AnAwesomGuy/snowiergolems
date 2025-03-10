package net.anawesomguy.snowiergolems;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(SnowierGolems.MODID)
public final class SnowierGolems {
    public static final String MODID = "snowiergolems";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public SnowierGolems(IEventBus eventBus) {
        eventBus.addListener(GolemObjects::register);
        eventBus.addListener(GolemObjects::addToCreativeTabs);
    }
}
