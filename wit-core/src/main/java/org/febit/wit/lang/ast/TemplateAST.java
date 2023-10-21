// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.Template;
import org.febit.wit.Vars;
import org.febit.wit.io.Out;
import org.febit.wit.lang.BreakpointListener;
import org.febit.wit.lang.VariantIndexer;

/**
 * @author zqq90
 */
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
        rootParams.exportTo(context::setVar);
        context.visit(this.statements);
        //assert context.indexer = 0
        return context;
    }

    public InternalContext execute(Template template, final InternalContext context, Vars rootParams) {
        var newContext = context.createPeerContext(template, indexers, varSize, rootParams);
        rootParams.exportTo(newContext::setVar);
        newContext.visit(this.statements);
        //assert context.indexer = 0
        return newContext;
    }

}
