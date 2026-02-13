// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.Out;
import org.febit.wit.Template;
import org.febit.wit.Vars;
import org.febit.wit.runtime.BreakpointListener;
import org.febit.wit.runtime.FrameIndexer;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.heap.LocalHeap;
import org.febit.wit.runtime.heap.VariantHeap;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public final class TemplateAST {

    private final FrameIndexer[] indexers;
    private final Statement[] statements;
    private final int frameSize;

    @Getter
    private final long resourceVersion;
    @Getter
    private final long createdAt = System.currentTimeMillis();

    public InternalContext execute(
            Template template,
            Out out,
            Vars inputs,
            @Nullable BreakpointListener listener
    ) {
        var heap = new VariantHeap(frameSize, indexers);
        inputs.sink(heap::set);
        var local = LocalHeap.create();
        var context = new InternalContext(template, out, inputs, heap, local, listener);
        context.visit(this.statements);
        //assert context.indexer = 0
        return context;
    }

    public InternalContext execute(Template template, InternalContext context, Vars inputs) {
        var heap = new VariantHeap(frameSize, indexers);
        inputs.sink(heap::set);

        var newContext = context.createPeerContext(template, heap, inputs);
        newContext.visit(this.statements);
        //assert context.indexer = 0
        return newContext;
    }

}
