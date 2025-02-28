package net.anawesomguy.snowiergolems.mixin;

import com.google.common.collect.Iterables;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
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
import net.anawesomguy.snowiergolems.enchant.EnchantmentGetter;
import net.anawesomguy.snowiergolems.enchant.GolemEnchantments;
import net.anawesomguy.snowiergolems.entity.ConditionalGoal;
import net.anawesomguy.snowiergolems.entity.EnchantedSnowball;
import net.anawesomguy.snowiergolems.entity.OwnableSnowGolem;
import net.anawesomguy.snowiergolems.entity.SnowGolemFollowOwnerGoal;
import net.anawesomguy.snowiergolems.entity.SnowGolemOwnerHurtByTargetGoal;
import net.anawesomguy.snowiergolems.entity.SnowGolemOwnerHurtTargetGoal;
import net.anawesomguy.snowiergolems.util.ExpiringMemoizedBooleanSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(SnowGolem.class)
public abstract class SnowGolemMixin extends AbstractGolem implements OwnableSnowGolem {
    @Unique
    private static final EnchantmentGetter AGGRESSIVE_ENCHANT = new EnchantmentGetter(GolemEnchantments.AGGRESSIVE);

    @Unique
    private static final EnchantmentGetter SNOWY_LOYALTY_ENCHANT = new EnchantmentGetter(GolemEnchantments.SNOWY_LOYALTY);

    @Unique
    private static final EnchantmentGetter HEAT_RESIST_ENCHANT = new EnchantmentGetter(GolemEnchantments.HEAT_RESISTANT);

    @SuppressWarnings("DataFlowIssue")
    private SnowGolemMixin() {
        super(null, null);
        throw new AssertionError();
    }

    @ModifyReturnValue(method = "createAttributes", at = @At("RETURN"))
    private static Builder addAttributes(Builder builder) {
        return builder.add(GolemEnchantments.PROJECTILE_ACCURACY, -11);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void alwaysDropHat(EntityType<? extends SnowGolem> type, Level level, CallbackInfo ci) {
        this.setItemSlotAndDropWhenKilled(EquipmentSlot.HEAD, Items.CARVED_PUMPKIN.getDefaultInstance());
    }

    @Unique // maybe better performance? (hopefully, there are fewer checks and stuff)
    private ItemStack getHeadItem() {
        return Iterables.get(getArmorSlots(), 3);
    }

    @Inject(method = "registerGoals", at = @At("RETURN"))
    private void addCustomGoals(CallbackInfo ci) {
        // yes its hardcoded sorry :( (open a pr cuz i honestly cant be bothered)
        // performance is probably really ass

        // check snowy loyalty
        BooleanSupplier checkLoyalty = new ExpiringMemoizedBooleanSupplier(
            () -> getHeadItem().getEnchantmentLevel(SNOWY_LOYALTY_ENCHANT.apply(this)) > 0,
            12);
        goalSelector.addGoal(5,
                             new ConditionalGoal(new SnowGolemFollowOwnerGoal(this, 1.1, 15, 4), checkLoyalty));
        targetSelector.addGoal(-4, // do negative priorities matter? i dont think so...
                               new ConditionalGoal(new SnowGolemOwnerHurtByTargetGoal(this, SnowGolem.class), checkLoyalty));
        targetSelector.addGoal(-3,
                               new ConditionalGoal(new SnowGolemOwnerHurtTargetGoal(this, SnowGolem.class), checkLoyalty));
        // check aggressive
        targetSelector.addGoal(-2, // lower priority is searched first, so it'll look for higher levels then lower
                               new ConditionalGoal(new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                                                                                     this::snowiergolems$isOwner),
                                                   () -> getHeadItem().getEnchantmentLevel(
                                                       AGGRESSIVE_ENCHANT.apply(this)) >= 3)); // level 3
        targetSelector.addGoal(-1,
                               new ConditionalGoal(new HurtByTargetGoal(this, SnowGolem.class).setAlertOthers(),
                                                   () -> getHeadItem().getEnchantmentLevel(
                                                       AGGRESSIVE_ENCHANT.apply(this)) >= 2)); // level 2
        targetSelector.addGoal(0,
                               new ConditionalGoal(new HurtByTargetGoal(this, SnowGolem.class),
                                                   () -> getHeadItem().getEnchantmentLevel(
                                                       AGGRESSIVE_ENCHANT.apply(this)) >= 1)); // level 1
    }

    @Redirect(method = "shear", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack dropHat(ItemLike item) {
        return getHeadItem();
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private void wearHat(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir,
                         @SuppressWarnings("UnresolvedLocalCapture") @Local ItemStack stack) {
        if (stack.is(GolemObjects.GOLEM_HEAD_ITEM)) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            setItemSlot(EquipmentSlot.HEAD, stack.copyWithCount(1));
        }
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean doNotMelt(boolean original) { // yet again hardcoded (i apologize)
        return original && (getHeadItem().getEnchantmentLevel(HEAT_RESIST_ENCHANT.apply(this)) > 0);
    }

    @WrapMethod(method = "performRangedAttack")
    private void shootMultiple(LivingEntity target, float distanceFactor, Operation<Void> original,
                               @Share("headStack") LocalRef<ItemStack> headStack,
                               @Share("enchantedSnowball") LocalBooleanRef enchants,
                               @Share("playedSound") LocalBooleanRef playedSound,
                               @Share("spread") LocalFloatRef spreadRef) {
        if (level() instanceof ServerLevel level) {
            ItemStack head = getHeadItem();
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
                               @Share("playedSound") LocalBooleanRef playedSound) {
        if (playedSound.get()) {
            playedSound.set(true);
            original.call(instance, soundEvent, volume, pitch);
        }
    }

    @WrapOperation(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Snowball;shoot(DDDFF)V"))
    private void changeShootAngle(Snowball instance, double x, double y, double z, float velocity, float inaccuracy,
                                  Operation<Void> original, @Share("spread") LocalFloatRef spreadRef) {
        float newInaccuracy = Math.max(0F, (float)-getAttributeValue(GolemEnchantments.PROJECTILE_ACCURACY));
        float spread = spreadRef.get();
        if (spread == 0)
            original.call(instance, x, y, z, velocity, newInaccuracy);
        else {
            // tysm gigaherz for this code, i wouldve never figured it out
            Vector3d vec3 = Axis.YP.rotationDegrees(spread).transform(x, y, z, new Vector3d());
            spreadRef.set(-spread);
            original.call(instance, vec3.x, y, vec3.z, velocity, newInaccuracy);
        }
    }
}
