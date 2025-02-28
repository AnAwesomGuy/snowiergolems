package net.anawesomguy.snowiergolems.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class SnowGolemOwnerHurtTargetGoal extends TargetGoal {
    protected final OwnableEntity ownable;
    protected final Class<? extends LivingEntity>[] ignore;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    @SafeVarargs
    public <T extends Mob & OwnableEntity> SnowGolemOwnerHurtTargetGoal(T ownable, Class<? extends LivingEntity>... ignore) {
        super(ownable, false);
        this.ownable = ownable;
        this.ignore = ignore;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = this.ownable.getOwner();
        if (owner != null) {
            LivingEntity entity = this.ownerLastHurt = owner.getLastHurtMob();
            if (entity == null)
                return false;

            Class<? extends LivingEntity> hurtClass = entity.getClass();
            for (Class<? extends LivingEntity> clazz : ignore)
                if (clazz.isAssignableFrom(hurtClass))
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
