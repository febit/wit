// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.Script;
import org.febit.wit.Vars;
import org.febit.wit.io.Out;
import org.febit.wit.runtime.BreakpointHandler;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.heap.GenricHeap;
import org.febit.wit.runtime.heap.VariableHeap;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public final class ScriptAST {

    private final Statement[] statements;
    private final FrameIndexer[] indexers;
    private final int frameSize;

    @Getter
    private final long sourceVersion;

    @Getter
    private final long createdAt = System.currentTimeMillis();

    public InternalContext execute(
            Script script,
            Out out,
            Vars inputs,
            @Nullable BreakpointHandler handler
    ) {
        var variables = new VariableHeap(frameSize, indexers);
        inputs.sink(variables::set);

        var local = GenricHeap.local();
        var context = new InternalContext(script, variables, inputs, out, local, handler);
        context.visitNonFlow(this.statements);
        // assert context.indexer = 0
        return context;
    }

    public InternalContext execute(Script script, InternalContext context, Vars inputs) {
        var variables = new VariableHeap(frameSize, indexers);
        var newContext = new InternalContext(
                script,
                variables,
                inputs,
                context.out(),
                context.local(),
                context.breakpointHandler()
        );
        newContext.visitNonFlow(this.statements);
        // assert context.indexer = 0
        return newContext;
    }

}
