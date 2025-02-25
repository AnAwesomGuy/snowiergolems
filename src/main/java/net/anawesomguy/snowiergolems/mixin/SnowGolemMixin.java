package net.anawesomguy.snowiergolems.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.math.Axis;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.enchant.GolemEnchantments;
import net.anawesomguy.snowiergolems.entity.EnchantedSnowball;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowGolem.class)
public abstract class SnowGolemMixin extends AbstractGolem {
    @SuppressWarnings("DataFlowIssue")
    private SnowGolemMixin() {
        super(null, null);
        throw new AssertionError();
    }

    @ModifyReturnValue(method = "createAttributes", at = @At("RETURN"))
    private static Builder addAttributes(Builder builder) {
        return builder.add(GolemEnchantments.PROJECTILE_ACCURACY, -10.5);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void alwaysDropHat(EntityType<? extends SnowGolem> type, Level level, CallbackInfo ci) {
        this.setGuaranteedDrop(EquipmentSlot.HEAD);
        this.setItemSlot(EquipmentSlot.HEAD, GolemObjects.GOLEM_HEAD_ITEM.getDefaultInstance());
    }

    @Redirect(method = "shear", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack dropHat(ItemLike item) {
        return getItemBySlot(EquipmentSlot.HEAD);
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private void wearHat(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir,
                         @SuppressWarnings("UnresolvedLocalCapture") @Local ItemStack stack) {
        if (stack.is(GolemObjects.GOLEM_HEAD_ITEM)) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            setItemSlot(EquipmentSlot.HEAD, stack.copyWithCount(1));
        }
    }

    @WrapMethod(method = "performRangedAttack")
    private void shootMultiple(LivingEntity target, float distanceFactor, Operation<Void> original,
                               @Share("headStack") LocalRef<ItemStack> headStack,
                               @Share("enchantedSnowball") LocalBooleanRef enchants,
                               @Share("soundPlayed") LocalBooleanRef soundPlayed,
                               @Share("spread") LocalFloatRef spreadRef) {
        if (level() instanceof ServerLevel level) {
            ItemStack head = getItemBySlot(EquipmentSlot.HEAD);
            headStack.set(head);
            enchants.set(head.getAllEnchantments(registryAccess().lookupOrThrow(Registries.ENCHANTMENT)).isEmpty());
            spreadRef.set(EnchantmentHelper.processProjectileSpread(level, head, this, 0F));
            int count = EnchantmentHelper.processProjectileCount(level, head, this, 1);
            while (count-- > 0)
                original.call(target, distanceFactor);
        }
    }

    @WrapOperation(method = "performRangedAttack", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/entity/projectile/Snowball;"))
    private Snowball enchantSnowball(Level level, LivingEntity shooter, Operation<Snowball> original,
                                     @Share("headStack") LocalRef<ItemStack> headStack,
                                     @Share("enchantedSnowball") LocalBooleanRef enchanted) {
        return enchanted.get() ? original.call(level, shooter) : new EnchantedSnowball(level, shooter, headStack.get());
    }

    @WrapOperation(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/SnowGolem;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    private void playSoundOnce(SnowGolem instance, SoundEvent soundEvent, float volume, float pitch, Operation<Void> original,
                               @Share("soundPlayed") LocalBooleanRef soundPlayed) {
        if (soundPlayed.get()) {
            soundPlayed.set(true);
            original.call(instance, soundEvent, volume, pitch);
        }
    }

    @WrapOperation(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Snowball;shoot(DDDFF)V"))
    private void changeShootAngle(Snowball instance, double x, double y, double z, float velocity, float inaccuracy,
                                  Operation<Void> original, @Share("spread") LocalFloatRef spreadRef) {
        float newInaccuracy = Math.max(0F, (float)-getAttributeValue(GolemEnchantments.PROJECTILE_ACCURACY));
        float spread = spreadRef.get();
        if (spread == 0) {
            original.call(instance, x, y, z, velocity, newInaccuracy);
        } else {
            //tysm gigaherz for this code, i wouldve never figured it out
            Vector3d vec3 = Axis.YP.rotationDegrees(spread).transform(x, y, z, new Vector3d());
            spreadRef.set(-spread);
            original.call(instance, vec3.x, y, vec3.z, velocity, newInaccuracy);
        }
    }
}
