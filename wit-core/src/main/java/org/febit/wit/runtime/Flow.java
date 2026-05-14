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
import org.febit.wit.ir.flow.JumpKind;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
public class Flow {

    /**
     * Flow state.
     */
    @lombok.Getter
    private JumpKind state = JumpKind.NOOP;

    /**
     * Target label id, used by break/continue-controls, 0 for any label.
     */
    private int target;

    /**
     * Value to be returned or yielded.
     */
    @Nullable
    private Object returned;

    public boolean isNoop() {
        return state == JumpKind.NOOP;
    }

    /**
     * Check if given label is the target of current jump.
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
     * Change to break state with given label.
     *
     * @param label target label id
     */
    public void toBreak(int label) {
        this.target = label;
        this.state = JumpKind.BREAK;
    }

    /**
     * Change to continue state with given label.
     *
     * @param label target label id
     */
    public void toContinue(int label) {
        this.target = label;
        this.state = JumpKind.CONTINUE;
    }

    /**
     * Change to return state with given value.
     *
     * @param value the returned.
     */
    public void toReturn(@Nullable Object value) {
        this.returned = value;
        this.target = 0;
        this.state = JumpKind.RETURN;
    }

    /**
     * Change to yield state with given value.
     *
     * @param value the yielded value.
     */
    public void toYield(@Nullable Object value) {
        this.returned = value;
        this.target = 0;
        this.state = JumpKind.YIELD;
    }

    /**
     * Reset to noop state.
     */
    public void reset() {
        this.returned = null;
        this.target = 0;
        this.state = JumpKind.NOOP;
    }

    /**
     * Reset if current state is break and target label matches given label.
     *
     * @param label target label id
     */
    public void resetIfBreak(int label) {
        if (this.state.isBreak() && isTarget(label)) {
            this.reset();
        }
    }

    /**
     * Get the yielded value if current state is yield.
     *
     * @return the yielded value
     * @throws ScriptEvaluateException if current state is not yield
     */
    @Nullable
    public Object yieldedAndReset() {
        if (!this.state.isYield()) {
            throw new ScriptEvaluateException(
                    "Invalid flow state, expected YIELD but was " + this.state);
        }
        var result = this.returned;
        this.reset();
        return result;
    }

    /**
     * Get the returned value if current state is return.
     *
     * @return the returned value, or Undefined if current state is noop.
     * @throws ScriptEvaluateException if current state is not return or noop
     */
    @Nullable
    public Object returned() {
        return switch (this.state) {
            case RETURN -> this.returned;
            case NOOP -> Undefined.UNDEFINED;
            default -> throw new ScriptEvaluateException(
                    "Invalid flow state, expected RETURN or NOOP but was " + this.state);
        };
    }

}
