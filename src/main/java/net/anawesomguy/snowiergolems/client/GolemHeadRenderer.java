package net.anawesomguy.snowiergolems.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.anawesomguy.snowiergolems.block.GolemHeadBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;

public class GolemHeadRenderer implements BlockEntityRenderer<GolemHeadBlockEntity> {
    public GolemHeadRenderer(Context ctx) {
    }

    @Override
    public void render(GolemHeadBlockEntity golemHead, float tickDelta, PoseStack stack, MultiBufferSource buffer,
                       int light, int overlay) {

    }
}
