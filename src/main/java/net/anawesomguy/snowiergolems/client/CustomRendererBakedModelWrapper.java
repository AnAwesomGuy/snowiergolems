package net.anawesomguy.snowiergolems.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

public class CustomRendererBakedModelWrapper<T extends BakedModel> extends BakedModelWrapper<T> {
    public CustomRendererBakedModelWrapper(T original) {
        super(original);
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext context, PoseStack poseStack, boolean leftHand) {
        return new CustomRendererBakedModelWrapper<>(super.applyTransform(context, poseStack, leftHand));
    }
}
