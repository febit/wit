// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.lang.UnConstableFunctionDeclare;
import org.febit.wit.lang.VariantIndexer;
import org.febit.wit.lang.ast.expr.FunctionDeclareExpr;
import org.jspecify.annotations.Nullable;

public final class FunctionFunctionDeclare implements FunctionDeclare, UnConstableFunctionDeclare {

    private final FunctionDeclareExpr function;
    private final InternalContext scopeContext;
    private final VariantIndexer[] indexers;
    private final int varSize;

    public FunctionFunctionDeclare(
            FunctionDeclareExpr function, InternalContext scopeContext,
            VariantIndexer[] indexers, int varSize
    ) {
        this.function = function;
        this.scopeContext = scopeContext;
        this.indexers = indexers;
        this.varSize = varSize;
    }

    @Nullable
    @Override
    public Object invoke(InternalContext context, @Nullable Object @Nullable [] args) {
        try {
            var sub = this.scopeContext.createSubContext(this.indexers, context, this.varSize);
            return function.invoke(sub, args);
        } catch (Exception e) {
            var runtimeException = ScriptRuntimeException.from(e, function);
            if (context != this.scopeContext) {
                throw runtimeException.setTemplate(this.scopeContext.template());
            }
            throw runtimeException;
        }
    }
}
