package net.anawesomguy.snowiergolems.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.item.GolemHatItem;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin {
    @ModifyReturnValue(method = "computeResult", at = @At("RETURN"))
    private ItemStack changeFaceOnDisenchant(ItemStack original) {
        if (original.is(GolemObjects.GOLEM_HAT_ITEM))
            GolemHatItem.setPumpkinFace(original);
        return original;
    }
}
