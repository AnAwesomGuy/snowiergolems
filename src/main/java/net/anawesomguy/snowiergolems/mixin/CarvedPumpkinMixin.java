package net.anawesomguy.snowiergolems.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.block.GolemHatBlock;
import net.anawesomguy.snowiergolems.block.GolemHatBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern.BlockPatternMatch;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
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
        if ((Object)this instanceof GolemHatBlock)
            ci.cancel();
    }

    @Inject(method = "trySpawnGolem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CarvedPumpkinBlock;spawnGolemInWorld(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/pattern/BlockPattern$BlockPatternMatch;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;)V", ordinal = 0))
    private void addSnowGolemEnchants(Level level, BlockPos pos, CallbackInfo ci, @Local BlockPatternMatch match,
                                      @Local SnowGolem golem) {
        BlockInWorld blockInWorld = match.getBlock(0, 0, 0);
        if (match.getBlock(0, 0, 0).getState().is(GolemObjects.GOLEM_HAT)) {
            BlockEntity golemHat = blockInWorld.getEntity();
            if (golemHat instanceof GolemHatBlockEntity golemHatEntity) {
                golem.setItemSlot(EquipmentSlot.HEAD, golemHatEntity.getAsStack());
                if (golemHatEntity.hasCustomName())
                    golem.setCustomName(golemHatEntity.getCustomName());
            }
        }
    }

    @Inject(method = "spawnGolemInWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/criterion/SummonedEntityTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/Entity;)V"))
    private static void findNearestPlayerToGolem(Level level, BlockPatternMatch patternMatch, Entity golem, BlockPos pos,
                                                 CallbackInfo ci, @Local ServerPlayer player,
                                                 @Share("nearestPlayer") LocalRef<ServerPlayer> nearestPlayer,
                                                 @Share("distanceSqr") LocalDoubleRef distanceSqrRef) {
        if (golem instanceof SnowGolem) {
            double playerDistanceSqr = player.distanceToSqr(golem);
            double distanceSqr = distanceSqrRef.get();
            // i think it's reasonable to assume that the player will never be exactly 0 away from the snow golem
            // the default for LocalDoubleRef is 0
            if (!(player instanceof FakePlayer) && (distanceSqr == 0 || playerDistanceSqr < distanceSqr)) {
                distanceSqrRef.set(playerDistanceSqr);
                nearestPlayer.set(player);
            }
        }
    }

    @Inject(method = "spawnGolemInWorld", at = @At("RETURN"))
    private static void setSnowGolemOwner(Level level, BlockPatternMatch patternMatch, Entity golem, BlockPos pos,
                                          CallbackInfo ci, @Share("nearestPlayer") LocalRef<@Nullable ServerPlayer> nearestPlayer,
                                          @Share("distanceSqr") LocalDoubleRef distanceSqrRef) {
        if (golem instanceof SnowGolem) {
            Player player = nearestPlayer.get();
            if (player != null)
                golem.setData(GolemObjects.SNOW_GOLEM_OWNER, EntityReference.of(player));
        }
    }

    @ModifyExpressionValue(method = "getOrCreateSnowGolemFull", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/CarvedPumpkinBlock;PUMPKINS_PREDICATE:Ljava/util/function/Predicate;", opcode = Opcodes.GETSTATIC))
    private Predicate<BlockState> addGolemHatAsPumpkin(Predicate<BlockState> original) {
        return original.or(state -> state.is(GolemObjects.GOLEM_HAT));
    }
}
