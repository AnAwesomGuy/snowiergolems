package net.anawesomguy.snowiergolems.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GolemHeadItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final GolemHeadItemRenderer INSTANCE = new GolemHeadItemRenderer();
    protected final ItemRenderer itemRenderer;

    private GolemHeadItemRenderer() {
        this(Minecraft.getInstance());
    }

    private GolemHeadItemRenderer(Minecraft minecraft) {
        super(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
        itemRenderer = minecraft.getItemRenderer();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer,
                             int light, int overlay) {

    }
}
