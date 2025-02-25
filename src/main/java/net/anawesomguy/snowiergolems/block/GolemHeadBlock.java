package net.anawesomguy.snowiergolems.block;

import net.anawesomguy.snowiergolems.mixin.CarvedPumpkinAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class GolemHeadBlock extends CarvedPumpkinBlock implements EntityBlock {
    public GolemHeadBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSpawnGolem(LevelReader level, BlockPos pos) {
        return ((CarvedPumpkinAccessor)(this)).callGetOrCreateSnowGolemBase().find(level, pos) != null;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return level.getBlockEntity(pos) instanceof GolemHeadBlockEntity entity ?
            entity.getAsStack() :
            super.getCloneItemStack(state, target, level, pos, player);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GolemHeadBlockEntity(pos, state);
    }
}
