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
package org.febit.wit.ir;

import lombok.RequiredArgsConstructor;
import org.febit.wit.ir.flow.FlowControl;
import org.febit.wit.ir.support.FlowControls;
import org.febit.wit.runtime.RuntimeContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * A batch of statements, used for internal optimization, not for AST.
 * <p>
 * NOTICE: Except the last one, statements in batch should not have flow control.
 */
@RequiredArgsConstructor(staticName = "of")
public class StatementBatch implements WithFlowControl {

    private final Statement[] statements;

    public static StatementBatch empty() {
        return new StatementBatch(new Statement[0]);
    }

    public static StatementBatch of(List<Statement> statements) {
        return new StatementBatch(statements.toArray(Statement[]::new));
    }

    public void execute(RuntimeContext context) {
        for (var stat : statements) {
            stat.execute(context);
        }
    }

    public List<Statement> asList() {
        return List.of(this.statements);
    }

    public boolean isEmpty() {
        return this.statements.length == 0;
    }

    @Override
    public void bubbleFlowControls(Consumer<FlowControl> collector) {
        FlowControls.bubble(collector, statements);
    }
}
