package net.anawesomguy.snowiergolems.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    private EnchantmentHelperMixin() {
    }

    @ModifyExpressionValue(method = "getComponentType", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private static boolean golemTomeComponent(boolean original, @Local(argsOnly = true) ItemStack stack) {
        return original || stack.is(GolemObjects.GOLEM_TOME);
    }
}
