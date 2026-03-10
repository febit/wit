// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.function;

import org.febit.wit.Vars;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.WitFunction;
import org.febit.wit.runtime.ast.ScopedIndexer;
import org.febit.wit.runtime.ast.expr.FunctionDeclarer;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record ScriptFunction(
        FunctionDeclarer declarer,
        InternalContext declarerContext,
        int heapSize,
        List<ScopedIndexer> indexers
) implements WitFunction {

    @Nullable
    @Override
    public Object apply(InternalContext context, @Nullable Object @Nullable [] args) {
        var declaredAt = this.declarerContext;
        try {
            var subVariables = declaredAt.variables().shift(this.heapSize, this.indexers);
            var sub = new InternalContext(
                    declaredAt.script(),
                    subVariables,
                    Vars.empty(),
                    context.out(),
                    context.local(),
                    declaredAt.breakpointHandler()
            );
            return declarer.apply(sub, args);
        } catch (Exception e) {
            var runtimeException = ScriptEvaluateException.from(e, declarer);
            if (context != declaredAt) {
                throw runtimeException.setScript(declaredAt.script());
            }
            throw runtimeException;
        }
    }
}
