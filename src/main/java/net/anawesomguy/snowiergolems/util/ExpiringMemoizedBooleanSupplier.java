package net.anawesomguy.snowiergolems.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public final class ExpiringMemoizedBooleanSupplier implements BooleanSupplier {
    private final BooleanSupplier delegate;
    private final int callsTilExpire;
    private int calls;
    private boolean value;

    public ExpiringMemoizedBooleanSupplier(@NotNull BooleanSupplier delegate, int callsTilExpire) {
        if (callsTilExpire < 0)
            throw new IllegalArgumentException("callsTilExpire negative: " + callsTilExpire);
        this.delegate = Objects.requireNonNull(delegate);
        this.calls = this.callsTilExpire = callsTilExpire;
    }

    @Override
    public boolean getAsBoolean() {
        if (++calls > callsTilExpire) {
            calls = 0;
            return value = delegate.getAsBoolean();
        }
        return value;
    }
}
