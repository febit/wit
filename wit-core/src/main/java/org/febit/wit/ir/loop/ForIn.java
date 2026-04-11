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
import org.febit.wit.ir.expr.FunctionLiteral;
import org.febit.wit.ir.flow.FlowControl;
import org.febit.wit.ir.support.FlowControls;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.iter.Iter;
import org.febit.wit.runtime.iter.Iters;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record ForIn(
        int scope,
        Expression collection,
        @Nullable FunctionLiteral filter,
        int iterSlot,
        int itemSlot,
        LoopBody body,
        @Nullable Statement elseBody,
        Position position
) implements Statement, WithFlowControl {

    @Override
    @Nullable
    public Object execute(RuntimeContext context) {
        Iter iter = iter(context);
        if (iter.hasNext()) {
            context.variables().withScope(scope, () -> execute0(context, iter));
            return null;
        }
        if (elseBody != null) {
            elseBody.execute(context);
        }
        return null;
    }

    private Iter iter(RuntimeContext context) {
        var iter = Iters.ofIter(collection.execute(context), this);
        if (filter == null) {
            return iter;
        }
        return Iters.ofFiltered(context, iter, filter.execute(context));
    }

    @SuppressWarnings({
            "UnnecessaryLocalVariable",
            "squid:S3776", // Cognitive Complexity of methods should not be too high
    })
    private void execute0(RuntimeContext context, Iter iter) {
        var loop = this.body;
        var iSlot = this.itemSlot;
        var heap = context.variables();
        heap.set(iterSlot, iter);
        do {
            heap.set(iSlot, iter.next());
            if (loop.execute(context)) {
                // End this loop if not continue
                break;
            }
        } while (iter.hasNext());
    }

    @Override
    public void bubbleFlowControls(Consumer<FlowControl> collector) {
        this.body.bubbleFlowControls(collector);
        FlowControls.bubble(collector, this.elseBody);
    }
}
