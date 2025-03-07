package net.anawesomguy.snowiergolems.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.item.GolemHeadItem;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    @WrapOperation(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void doNotShrinkIfGolemTome(ItemStack stack, int decrement, Operation<Void> original) {
        if (!stack.is(GolemObjects.GOLEM_TOME))
            original.call(stack, decrement);
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;setEnchantments(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/ItemEnchantments;)V"))
    private void changePumpkinFace(CallbackInfo ci, @Local(ordinal = 2) ItemStack stack) {
        if (stack.is(GolemObjects.GOLEM_HEAD_ITEM))
            GolemHeadItem.updatePumpkinFace(stack);
    }
}
