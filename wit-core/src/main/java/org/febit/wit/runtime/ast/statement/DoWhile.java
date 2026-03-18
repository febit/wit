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
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.WithFlowControl;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public record DoWhile(
        int label,
        int scope,
        Expression condition,
        List<StatementBatch> body,
        List<FlowControl> bubbledFlowControls,
        Position position
) implements Statement, WithFlowControl {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.variables().onScope(scope, () -> execute0(context));
        return null;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void execute0(InternalContext context) {
        var batches = this.body;
        var myLabel = this.label;
        var cond = this.condition;
        do {
            if (context.visitLoopBody(batches, myLabel)) {
                // End this loop if not continue
                break;
            }
        } while (ALU.isTruly(cond.execute(context)));
    }

    @Override
    public void bubbleFlowControls(Consumer<FlowControl> collector) {
        bubbledFlowControls.forEach(collector);
    }
}
