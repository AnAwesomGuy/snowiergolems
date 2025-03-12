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
import net.anawesomguy.snowiergolems.enchant.GolemEnchantments;
import net.anawesomguy.snowiergolems.entity.ConditionalGoal;
import net.anawesomguy.snowiergolems.entity.EnchantedSnowball;
import net.anawesomguy.snowiergolems.entity.OwnableSnowGolem;
import net.anawesomguy.snowiergolems.entity.SnowGolemFollowOwnerGoal;
import net.anawesomguy.snowiergolems.entity.SnowGolemOwnerHurtByTargetGoal;
import net.anawesomguy.snowiergolems.entity.SnowGolemOwnerHurtTargetGoal;
import net.anawesomguy.snowiergolems.util.ExpiringMemoizedBooleanSupplier;
import net.anawesomguy.snowiergolems.util.HolderCacher;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
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
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import java.util.function.BooleanSupplier;

@Mixin(SnowGolem.class)
public abstract class SnowGolemMixin extends AbstractGolem implements OwnableSnowGolem {
    @Unique
    @Nullable
    private UUID ownerUuid;

    @SuppressWarnings("DataFlowIssue")
    private SnowGolemMixin() {
        super(null, null);
        throw new AssertionError();
    }

    @Override
    public void snowiergolems$setOwner(@Nullable UUID uuid) {
        ownerUuid = uuid;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return ownerUuid;
    }

    @Unique // maybe better performance? (hopefully, there are fewer checks and stuff)
    private ItemStack getHeadItem() {
        return Iterables.get(getArmorSlots(), 3);
    }

    @ModifyReturnValue(method = "createAttributes", at = @At("RETURN"))
    private static Builder addAttributes(Builder builder) {
        return builder.add(GolemEnchantments.PROJECTILE_ACCURACY, -11);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void alwaysDropHat(EntityType<? extends SnowGolem> type, Level level, CallbackInfo ci) {
        this.setItemSlotAndDropWhenKilled(EquipmentSlot.HEAD, Items.CARVED_PUMPKIN.getDefaultInstance());
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    public void addOwnerToSaveData(CompoundTag compound, CallbackInfo ci) {
        UUID uuid = ownerUuid;
        if (uuid != null)
            compound.putUUID(NBT_TAG_KEY, uuid);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    public void readOwnerFromSaveData(CompoundTag compound, CallbackInfo ci) {
        if (compound.hasUUID(NBT_TAG_KEY))
            ownerUuid = compound.getUUID(NBT_TAG_KEY);
    }

    @Inject(method = "registerGoals", at = @At("RETURN"))
    private void addCustomGoals(CallbackInfo ci) {
        // yes its hardcoded sorry :( (open a pr cuz i honestly cant be bothered)
        // performance is probably really ass

        // check snowy loyalty
        BooleanSupplier checkLoyalty = new ExpiringMemoizedBooleanSupplier(
            () -> getHeadItem().getEnchantmentLevel(registryAccess().holderOrThrow(GolemEnchantments.SNOWY_LOYALTY)) > 0,
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
                                                       HolderCacher.AGGRESSIVE_ENCHANT.apply(this)) >= 3)); // level 3
        targetSelector.addGoal(-1,
                               new ConditionalGoal(new HurtByTargetGoal(this, SnowGolem.class).setAlertOthers(),
                                                   () -> getHeadItem().getEnchantmentLevel(
                                                       HolderCacher.AGGRESSIVE_ENCHANT.apply(this)) >= 2)); // level 2
        targetSelector.addGoal(0,
                               new ConditionalGoal(new HurtByTargetGoal(this, SnowGolem.class),
                                                   () -> getHeadItem().getEnchantmentLevel(
                                                       HolderCacher.AGGRESSIVE_ENCHANT.apply(this)) >= 1)); // level 1
    }

    // i dont feel like using a wrap-op (i will if you yell at me tho)
    @Redirect(method = "shear", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack dropHat(ItemLike item) {
        return getHeadItem().split(1);
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private void wearHat(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir,
                         @SuppressWarnings("UnresolvedLocalCapture") @Local ItemStack stack) {
        if (stack.is(GolemObjects.GOLEM_HAT_ITEM)) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            setItemSlot(EquipmentSlot.HEAD, stack.consumeAndReturn(1, player));
        }
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean doNotMelt(boolean original) { // yet again hardcoded (i apologize)
        return original && // if the biome causes heat there must be a >0 level of heat-resistant for damage
               (getHeadItem().getEnchantmentLevel(registryAccess().holderOrThrow(GolemEnchantments.HEAT_RESISTANT)) <= 0);
    }

    @WrapMethod(method = "performRangedAttack")
    private void shootMultiple(LivingEntity target, float distanceFactor, Operation<Void> original,
                               @Share("hatStack") LocalRef<ItemStack> hatStack,
                               @Share("enchanted") LocalBooleanRef enchanted,
                               @Share("soundNotPlayed") LocalBooleanRef soundNotPlayed,
                               @Share("spread") LocalFloatRef spreadRef) {
        if (level() instanceof ServerLevel level) {
            ItemStack hat = getHeadItem();
            hatStack.set(hat);
            enchanted.set(!hat.getAllEnchantments(registryAccess().lookupOrThrow(Registries.ENCHANTMENT)).isEmpty());
            spreadRef.set(EnchantmentHelper.processProjectileSpread(level, hat, this, 0F));
            int count = EnchantmentHelper.processProjectileCount(level, hat, this, 1);
            while (count-- > 0)
                original.call(target, distanceFactor);
        }
    }

    @WrapOperation(method = "performRangedAttack", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/entity/projectile/Snowball;"))
    private Snowball enchantSnowball(Level level, LivingEntity shooter, Operation<Snowball> original,
                                     @Share("hatStack") LocalRef<ItemStack> hatStack,
                                     @Share("enchanted") LocalBooleanRef enchanted) {
        return enchanted.get() ? new EnchantedSnowball(level, shooter, hatStack.get()) : original.call(level, shooter);
    }

    @WrapOperation(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/SnowGolem;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    private void playSoundOnce(SnowGolem instance, SoundEvent soundEvent, float volume, float pitch, Operation<Void> original,
                               @Share("soundNotPlayed") LocalBooleanRef soundNotPlayed) {
        if (!soundNotPlayed.get()) {
            soundNotPlayed.set(false);
            original.call(instance, soundEvent, volume, pitch);
        }
    }

    @WrapOperation(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Snowball;shoot(DDDFF)V"))
    private void changeShootAngle(Snowball instance, double x, double y, double z, float velocity, float inaccuracy,
                                  Operation<Void> original, @Share("spread") LocalFloatRef spreadRef) {
        float newInaccuracy = Math.max(0F, (float)-getAttributeValue(GolemEnchantments.PROJECTILE_ACCURACY));
        float spread = spreadRef.get();
        if (spread == 0F)
            original.call(instance, x, y, z, velocity, newInaccuracy);
        else {
            // tysm gigaherz for this code, i wouldve never figured it out
            Vector3d vec3 = Axis.YP.rotationDegrees(spread).transform(x, y, z, new Vector3d());
            spreadRef.set(-spread);
            original.call(instance, vec3.x, y, vec3.z, velocity, newInaccuracy);
        }
    }
}
