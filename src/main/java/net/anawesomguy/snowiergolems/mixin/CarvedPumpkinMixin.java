package net.anawesomguy.snowiergolems.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.block.GolemHeadBlock;
import net.anawesomguy.snowiergolems.block.GolemHeadBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern.BlockPatternMatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinMixin {
    private CarvedPumpkinMixin() {
    }

    @Inject(method = "trySpawnGolem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CarvedPumpkinBlock;getOrCreateIronGolemFull()Lnet/minecraft/world/level/block/state/pattern/BlockPattern;"), cancellable = true)
    private void noIronGolem(Level level, BlockPos pos, CallbackInfo ci) {
        if ((Object)this instanceof GolemHeadBlock)
            ci.cancel();
    }

    @Inject(method = "trySpawnGolem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CarvedPumpkinBlock;spawnGolemInWorld(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/pattern/BlockPattern$BlockPatternMatch;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;)V", ordinal = 0))
    private void addSnowGolemEnchants(Level level, BlockPos pos, CallbackInfo ci, @Local BlockPatternMatch match,
                                      @Local SnowGolem golem) {
        BlockInWorld blockInWorld = match.getBlock(0, 0, 0);
        if (match.getBlock(0, 0, 0).getState().is(GolemObjects.GOLEM_HEAD)) {
            BlockEntity golemHead = blockInWorld.getEntity();
            if (golemHead instanceof GolemHeadBlockEntity golemHeadEntity) {
                golem.setItemSlot(EquipmentSlot.HEAD, golemHeadEntity.getAsStack());
                if (golemHeadEntity.hasCustomName())
                    golem.setCustomName(golemHeadEntity.getCustomName());
            }
        }
    }

    @ModifyExpressionValue(method = "getOrCreateSnowGolemFull", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/CarvedPumpkinBlock;PUMPKINS_PREDICATE:Ljava/util/function/Predicate;"))
    private Predicate<BlockState> addGolemHeadAsPumpkin(Predicate<BlockState> original) {
        return original.or(state -> state != null && state.is(GolemObjects.GOLEM_HEAD));
    }
}
