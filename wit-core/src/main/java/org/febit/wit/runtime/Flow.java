package org.febit.wit.runtime;

import lombok.experimental.Accessors;
import org.febit.wit.runtime.ast.FlowControl;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
public class Flow {

    /**
     * Flow kind.
     */
    @lombok.Getter
    private FlowControl.Kind kind = FlowControl.Kind.NOOP;

    /**
     * Target label id, used by break/continue-controls, 0 for any label.
     */
    private int target;

    /**
     * Value to be returned, used by return-controls.
     */
    @Nullable
    private Object returned;

    public boolean isNoop() {
        return kind.isNoop();
    }

    /**
     * Check if given label is the target of current flow control.
     *
     * @param label target label id
     * @return true if match
     */
    @SuppressWarnings({
            "BooleanMethodIsAlwaysInverted"
    })
    public boolean isTarget(int label) {
        return this.target == 0 || this.target == label;
    }

    /**
     * Change to break control with given label.
     *
     * @param label target label id
     */
    public void toBreak(int label) {
        this.target = label;
        this.kind = FlowControl.Kind.BREAK;
    }

    /**
     * Change to continue control with given label.
     *
     * @param label target label id
     */
    public void toContinue(int label) {
        this.target = label;
        this.kind = FlowControl.Kind.CONTINUE;
    }

    /**
     * Change to return control with given value.
     *
     * @param value the returned.
     */
    public void toReturn(@Nullable Object value) {
        this.returned = value;
        this.target = 0;
        this.kind = FlowControl.Kind.RETURN;
    }

    /**
     * Reset to noop control.
     */
    public void reset() {
        this.returned = null;
        this.target = 0;
        this.kind = FlowControl.Kind.NOOP;
    }

    /**
     * Reset if current control is break and target label matches given label.
     *
     * @param label target label id
     */
    public void resetIfBreak(int label) {
        if (this.kind.isBreak() && isTarget(label)) {
            this.reset();
        }
    }

    /**
     * Get the returned value if current control is return,
     * otherwise return {@link Undefined#UNDEFINED}.
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
