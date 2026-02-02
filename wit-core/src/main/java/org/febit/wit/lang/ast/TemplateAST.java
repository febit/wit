// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.Template;
import org.febit.wit.Vars;
import org.febit.wit.lang.BreakpointListener;
import org.febit.wit.lang.Out;
import org.febit.wit.lang.VariantIndexer;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public final class TemplateAST {

    private final VariantIndexer[] indexers;
    private final Statement[] statements;
    private final int varSize;

    @Getter
    private final long resourceVersion;
    @Getter
    private final long createdAt = System.currentTimeMillis();

    public InternalContext execute(
            Template template,
            Out out,
            Vars rootParams,
            @Nullable BreakpointListener listener
    ) {
        var context = new InternalContext(template, out, rootParams, indexers, varSize, null, listener);
        rootParams.sink(context::setVar);
        context.visit(this.statements);
        //assert context.indexer = 0
        return context;
    }

    public InternalContext execute(Template template, InternalContext context, Vars rootParams) {
        var newContext = context.createPeerContext(template, indexers, varSize, rootParams);
        rootParams.sink(newContext::setVar);
        newContext.visit(this.statements);
        //assert context.indexer = 0
        return newContext;
    }

}
