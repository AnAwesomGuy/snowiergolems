package net.anawesomguy.snowiergolems.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    @WrapOperation(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void doNotShrinkIfGolemTome(ItemStack stack, int decrement, Operation<Void> original) {
        if (!stack.is(GolemObjects.GOLEM_TOME))
            original.call(stack, decrement);
    }
}
