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
package org.febit.wit.ir.loop;

import org.febit.wit.ir.Expression;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.Statement;
import org.febit.wit.ir.WithFlowControl;
import org.febit.wit.ir.flow.FlowControl;
import org.febit.wit.ir.support.ALU;
import org.febit.wit.runtime.RuntimeContext;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record While(
        int scope,
        Expression condition,
        LoopBody body,
        Position position
) implements Statement, WithFlowControl {

    @Override
    @Nullable
    public Object execute(RuntimeContext context) {
        context.variables().withScope(scope, () -> execute0(context));
        return null;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void execute0(RuntimeContext context) {
        var loop = this.body;
        var cond = this.condition;
        while (ALU.isTruly(cond.execute(context))) {
            if (loop.execute(context)) {
                // End this loop if not continue
                break;
            }
        }
    }

    @Override
    public void bubbleFlowControls(Consumer<FlowControl> collector) {
        this.body.bubbleFlowControls(collector);
    }
}
