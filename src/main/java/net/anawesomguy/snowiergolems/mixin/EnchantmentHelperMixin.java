package net.anawesomguy.snowiergolems.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    private EnchantmentHelperMixin() {
    }

    @WrapOperation(method = "getComponentType", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private static boolean golemTomeComponent(ItemStack stack, Item item, Operation<Boolean> original) {
        return original.call(stack, item) || original.call(stack, GolemObjects.GOLEM_TOME);
    }
}
