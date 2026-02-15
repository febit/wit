package org.febit.wit.runtime;

import lombok.experimental.Accessors;
import org.febit.wit.runtime.ast.LoopFlag;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
public class Loop {

    /**
     * Loop kind.
     */
    @lombok.Getter
    private LoopFlag.Kind kind = LoopFlag.Kind.NOOP;

    /**
     * Target label.
     */
    private int label;

    /**
     * Value to be returned, used by return-loops.
     */
    @Nullable
    private Object returned;

    public boolean isNoop() {
        return kind.isNoop();
    }

    /**
     * if gaven loop label matched current loop.
     *
     * @param label label id
     * @return true if match
     */
    @SuppressWarnings({
            "BooleanMethodIsAlwaysInverted"
    })
    public boolean isTargetLabel(int label) {
        return this.label == 0 || this.label == label;
    }

    /**
     * Mark a break-loop.
     *
     * @param label label id
     */
    public void toBreak(int label) {
        this.label = label;
        this.kind = LoopFlag.Kind.BREAK;
    }

    /**
     * Mark a continue-loop.
     *
     * @param label label id
     */
    public void toContinue(int label) {
        this.label = label;
        this.kind = LoopFlag.Kind.CONTINUE;
    }

    /**
     * Mark a return-loop.
     *
     * @param value the returned.
     */
    public void toReturn(@Nullable Object value) {
        this.returned = value;
        this.label = 0;
        this.kind = LoopFlag.Kind.RETURN;
    }

    /**
     * Unmark loops.
     */
    public void reset() {
        this.returned = null;
        this.label = 0;
        this.kind = LoopFlag.Kind.NOOP;
    }

    /**
     * Unmark loops, is a break and match the label.
     *
     * @param label label id
     */
    public void resetIfBreak(int label) {
        if (this.kind.isBreak() && isTargetLabel(label)) {
            this.reset();
        }
    }

    /**
     * Unmark loops, at the end of functions.
     *
     * @return the returned
     */
    @Nullable
    public Object returned() {
        return this.kind.isReturn()
                ? this.returned
                : Undefined.UNDEFINED;
    }

}
