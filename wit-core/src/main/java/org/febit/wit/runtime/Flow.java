/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.runtime;

import lombok.experimental.Accessors;
import org.febit.wit.exception.ScriptEvaluateException;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
public class Flow {

    /**
     * Flow state.
     */
    @lombok.Getter
    private FlowState state = FlowState.NOOP;

    /**
     * Target label id, used by break/continue-controls, 0 if not specified.
     */
    private int target;

    /**
     * Value to be returned, used by return-controls.
     */
    @Nullable
    private Object returned;

    public boolean isNoop() {
        return state == FlowState.NOOP;
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
        this.state = FlowState.BREAK;
    }

    /**
     * Change to continue control with given label.
     *
     * @param label target label id
     */
    public void toContinue(int label) {
        this.target = label;
        this.state = FlowState.CONTINUE;
    }

    /**
     * Change to return control with given value.
     *
     * @param value the returned.
     */
    public void toReturn(@Nullable Object value) {
        this.returned = value;
        this.target = 0;
        this.state = FlowState.RETURN;
    }

    /**
     * Reset to noop control.
     */
    public void reset() {
        this.returned = null;
        this.target = 0;
        this.state = FlowState.NOOP;
    }

    /**
     * Reset if current control is break and target label matches given label.
     *
     * @param label target label id
     */
    public void resetIfBreak(int label) {
        if (this.state.isBreak() && isTarget(label)) {
            this.reset();
        }
    }

    /**
     * Get the returned value if current control is return, then reset to noop control.
     *
     * @return the returned
     * @throws ScriptEvaluateException if current control is not return or noop
     */
    @Nullable
    public Object returnAndReset() {
        return switch (this.state) {
            case RETURN -> {
                var ret = this.returned;
                reset();
                yield ret;
            }
            case NOOP -> Undefined.UNDEFINED;
            default -> throw new ScriptEvaluateException("Flow control leaks when returning: " + this.state);
        };
    }

}
