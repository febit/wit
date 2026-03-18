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
package org.febit.wit.runtime.ast;

import org.febit.wit.Script;
import org.febit.wit.Vars;
import org.febit.wit.io.Out;
import org.febit.wit.runtime.BreakpointHandler;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.statement.StatementBatch;
import org.febit.wit.runtime.heap.GenericHeap;
import org.febit.wit.runtime.heap.VariableHeap;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record ScriptAST(
        long sourceVersion,
        int heapSize,
        List<ScopedIndexer> scopedIndexers,
        StatementBatch body
) {

    public InternalContext execute(
            Script script,
            Out out,
            Vars inputs,
            @Nullable BreakpointHandler handler
    ) {
        var variables = new VariableHeap(heapSize, scopedIndexers);
        inputs.sink(variables::set);

        var local = GenericHeap.local();
        var context = new InternalContext(script, variables, inputs, out, local, handler);
        body.execute(context);
        // assert context.indexer = 0
        return context;
    }

    public InternalContext execute(Script script, InternalContext context, Vars inputs) {
        var variables = new VariableHeap(heapSize, scopedIndexers);
        var newContext = new InternalContext(
                script,
                variables,
                inputs,
                context.out(),
                context.local(),
                context.breakpointHandler()
        );
        body.execute(newContext);
        // assert context.indexer = 0
        return newContext;
    }

}
