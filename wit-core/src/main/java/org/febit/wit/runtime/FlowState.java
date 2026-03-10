package org.febit.wit.runtime;

public enum FlowState {
    NOOP,
    BREAK,
    CONTINUE,
    RETURN,
    ;

    public boolean isBreak() {
        return this == BREAK;
    }

    public boolean isReturn() {
        return this == RETURN;
    }

    public boolean isBreakOrContinue() {
        return this == BREAK || this == CONTINUE;
    }

    public boolean isNoop() {
        return this == NOOP;
    }
}
