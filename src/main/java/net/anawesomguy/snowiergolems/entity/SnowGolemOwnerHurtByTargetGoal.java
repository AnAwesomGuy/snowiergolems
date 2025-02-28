package net.anawesomguy.snowiergolems.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class SnowGolemOwnerHurtByTargetGoal extends TargetGoal {
    protected final OwnableEntity ownable;
    protected final Class<? extends LivingEntity>[] ignore;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    @SafeVarargs
    public <T extends Mob & OwnableEntity> SnowGolemOwnerHurtByTargetGoal(T ownable, Class<? extends LivingEntity>... ignore) {
        super(ownable, false);
        this.ownable = ownable;
        this.ignore = ignore;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = this.ownable.getOwner();
        if (owner != null) {
            LivingEntity entity = this.ownerLastHurtBy = owner.getLastHurtByMob();
            if (entity == null)
                return false;

            Class<? extends LivingEntity> hurtByClass = entity.getClass();
            for (Class<? extends LivingEntity> clazz : ignore)
                if (clazz.isAssignableFrom(hurtByClass))
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
