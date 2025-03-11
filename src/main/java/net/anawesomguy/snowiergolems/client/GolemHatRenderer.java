package net.anawesomguy.snowiergolems.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Axis;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.anawesomguy.snowiergolems.block.GolemHatBlock;
import net.anawesomguy.snowiergolems.block.GolemHatBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;

import static net.minecraft.util.Mth.HALF_PI;
import static net.minecraft.util.Mth.PI;

public class GolemHatRenderer implements BlockEntityRenderer<GolemHatBlockEntity> {
    public static final List<Material> NORMAL_FACES = List.of(pumpkinFace("face_1"),
                                                              pumpkinFace("face_2"),
                                                              pumpkinFace("face_3"),
                                                              pumpkinFace("face_4"));
    public static final List<Material> ANGRY_FACES = List.of(pumpkinFace("angry_1"),
                                                             pumpkinFace("angry_2"));
    public static final List<Material> LIT_FACES = List.of(
        blockMaterial(ResourceLocation.withDefaultNamespace("block/jack_o_lantern")),
        pumpkinFace("three_eyed_lit"));
    public static final Material ONE_EYED_FACE = pumpkinFace("one_eyed");
    public static final Material THREE_EYED_FACE = pumpkinFace("three_eyed");
    public static final Material FROST_FACE = pumpkinFace("frost");
    public static final Material SNOWY_PUMPKIN_SIDE = blockMaterial(SnowierGolems.id("block/pumpkin/snowy_pumpkin"));
    public static final Material PUMPKIN_SIDE = blockMaterial(ResourceLocation.withDefaultNamespace("block/pumpkin_side"));
    public static final Material SNOW = blockMaterial(ResourceLocation.withDefaultNamespace("block/snow"));
    public static final Material PUMPKIN_TOP = blockMaterial(ResourceLocation.withDefaultNamespace("block/pumpkin_top"));

    public static final List<Material> ALL_FACES = ImmutableList.<Material>builder()
                                                                .addAll(NORMAL_FACES)
                                                                .addAll(ANGRY_FACES)
                                                                .addAll(LIT_FACES)
                                                                .add(ONE_EYED_FACE, THREE_EYED_FACE, FROST_FACE)
                                                                .build();

    public static final ModelLayerLocation SIDES_LAYER = new ModelLayerLocation(GolemObjects.GOLEM_HAT_ID, "main");

    private static Material blockMaterial(ResourceLocation texture) {
        return new Material(InventoryMenu.BLOCK_ATLAS, texture);
    }

    private static Material pumpkinFace(String faceName) {
        return blockMaterial(SnowierGolems.id("block/pumpkin/face/" + faceName));
    }

    public static LayerDefinition createSidesLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();

        //@formatter:off (alignment)
        CubeListBuilder horizontalFace = CubeListBuilder.create().addBox(0F, 0F, 0F, 16F, 16F, 0F, EnumSet.of(Direction.NORTH));
        parts.addOrReplaceChild("back",  horizontalFace, PartPose.offsetAndRotation(16F, 16F, 0F,  0F, 0F,       PI));
        parts.addOrReplaceChild("left",  horizontalFace, PartPose.offsetAndRotation(0F,  16F, 0F,  0F, -HALF_PI, PI));
        parts.addOrReplaceChild("right", horizontalFace, PartPose.offsetAndRotation(16F, 16F, 16F, 0F, HALF_PI,  PI));
        parts.addOrReplaceChild("front", horizontalFace, PartPose.offsetAndRotation(0F,  16F, 16F, PI, 0F,       0F));
        //@formatter:on

        parts.addOrReplaceChild("top",
                                CubeListBuilder.create().texOffs(-32, 0).addBox(0F, 16F, 0F, 16F, 0F, 16F),
                                PartPose.ZERO);
        parts.addOrReplaceChild("bottom",
                                CubeListBuilder.create().texOffs(-16, 0).addBox(0F, 0F, 0F, 16F, 0F, 16F),
                                PartPose.ZERO);

        return LayerDefinition.create(mesh, 16, 16);
    }

    protected final ModelPart back;
    protected final ModelPart left;
    protected final ModelPart right;
    protected final ModelPart front;
    protected final ModelPart top;
    protected final ModelPart bottom;

    protected final BlockRenderDispatcher blockRenderer;

    public GolemHatRenderer(Context ctx) {
        this.blockRenderer = ctx.getBlockRenderDispatcher();

        ModelPart faces = ctx.bakeLayer(SIDES_LAYER);
        this.back = faces.getChild("back");
        this.left = faces.getChild("left");
        this.right = faces.getChild("right");
        this.front = faces.getChild("front");
        this.top = faces.getChild("top");
        this.bottom = faces.getChild("bottom");
    }

    @Override
    public void render(GolemHatBlockEntity golemHat, float tickDelta, PoseStack stack, MultiBufferSource buffer,
                       int light, int overlay) {
        Level level = golemHat.getLevel();
        if (level == null)
            level = Minecraft.getInstance().level;
        if (level != null)
            light = LevelRenderer.getLightColor(level, golemHat.getBlockState(), golemHat.getBlockPos().above());

        int faceId = Byte.toUnsignedInt(golemHat.getOrCreateFaceId());
        Material face = ALL_FACES.get(faceId);
        if (face == null) {
            SnowierGolems.LOGGER.error("Invalid face id {}! (wtf?)", faceId);
            return;
        }

        stack.translate(0.5F, 0F, 0.5F);
        stack.mulPose(Axis.YP.rotationDegrees(-golemHat.getBlockState().getValue(GolemHatBlock.FACING).toYRot()));
        stack.translate(-0.5F, 0F, -0.5F);
        render(face, stack, buffer, light, overlay, golemHat.hasEnchantments(), false, back, left, right, front, top, bottom);

        if (golemHat.hasEnchantment(Enchantments.FLAME)) {
            BlockState state = Blocks.FIRE.defaultBlockState();
            RenderType renderType = RenderType.cutout();
            stack.translate(0.1F, 1F, 0.1F);
            stack.scale(0.8F, 0.6F, 0.8F);
            blockRenderer.getModelRenderer().renderModel(
                stack.last(),
                buffer.getBuffer(renderType),
                state,
                blockRenderer.getBlockModel(state),
                0F, 0F, 0F,
                light, overlay,
                ModelData.EMPTY,
                renderType
            );
        }
    }

    public static void render(Material face, PoseStack stack, MultiBufferSource buffer,
                              int light, int overlay, boolean glint, boolean item,
                              ModelPart back, ModelPart left, ModelPart right, ModelPart front, ModelPart top, ModelPart bottom) {
        Function<ResourceLocation, RenderType> entitySolid = RenderType::entitySolid;
        VertexConsumer topTexture, sidesTexture;
        VertexConsumer bottomTexture = withGlint(buffer, PUMPKIN_TOP.buffer(buffer, entitySolid), item, glint);
        if (face == FROST_FACE) {
            topTexture = withGlint(buffer, SNOW.buffer(buffer, entitySolid), item, glint);
            sidesTexture = withGlint(buffer, SNOWY_PUMPKIN_SIDE.buffer(buffer, entitySolid), item, glint);
        } else {
            topTexture = bottomTexture;
            sidesTexture = withGlint(buffer, PUMPKIN_SIDE.buffer(buffer, entitySolid), item, glint);
        }

        back.render(stack, sidesTexture, light, overlay);
        left.render(stack, sidesTexture, light, overlay);
        right.render(stack, sidesTexture, light, overlay);
        front.render(stack, withGlint(buffer, face.buffer(buffer, entitySolid), item, glint), light, overlay);
        top.render(stack, topTexture, light, overlay);
        bottom.render(stack, bottomTexture, light, overlay);
    }

    public static VertexConsumer withGlint(MultiBufferSource buffers, VertexConsumer original, boolean item, boolean glint) {
        return glint
            ? VertexMultiConsumer.create(buffers.getBuffer(RenderType.entityGlint()), original)
            : original;
    }
}
