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

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.IBlock;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public record BlockNonFlow(
        int scope,
        StatementBatch bodyBatch,
        Position position
) implements IBlock {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.variables().onScope(scope,
                () -> bodyBatch.execute(context)
        );
        return null;
    }

    @Override
    public List<StatementBatch> body() {
        return List.of(bodyBatch);
    }

    @Override
    public void bubbleFlowControls(Consumer<FlowControl> collector) {
        // No flow control.
    }

    @Override
    public Statement optimize() {
        return bodyBatch.isEmpty() ? NoopStatement.INSTANCE : this;
    }
}
