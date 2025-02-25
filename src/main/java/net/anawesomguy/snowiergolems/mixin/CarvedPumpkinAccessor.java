package net.anawesomguy.snowiergolems.mixin;

import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CarvedPumpkinBlock.class)
public interface CarvedPumpkinAccessor {
    @Invoker
    BlockPattern callGetOrCreateSnowGolemBase();
}
