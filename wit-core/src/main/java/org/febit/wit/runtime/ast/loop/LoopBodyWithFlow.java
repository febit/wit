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
package org.febit.wit.runtime.ast.loop;

import org.febit.wit.runtime.FlowControl;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.ast.StatementBatch;

import java.util.List;
import java.util.function.Consumer;

public record LoopBodyWithFlow(
        int targetLabel,
        List<StatementBatch> batches,
        List<FlowControl> bubbledFlowControls
) implements LoopBody {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(RuntimeContext context) {
        context.visitBatches(this.batches);
        var flow = context.flow();
        if (flow.isNoop()) {
            // Continue to next iteration
            return false;
        }
        if (!flow.isTarget(targetLabel)) {
            // Interrupt loop, sine ctrl cannot be handled by current loop
            return true;
        }
        return switch (flow.state()) {
            case RETURN -> true; // Interrupt loop, loops cannot handle return control.
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
    public void bubbleFlowControls(Consumer<FlowControl> collector) {
        bubbledFlowControls.forEach(collector);
    }
}
