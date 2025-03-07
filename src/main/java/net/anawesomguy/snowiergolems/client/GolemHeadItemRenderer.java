package net.anawesomguy.snowiergolems.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static net.anawesomguy.snowiergolems.client.GolemHeadRenderer.*;

public final class GolemHeadItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final GolemHeadItemRenderer INSTANCE = new GolemHeadItemRenderer();

    private final ModelPart back;
    private final ModelPart left;
    private final ModelPart right;
    private final ModelPart front;
    private final ModelPart top;
    private final ModelPart bottom;

    private GolemHeadItemRenderer() {
        this(Minecraft.getInstance());
    }

    private GolemHeadItemRenderer(Minecraft minecraft) {
        super(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());

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
    }
}
