// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.Out;
import org.febit.wit.Script;
import org.febit.wit.Vars;
import org.febit.wit.runtime.BreakpointHandler;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.heap.GenricHeap;
import org.febit.wit.runtime.heap.VariantHeap;
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
        var heap = new VariantHeap(frameSize, indexers);
        inputs.sink(heap::set);
        var local = GenricHeap.local();
        var context = new InternalContext(script, out, inputs, heap, local, handler);
        context.visit(this.statements);
        //assert context.indexer = 0
        return context;
    }

    public InternalContext execute(Script script, InternalContext context, Vars inputs) {
        var heap = new VariantHeap(frameSize, indexers);
        inputs.sink(heap::set);

        var newContext = context.createPeerContext(script, heap, inputs);
        newContext.visit(this.statements);
        //assert context.indexer = 0
        return newContext;
    }

}
