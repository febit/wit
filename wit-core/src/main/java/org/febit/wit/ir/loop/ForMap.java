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
import org.febit.wit.ir.JumpAware;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.Statement;
import org.febit.wit.ir.expr.FunctionLiteral;
import org.febit.wit.ir.flow.Jump;
import org.febit.wit.ir.support.Jumps;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.iter.Iters;
import org.febit.wit.runtime.iter.KeyIter;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record ForMap(
        int scope,
        Expression collection,
        @Nullable FunctionLiteral filter,
        int iterSlot,
        int keySlot,
        int valueSlot,
        LoopBody body,
        @Nullable Statement elseBody,
        Position position
) implements Statement, JumpAware {

    @Override
    @Nullable
    public Object execute(RuntimeContext context) {
        var iter = iter(context);
        if (iter.hasNext()) {
            context.variables().withScope(scope, () -> execute0(context, iter));
            return null;
        }
        if (elseBody != null) {
            elseBody.execute(context);
        }
        return null;
    }

    private KeyIter iter(RuntimeContext context) {
        var iter = Iters.ofKeyIter(collection.execute(context), this);
        if (filter == null) {
            return iter;
        }
        return Iters.ofFiltered(context, iter, filter.execute(context));
    }

    @SuppressWarnings({
            "UnnecessaryLocalVariable",
            "squid:S3776", // Cognitive Complexity of methods should not be too high
    })
    private void execute0(RuntimeContext context, KeyIter iter) {
        var loop = this.body;
        var kSlot = this.keySlot;
        var vSlot = this.valueSlot;
        var heap = context.variables();
        heap.set(iterSlot, iter);
        do {
            heap.set(
                    kSlot, iter.next(),
                    vSlot, iter.value()
            );
            if (loop.execute(context)) {
                // End this loop if not continue
                break;
            }
        } while (iter.hasNext());
    }

    @Override
    public void collectJumps(Consumer<Jump> collector) {
        this.body.collectJumps(collector);
        Jumps.collect(collector, this.elseBody);
    }
}
