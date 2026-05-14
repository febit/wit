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

import org.febit.wit.ir.StatementBatch;
import org.febit.wit.ir.flow.Jump;
import org.febit.wit.runtime.RuntimeContext;

import java.util.List;
import java.util.function.Consumer;

public record JumpAwareLoopBody(
        int targetLabel,
        List<StatementBatch> body,
        List<Jump> jumps
) implements LoopBody {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(RuntimeContext context) {
        var flow = context.flow();
        var batches = this.body;
        for (int i = 0, len = batches.size(); i < len && flow.isNoop(); i++) {
            batches.get(i).execute(context);
        }
        if (flow.isNoop()) {
            // Continue to next iteration
            return false;
        }
        if (!flow.isTarget(targetLabel)) {
            // Interrupt loop, sine flow cannot be handled by current loop
            return true;
        }
        return switch (flow.state()) {
            case RETURN -> true; // Interrupt loop, loops cannot handle return flow.
            case YIELD -> true; // Interrupt loop, loops cannot handle yield flow.
            case BREAK -> {
                // Reset & Interrupt loop.
                flow.reset();
                yield true;
            }
            case CONTINUE -> {
                // Reset & Continue to next iteration.
                flow.reset();
                yield false;
            }
            case NOOP -> throw new IllegalStateException("unexpected NOOP");
        };
    }

    @Override
    public void collectJumps(Consumer<Jump> collector) {
        jumps.forEach(collector);
    }
}
