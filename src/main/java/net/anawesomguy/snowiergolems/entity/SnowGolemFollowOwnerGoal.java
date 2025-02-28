package net.anawesomguy.snowiergolems.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.PathType;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SnowGolemFollowOwnerGoal extends Goal {
    private final Mob ownable;
    private final PathNavigation navigation;
    private final double speedModifier;
    private final double startDistanceSqr;
    private final double finishDistanceSqr;
    private int timeToRecalcPath;
    private float oldWaterCost;
    @Nullable
    private LivingEntity owner;

    public <T extends Mob & OwnableEntity> SnowGolemFollowOwnerGoal(T ownable, double speedModifier, double startDistance, double finishDistance) {
        this.ownable = ownable;
        PathNavigation navigation = this.navigation = ownable.getNavigation();
        this.speedModifier = speedModifier;
        this.startDistanceSqr = startDistance * startDistance;
        this.finishDistanceSqr = finishDistance * finishDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        if (!(navigation instanceof GroundPathNavigation) && !(navigation instanceof FlyingPathNavigation))
            throw new IllegalArgumentException("Unsupported navigation type for SnowGolemFollowOwnerGoal");
    }

    @Override
    public boolean canUse() {
        LivingEntity living = ((OwnableEntity)this.ownable).getOwner();
        if (living == null || this.ownable.isLeashed() || this.ownable.distanceToSqr(living) < startDistanceSqr)
            return false;
        this.owner = living;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity living = this.owner;
        return living != null &&
               !this.navigation.isDone() &&
               this.ownable.distanceToSqr(living) > finishDistanceSqr;
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.ownable.getPathfindingMalus(PathType.WATER);
        this.ownable.setPathfindingMalus(PathType.WATER, 0F);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.ownable.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        LivingEntity living = this.owner;
        if (living == null)
            return;
        this.ownable.getLookControl().setLookAt(living, 10F, this.ownable.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            this.navigation.moveTo(living, this.speedModifier);
        }
    }
}
