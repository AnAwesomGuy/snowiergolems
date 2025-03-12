package net.anawesomguy.snowiergolems.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    private LivingEntityMixin() {
    }

    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Inject(method = "onEquipItem", at = @At("HEAD"))
    private void setPumpkinOnEquip(EquipmentSlot slot, ItemStack oldItem, ItemStack newItem, CallbackInfo ci) {
        //noinspection ConstantValue
        if ((Object)this instanceof SnowGolem golem && slot == EquipmentSlot.HEAD && !newItem.isEmpty())
            golem.setPumpkin(true);
    }

    @WrapOperation(method = "getWeaponItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack changeWeaponItem(LivingEntity instance, Operation<ItemStack> original) {
        //noinspection ConstantValue
        return (Object)this instanceof SnowGolem ? getItemBySlot(EquipmentSlot.HEAD) : original.call(instance);
    }
}
