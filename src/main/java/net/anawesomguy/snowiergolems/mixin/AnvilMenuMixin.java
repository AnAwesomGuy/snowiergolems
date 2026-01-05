package net.anawesomguy.snowiergolems.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    private AnvilMenuMixin() {
    }

    @WrapWithCondition(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 3))
    private boolean doNotShrinkIfGolemTome(Container instance, int i, ItemStack empty) {
        return !instance.getItem(1).is(GolemObjects.GOLEM_TOME);
    }
}
