package net.anawesomguy.snowiergolems.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.SnowGolem;

import java.util.EnumSet;
import java.util.Objects;

public class SnowGolemOwnerHurtTargetGoal extends TargetGoal {
    protected final OwnableEntity ownable;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public <T extends Mob & OwnableEntity> SnowGolemOwnerHurtTargetGoal(T ownable) {
        super(ownable, false);
        this.ownable = Objects.requireNonNull(ownable);
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = this.ownable.getOwner();
        if (owner != null) {
            LivingEntity entity = this.ownerLastHurt = owner.getLastHurtMob();
            if (entity == null)
                return false;

            if (SnowGolem.class.isAssignableFrom(entity.getClass()))
                return false;

            return owner.getLastHurtMobTimestamp() != this.timestamp
                   && this.canAttack(entity, TargetingConditions.DEFAULT);
        }
        return false;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        LivingEntity owner = this.ownable.getOwner();
        if (owner != null)
            this.timestamp = owner.getLastHurtMobTimestamp();
        super.start();
    }
}
