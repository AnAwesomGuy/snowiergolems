package net.anawesomguy.snowiergolems.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.anawesomguy.snowiergolems.util.HolderCacher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import static net.anawesomguy.snowiergolems.client.GolemHatRenderer.*;

public final class GolemHatItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final GolemHatItemRenderer INSTANCE = new GolemHatItemRenderer();

    private final BlockRenderDispatcher blockRenderer;

    private final ModelPart back;
    private final ModelPart left;
    private final ModelPart right;
    private final ModelPart front;
    private final ModelPart top;
    private final ModelPart bottom;

    private GolemHatItemRenderer() {
        this(Minecraft.getInstance());
    }

    private GolemHatItemRenderer(Minecraft minecraft) {
        super(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());

        this.blockRenderer = minecraft.getBlockRenderer();

        ModelPart faces = minecraft.getEntityModels().bakeLayer(SIDES_LAYER);
        this.back = faces.getChild("back");
        this.left = faces.getChild("left");
        this.right = faces.getChild("right");
        this.front = faces.getChild("front");
        this.top = faces.getChild("top");
        this.bottom = faces.getChild("bottom");
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer,
                             int light, int overlay) {
        int faceId = Byte.toUnsignedInt(stack.getOrDefault(GolemObjects.PUMPKIN_FACE, (byte)0));
        Material face = ALL_FACES.get(faceId);
        if (face == null) {
            SnowierGolems.LOGGER.error("Invalid face id {}! (wtf?)", faceId);
            return;
        }

        render(face, poseStack, buffer, light, overlay, back, left, right, front, top, bottom);

        if (stack.getEnchantmentLevel(HolderCacher.getAsHolder(Enchantments.FLAME, Minecraft.getInstance().level)) > 0) {
            BlockState state = Blocks.FIRE.defaultBlockState();
            RenderType renderType = RenderType.cutout();
            poseStack.translate(0F, 1F, 0F);
            poseStack.scale(0.8F, 0.8F, 0.8F);
            blockRenderer.getModelRenderer().renderModel(
                poseStack.last(),
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
}
