package net.anawesomguy.snowiergolems.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.anawesomguy.snowiergolems.block.GolemHatBlock;
import net.anawesomguy.snowiergolems.block.GolemHatBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.SingleVariant;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.RenderTypeHelper;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static net.anawesomguy.snowiergolems.client.SnowierGolemsClient.FACES_KEYS;

public class GolemHatRenderer implements BlockEntityRenderer<GolemHatBlockEntity, GolemHatRenderState> {
    private static final Map<BlockModelPart, BlockStateModel> BLOCK_STATE_MODEL_CACHE = new IdentityHashMap<>(44);

    public static BlockStateModel toBlockStateModel(BlockModelPart model) {
        return BLOCK_STATE_MODEL_CACHE.computeIfAbsent(model, SingleVariant::new);
    }

    public static BlockModelPart getModel(byte faceId, Direction facing, ModelManager models) {
        return requireNonNull(models.getStandaloneModel(FACES_KEYS.get(Byte.toUnsignedInt(faceId)).get(facing)));
    }

    public final ModelManager modelManager;

    public GolemHatRenderer(BlockEntityRendererProvider.Context ctx) {
        this.modelManager = ctx.blockRenderDispatcher().getBlockModelShaper().getModelManager();
    }

    public GolemHatRenderer() {
        this.modelManager = Minecraft.getInstance().getModelManager();
    }

    public static void submit(PoseStack stack, SubmitNodeCollector collector, ModelManager modelManager, BlockState state, byte faceId, boolean foil, boolean flame, int light, int overlay, int outlineColor) {
        BlockModelPart model = getModel(faceId, state.getValue(GolemHatBlock.FACING), modelManager);
        collector.submitBlockModel(stack,
                                   RenderTypeHelper.getEntityRenderType(model.getRenderType(state)),
                                   toBlockStateModel(model),
                                   0F, 0F, 0F,
                                   light, overlay, outlineColor);

        if (foil)
            collector.submitBlockModel(stack,
                                       RenderTypes.entityGlint(),
                                       toBlockStateModel(model),
                                       0F, 0F, 0F,
                                       light, overlay, outlineColor);

        if (flame) {
            stack.translate(0.1F, 1F, 0.1F);
            stack.scale(0.8F, 0.6F, 0.8F);
            collector.submitBlock(stack, Blocks.FIRE.defaultBlockState(), light, OverlayTexture.NO_OVERLAY, 0);
        }
    }

    @Override
    public GolemHatRenderState createRenderState() {
        return new GolemHatRenderState();
    }

    @Override
    public void extractRenderState(GolemHatBlockEntity golemHat, GolemHatRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(golemHat, renderState, partialTick, cameraPosition, breakProgress);
        renderState.hasFlame = golemHat.hasEnchantment(Enchantments.FLAME);
        renderState.hasFoil = golemHat.hasEnchantments();
        renderState.faceId = golemHat.getOrCreateFaceId();
        renderState.lightCoords = golemHat.getLevel() != null ? LevelRenderer.getLightColor(golemHat.getLevel(),
                                                                                            golemHat.getBlockPos()
                                                                                                    .above()) : 0xF000F0;
    }

    @Override
    public void submit(GolemHatRenderState golemHat, PoseStack stack, SubmitNodeCollector nodeCollector, CameraRenderState camera) {
        stack.rotateAround(Axis.YP.rotationDegrees(180F), 0.5F, 0.5F, 0.5F); // 180 deg
        submit(stack, nodeCollector, modelManager,
               golemHat.blockState, golemHat.faceId, golemHat.hasFoil, golemHat.hasFlame,
               golemHat.lightCoords, OverlayTexture.NO_OVERLAY, 0);
    }
}
