// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.function;

import org.febit.wit.Vars;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.FrameIndexer;
import org.febit.wit.runtime.ast.expr.FunctionDeclareExpr;
import org.jspecify.annotations.Nullable;

public final class FunctionFunctionDeclare implements FunctionDeclare, UnConstableFunctionDeclare {

    private final FunctionDeclareExpr declareExpr;
    private final InternalContext declarerContext;
    private final FrameIndexer[] indexers;
    private final int frameSize;

    public FunctionFunctionDeclare(
            FunctionDeclareExpr declareExpr,
            InternalContext declarerContext,
            FrameIndexer[] indexers,
            int frameSize
    ) {
        this.declareExpr = declareExpr;
        this.declarerContext = declarerContext;
        this.indexers = indexers;
        this.frameSize = frameSize;
    }

    @Nullable
    @Override
    public Object apply(InternalContext context, @Nullable Object @Nullable [] args) {
        var declaredAt = this.declarerContext;
        try {
            var subVariables = declaredAt.variables().shift(this.frameSize, this.indexers);
            var sub = new InternalContext(
                    declaredAt.script(),
                    subVariables,
                    Vars.empty(),
                    context.out(),
                    context.local(),
                    declaredAt.breakpointHandler()
            );
            return declareExpr.apply(sub, args);
        } catch (Exception e) {
            var runtimeException = ScriptEvaluateException.from(e, declareExpr);
            if (context != declaredAt) {
                throw runtimeException.setScript(declaredAt.script());
            }
            throw runtimeException;
        }
    }
}
