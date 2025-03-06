package net.anawesomguy.snowiergolems.client;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = SnowierGolems.MODID, bus = Bus.MOD)
public final class SnowierGolemsClient {
    public static final IClientItemExtensions GOLEM_HEAD_ITEM_EXTENSIONS = new IClientItemExtensions() {
        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return GolemHeadItemRenderer.INSTANCE;
        }
    };

    //@SubscribeEvent
    private static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(GOLEM_HEAD_ITEM_EXTENSIONS, GolemObjects.GOLEM_HEAD_ITEM);
    }

    @SubscribeEvent
    private static void registerRenderers(RegisterRenderers event) {
        event.registerBlockEntityRenderer(GolemObjects.GOLEM_HEAD_TYPE, GolemHeadRenderer::new);
        event.registerEntityRenderer(GolemObjects.ENCHANTED_SNOWBALL, ThrownItemRenderer::new);
    }

    @SubscribeEvent
    private static void registerLayers(RegisterLayerDefinitions event) {
        event.registerLayerDefinition(GolemHeadRenderer.SIDES_LAYER, GolemHeadRenderer::createSidesLayer);
    }
}
