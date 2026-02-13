// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.method;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.runtime.FrameIndexer;
import org.febit.wit.runtime.FunctionDeclare;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.UnConstableFunctionDeclare;
import org.febit.wit.runtime.ast.expr.FunctionDeclareExpr;
import org.jspecify.annotations.Nullable;

public final class FunctionFunctionDeclare implements FunctionDeclare, UnConstableFunctionDeclare {

    private final FunctionDeclareExpr declareExpr;
    private final InternalContext upstreamContext;
    private final FrameIndexer[] indexers;
    private final int frameSize;

    public FunctionFunctionDeclare(
            FunctionDeclareExpr declareExpr,
            InternalContext upstreamContext,
            FrameIndexer[] indexers,
            int frameSize
    ) {
        this.declareExpr = declareExpr;
        this.upstreamContext = upstreamContext;
        this.indexers = indexers;
        this.frameSize = frameSize;
    }

    @Nullable
    @Override
    public Object apply(InternalContext context, @Nullable Object @Nullable [] args) {
        try {
            var sub = this.upstreamContext.createSubContext(context, this.indexers, this.frameSize);
            return declareExpr.apply(sub, args);
        } catch (Exception e) {
            var runtimeException = ScriptRuntimeException.from(e, declareExpr);
            if (context != this.upstreamContext) {
                throw runtimeException.setTemplate(this.upstreamContext.template());
            }
            throw runtimeException;
        }
    }
}
