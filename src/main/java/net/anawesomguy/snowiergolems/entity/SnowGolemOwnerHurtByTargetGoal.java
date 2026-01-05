package net.anawesomguy.snowiergolems.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Objects;

public class SnowGolemOwnerHurtByTargetGoal extends TargetGoal {
    protected final OwnableEntity ownable;
    @Nullable
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public <T extends Mob & OwnableEntity> SnowGolemOwnerHurtByTargetGoal(T ownable) {
        super(ownable, false);
        this.ownable =  Objects.requireNonNull(ownable);
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = this.ownable.getOwner();
        if (owner != null) {
            LivingEntity entity = this.ownerLastHurtBy = owner.getLastHurtByMob();
            if (entity == null)
                return false;

            if (SnowGolem.class.isAssignableFrom(entity.getClass()))
                return false;

            return owner.getLastHurtByMobTimestamp() != this.timestamp
                   && this.canAttack(entity, TargetingConditions.DEFAULT);
        }
        return false;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity owner = this.ownable.getOwner();
        if (owner != null)
            this.timestamp = owner.getLastHurtByMobTimestamp();
        super.start();
    }
}
