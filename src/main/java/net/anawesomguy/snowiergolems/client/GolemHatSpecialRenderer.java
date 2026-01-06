package net.anawesomguy.snowiergolems.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.SnowierGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class GolemHatSpecialRenderer implements SpecialModelRenderer<DataComponentMap> {
    private final GolemHatRenderer renderer;

    private GolemHatSpecialRenderer(GolemHatRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void submit(@Nullable DataComponentMap components, ItemDisplayContext displayContext, PoseStack stack, SubmitNodeCollector nodeCollector, int light, int overlay, boolean foil, int outline) {
        byte faceId;
        boolean flame;
        if (components == null || components.isEmpty()) {
            faceId = 0;
            flame = false;
        } else {
            faceId = components.getOrDefault(GolemObjects.PUMPKIN_FACE, (byte)0);
            ItemEnchantments enchantments = components.getOrDefault(DataComponents.ENCHANTMENTS,
                                                                    ItemEnchantments.EMPTY);
            flame = enchantments.getLevel(
                SnowierGolems.getAsHolder(Enchantments.FLAME, Minecraft.getInstance().level)) > 0;
        }
        stack.translate(0.5F, 0.5F, 0.5F);
        stack.rotateAround(Axis.YP.rotationDegrees(180F), 0.5F, 0.5F, 0.5F); // 180 deg
        SnowierGolemsClient.StandaloneGolemHatModel.TRANSFORMS.getTransform(displayContext)
                                                              .apply(displayContext.leftHand(), stack.last());
        GolemHatRenderer.submit(stack, nodeCollector, renderer.modelManager,
                                GolemObjects.GOLEM_HAT.defaultBlockState(), faceId, foil, flame,
                                light, overlay, outline);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        // for (BakedQuad quad : SnowierGolemsClient.getAllQuads(
        //     GolemHatRenderer.getModel((byte)0, Direction.NORTH, renderer.modelManager))) {
        //     consumer.accept(quad.position0());
        //     consumer.accept(quad.position1());
        //     consumer.accept(quad.position2());
        //     consumer.accept(quad.position3());
        // }
    }

    @Override
    public DataComponentMap extractArgument(ItemStack stack) {
        return stack.getComponents();
    }

    public static final class Unbaked implements SpecialModelRenderer.Unbaked {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(INSTANCE);

        private Unbaked() {
        }

        @Override
        public MapCodec<Unbaked> type() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(BakingContext ctx) {
            return new GolemHatSpecialRenderer(new GolemHatRenderer());
        }
    }
}
