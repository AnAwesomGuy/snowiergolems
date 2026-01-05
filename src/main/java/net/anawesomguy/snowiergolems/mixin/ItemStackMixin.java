package net.anawesomguy.snowiergolems.mixin;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.anawesomguy.snowiergolems.block.GolemHatBlockEntity;
import net.anawesomguy.snowiergolems.item.GolemHatItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    private ItemStackMixin() {
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("TAIL"))
    private void verifyComponents(ItemLike itemLike, int count, PatchedDataComponentMap components, CallbackInfo ci) {
        if (count > 0 && itemLike.asItem() instanceof GolemHatItem) {
            Byte b = components.get(GolemObjects.PUMPKIN_FACE);
            if (b != null && !GolemHatBlockEntity.isValidFaceId(b))
                components.set(GolemObjects.PUMPKIN_FACE, // inline GolemHatItem.setPumpkinFace
                               GolemHatBlockEntity.calculateFaceId(null, components.get(DataComponents.ENCHANTMENTS)));
        }
    }
}
