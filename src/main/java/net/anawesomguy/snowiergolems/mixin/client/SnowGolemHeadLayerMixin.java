package net.anawesomguy.snowiergolems.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.client.GolemHatRenderer;
import net.anawesomguy.snowiergolems.client.SnowierGolemsClient;
import net.anawesomguy.snowiergolems.item.GolemHatItem;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(SnowGolemHeadLayer.class)
public abstract class SnowGolemHeadLayerMixin {
    private SnowGolemHeadLayerMixin() {
    }

    @WrapOperation(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/SnowGolemRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;defaultBlockState()Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState changeRenderedHat(Block instance, Operation<BlockState> original, @Local(argsOnly = true) SnowGolemRenderState golem, @Share("hatStack") LocalRef<ItemStack> hatStackRef) {
        ItemStack head = Objects.requireNonNullElse(golem.getRenderData(SnowierGolemsClient.HAT_KEY), ItemStack.EMPTY);
        hatStackRef.set(head);
        return original.call(
            head.isEmpty() || !(head.getItem() instanceof GolemHatItem hat) ? instance : hat.getBlock());
    }

    @WrapOperation(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/SnowGolemRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/block/model/BlockStateModel;"))
    private BlockStateModel changeRenderedHat(BlockRenderDispatcher blockRenderer, BlockState state, Operation<BlockStateModel> original, @Local(argsOnly = true) SnowGolemRenderState golem, @Share("hatStack") LocalRef<ItemStack> hatStackRef) {
        ItemStack stack = hatStackRef.get();
        if (stack.isEmpty())
            return original.call(blockRenderer, state);
        return GolemHatRenderer.toBlockStateModel(
            GolemHatRenderer.getModel(stack.getOrDefault(GolemObjects.PUMPKIN_FACE, (byte)0), Direction.NORTH,
                                      blockRenderer.getBlockModelShaper().getModelManager()));
    }
}
