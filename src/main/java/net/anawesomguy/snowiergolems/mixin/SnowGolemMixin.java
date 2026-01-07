package net.anawesomguy.snowiergolems.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.math.Axis;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.enchant.GolemEnchantments;
import net.anawesomguy.snowiergolems.entity.ConditionalGoal;
import net.anawesomguy.snowiergolems.entity.EnchantedSnowball;
import net.anawesomguy.snowiergolems.entity.SnowGolemFollowOwnerGoal;
import net.anawesomguy.snowiergolems.entity.SnowGolemOwnerHurtByTargetGoal;
import net.anawesomguy.snowiergolems.entity.SnowGolemOwnerHurtTargetGoal;
import net.anawesomguy.snowiergolems.util.ExpiringMemoizedBooleanSupplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Mixin(SnowGolem.class)
public abstract class SnowGolemMixin extends AbstractGolem implements OwnableEntity {
    @SuppressWarnings("DataFlowIssue")
    private SnowGolemMixin() {
        super(null, null);
        throw new AssertionError();
    }

    @Nullable
    @Override
    public EntityReference<LivingEntity> getOwnerReference() {
        return getData(GolemObjects.SNOW_GOLEM_OWNER);
    }

    @ModifyReturnValue(method = "createAttributes", at = @At("RETURN"))
    private static Builder addAttributes(Builder builder) {
        return builder.add(GolemEnchantments.PROJECTILE_ACCURACY, -11);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void alwaysDropHat(EntityType<? extends SnowGolem> type, Level level, CallbackInfo ci) {
        this.setItemSlotAndDropWhenKilled(EquipmentSlot.HEAD, Items.CARVED_PUMPKIN.getDefaultInstance());
    }

    @Inject(method = "registerGoals", at = @At("RETURN"))
    private void addCustomGoals(CallbackInfo ci) {
        // yes its hardcoded sorry :( (open a pr cuz i honestly cant be bothered)
        // performance is probably really ass

        // check snowy loyalty
        BooleanSupplier checkLoyalty = new ExpiringMemoizedBooleanSupplier(
            () -> this.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(
                registryAccess().holderOrThrow(GolemEnchantments.SNOWY_LOYALTY)) > 0,
            12);
        goalSelector.addGoal(5,
                             new ConditionalGoal(new SnowGolemFollowOwnerGoal(this, 1.1, 15, 4), checkLoyalty));
        targetSelector.addGoal(-4, // will negative priorities cause issues? i don't think so...
                               new ConditionalGoal(new SnowGolemOwnerHurtByTargetGoal(this), checkLoyalty));
        targetSelector.addGoal(-3,
                               new ConditionalGoal(new SnowGolemOwnerHurtTargetGoal(this), checkLoyalty));
        // check aggressive
        targetSelector.addGoal(-2, // lower priority is searched first, so it'll look for higher levels then lower
                               new ConditionalGoal(
                                   new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                                                                     (entity, level) -> {
                                                                         EntityReference<LivingEntity> reference = getOwnerReference();
                                                                         return reference != null &&
                                                                             reference.matches(entity);
                                                                     }),
                                   () -> this.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(
                                       registryAccess().holderOrThrow(GolemEnchantments.AGGRESSIVE)) >= 3)); // level 3
        targetSelector.addGoal(-1,
                               new ConditionalGoal(new HurtByTargetGoal(this, SnowGolem.class).setAlertOthers(),
                                                   () -> this.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(
                                                       registryAccess().holderOrThrow(GolemEnchantments.AGGRESSIVE)) >=
                                                       2)); // level 2
        targetSelector.addGoal(0,
                               new ConditionalGoal(new HurtByTargetGoal(this, SnowGolem.class),
                                                   () -> this.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(
                                                       registryAccess().holderOrThrow(GolemEnchantments.AGGRESSIVE)) >=
                                                       1)); // level 1
    }

    @SuppressWarnings("InvokeAssignCanReplacedWithExpression")
    @Inject(method = "mobInteract", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private void wearHat(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, @Local ItemStack stack) {
        if (stack.is(GolemObjects.GOLEM_HAT_ITEM) || stack.is(Items.CARVED_PUMPKIN)) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            setItemSlot(EquipmentSlot.HEAD, stack.split(1));
        }
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;getValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;Lnet/minecraft/world/phys/Vec3;)Ljava/lang/Object;"))
    private Object doNotMelt(Object original) {
        // if the biome causes heat there must be a >0 level of heat-resistant to cancel the damage
        return ((Boolean)original) && getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(
            registryAccess().holderOrThrow(GolemEnchantments.HEAT_RESISTANT)) <= 0;
    }

    @WrapOperation(method = "performRangedAttack", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/projectile/throwableitemprojectile/Snowball;"))
    private Snowball enchantSnowball(Level level, LivingEntity shooter, ItemStack stack, Operation<Snowball> original,
                                     @Share("hatStack") LocalRef<ItemStack> hatStack, @Share("enchanted") LocalBooleanRef enchantedRef) {
        ItemStack hat = getItemBySlot(EquipmentSlot.HEAD);
        hatStack.set(hat);
        boolean enchanted = hat.isEnchanted();
        enchantedRef.set(enchanted);
        return enchanted ?
            new EnchantedSnowball(level, shooter, hat) :
            original.call(level, shooter, stack);
    }

    @WrapOperation(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectile(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;)Lnet/minecraft/world/entity/projectile/Projectile;"))
    private Projectile enchantSnowball(Projectile projectile, ServerLevel level, ItemStack stack, Consumer<Projectile> adapter, Operation<Projectile> original,
                                       @Share("hatStack") LocalRef<ItemStack> hatStack, @Share("enchanted") LocalBooleanRef enchantedRef,
                                       @Local(ordinal = 0) LocalDoubleRef x, @Local(ordinal = 1) double y, @Local(ordinal = 2) LocalDoubleRef z) {
        ItemStack hat = hatStack.get();
        boolean enchanted = enchantedRef.get();
        int count;
        if (!enchanted || (count = EnchantmentHelper.processProjectileCount(level, hat, this, 1)) == 0)
            return original.call(projectile, level, stack, adapter);
        float spread = EnchantmentHelper.processProjectileSpread(level, hat, this, 0F);
        // tysm gigaherz for this code, i would've never figured it out
        Vector3d vec = new Vector3d();
        while (--count > 0) { // runs count - 1 times
            Axis.YP.rotationDegrees(spread).transform(x.get(), y, z.get(), vec);
            spread = -spread;
            x.set(vec.x);
            z.set(vec.z);
            original.call(new EnchantedSnowball(level, this, hat), level, stack, adapter);
        }
        // run a last time (for return value)
        Axis.YP.rotationDegrees(spread).transform(x.get(), y, z.get(), vec);
        x.set(vec.x);
        z.set(vec.z);
        return original.call(projectile, level, stack, adapter);
    }

    @WrapOperation(method = "lambda$performRangedAttack$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/Snowball;shoot(DDDFF)V"))
    private static void changeShootAngle(Snowball snowball, double x, double y, double z, float velocity, float inaccuracy, Operation<Void> original) {
        // noinspection DataFlowIssue (owner will not be null)
        original.call(snowball, x, y, z, velocity,
                      Math.max(0F, -(float)((LivingEntity)snowball.getOwner()).getAttributeValue(
                          GolemEnchantments.PROJECTILE_ACCURACY)));
    }
}
