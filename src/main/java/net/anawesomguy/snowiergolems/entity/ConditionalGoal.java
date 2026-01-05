package net.anawesomguy.snowiergolems.entity;

import net.anawesomguy.snowiergolems.util.ExpiringMemoizedBooleanSupplier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.function.BooleanSupplier;

public final class ConditionalGoal extends Goal {
    public final Goal delegateGoal;
    private final BooleanSupplier predicate;

    public ConditionalGoal(Goal delegateGoal, BooleanSupplier predicate, int cacheCalls) {
        this.delegateGoal = delegateGoal;
        // cache it until called cacheCalls times
        this.predicate = cacheCalls <= 0 || predicate instanceof ExpiringMemoizedBooleanSupplier ?
            predicate :
            new ExpiringMemoizedBooleanSupplier(predicate, cacheCalls);
    }

    public ConditionalGoal(Goal delegateGoal, BooleanSupplier predicate) {
        this(delegateGoal, predicate, 8); // default of 8 calls before the cache expires
    }

    @Override
    public boolean canUse() {
        return predicate.getAsBoolean() && delegateGoal.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return /*predicate.getAsBoolean() &&*/ delegateGoal.canContinueToUse();
    }

    @Override
    public boolean isInterruptable() {
        return delegateGoal.isInterruptable();
    }

    @Override
    public void start() {
        delegateGoal.start();
    }

    @Override
    public void stop() {
        delegateGoal.stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return delegateGoal.requiresUpdateEveryTick();
    }

    @Override
    public void tick() {
        delegateGoal.tick();
    }

    @Override
    public void setFlags(EnumSet<Flag> flagSet) {
        delegateGoal.setFlags(flagSet);
    }

    @Override
    public String toString() {
        return "ConditionalGoal[" + delegateGoal + ']';
    }

    @Override
    public EnumSet<Flag> getFlags() {
        return delegateGoal.getFlags();
    }
}
