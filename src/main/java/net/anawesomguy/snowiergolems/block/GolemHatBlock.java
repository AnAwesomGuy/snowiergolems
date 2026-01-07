package net.anawesomguy.snowiergolems.block;

import net.anawesomguy.snowiergolems.mixin.CarvedPumpkinAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import org.jetbrains.annotations.Nullable;

public class GolemHatBlock extends CarvedPumpkinBlock implements EntityBlock {
    public GolemHatBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSpawnGolem(LevelReader level, BlockPos pos) {
        return ((CarvedPumpkinAccessor)this).callGetOrCreateSnowGolemBase().find(level, pos) != null;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        return level.getBlockEntity(pos) instanceof GolemHatBlockEntity entity ?
            entity.getAsStack() :
            super.getCloneItemStack(level, pos, state, includeData, player);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GolemHatBlockEntity(pos, state);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        AuxiliaryLightManager auxLight = level.getAuxLightManager(pos);
        return auxLight != null ? auxLight.getLightAt(pos) : super.getLightEmission(state, level, pos);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof GolemHatBlockEntity entity) ||
            !entity.hasEnchantment(Enchantments.FLAME)) return;
        // spawn smoke if enchanted with flame
        float y = pos.getY() + 0.6F;
        int x = pos.getX(), z = pos.getZ();
        level.addParticle(ParticleTypes.LARGE_SMOKE,
                          x + random.nextDouble(),
                          y + random.nextDouble() * 0.5 + 0.5,
                          z + random.nextDouble(),
                          0.0, 0.0, 0.0);
        level.addParticle(ParticleTypes.LARGE_SMOKE,
                          x + random.nextDouble(),
                          y + random.nextDouble() * 0.5 + 0.5,
                          z + random.nextDouble(),
                          0.0, 0.0, 0.0);
        level.addParticle(ParticleTypes.LARGE_SMOKE,
                          x + random.nextDouble(),
                          y + random.nextDouble() * 0.5 + 0.5,
                          z + random.nextDouble(),
                          0.0, 0.0, 0.0);
    }

    @Override
    public boolean hasDynamicLightEmission(BlockState state) {
        return true;
    }
}
