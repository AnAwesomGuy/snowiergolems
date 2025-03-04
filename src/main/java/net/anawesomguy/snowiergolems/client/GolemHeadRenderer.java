package net.anawesomguy.snowiergolems.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.anawesomguy.snowiergolems.block.GolemHeadBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.List;

public class GolemHeadRenderer implements BlockEntityRenderer<GolemHeadBlockEntity> {
    public static final List<Material> FACES = List.of(pumpkinFace("face_1"),
                                                       pumpkinFace("face_2"),
                                                       pumpkinFace("face_3"),
                                                       pumpkinFace("face_4"));
    public static final List<Material> LIT_FACES = List.of(
        blockMaterial(ResourceLocation.withDefaultNamespace("block/jack_o_lantern")),
        pumpkinFace("three_eyed_lit"));
    public static final List<Material> ANGRY_FACES = List.of(pumpkinFace("angry_1"),
                                                             pumpkinFace("angry_2"));
    public static final Material FROST_FACE = pumpkinFace("frost");
    public static final Material ONE_EYED_FACE = pumpkinFace("one_eyed");
    public static final Material THREE_EYED_FACE = pumpkinFace("three_eyed");
    public static final Material SNOWY_PUMPKIN_SIDE = blockMaterial(SnowierGolems.id("block/pumpkin/snowy_pumpkin"));
    public static final Material SNOW = blockMaterial(ResourceLocation.withDefaultNamespace("block/snow"));

    private static Material blockMaterial(ResourceLocation texture) {
        return new Material(InventoryMenu.BLOCK_ATLAS, texture);
    }

    private static Material pumpkinFace(String faceName) {
        return blockMaterial(SnowierGolems.id("block/pumpkin/face/" + faceName));
    }

    public GolemHeadRenderer(Context ctx) {
    }

    @Override
    public void render(GolemHeadBlockEntity golemHead, float tickDelta, PoseStack stack, MultiBufferSource buffer,
                       int light, int overlay) {

    }
}
