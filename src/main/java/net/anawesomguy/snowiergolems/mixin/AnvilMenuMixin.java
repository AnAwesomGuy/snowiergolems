package net.anawesomguy.snowiergolems.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.item.GolemHatItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    private AnvilMenuMixin() {
    }

    @WrapWithCondition(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 3))
    private boolean doNotShrinkIfGolemTome(Container instance, int i, ItemStack stack) {
        return !stack.is(GolemObjects.GOLEM_TOME);
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;setEnchantments(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/ItemEnchantments;)V", shift = Shift.AFTER))
    private void changePumpkinFace(CallbackInfo ci, @Local(ordinal = 2) ItemStack stack) {
        if (stack.is(GolemObjects.GOLEM_HAT_ITEM))
            GolemHatItem.setPumpkinFace(stack);
    }
}
