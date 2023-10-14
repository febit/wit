// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import org.febit.wit.InternalContext;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.lang.UnConstableMethodDeclare;
import org.febit.wit.lang.VariantIndexer;
import org.febit.wit.lang.ast.expr.FunctionDeclare;
import org.febit.wit.util.ExceptionUtil;

/**
 * @author zqq90
 */
public final class FunctionMethodDeclare implements MethodDeclare, UnConstableMethodDeclare {

    private final FunctionDeclare function;
    private final InternalContext scopeContext;
    private final VariantIndexer[] indexers;
    private final int varSize;

    public FunctionMethodDeclare(FunctionDeclare function, InternalContext scopeContext,
                                 VariantIndexer[] indexers, int varSize) {
        this.function = function;
        this.scopeContext = scopeContext;
        this.indexers = indexers;
        this.varSize = varSize;
    }

    @Override
    public Object invoke(final InternalContext context, final Object[] args) {
        try {
            return function.invoke(this.scopeContext.createSubContext(this.indexers, context, this.varSize), args);
        } catch (Exception e) {
            var runtimeException = ExceptionUtil.toScriptRuntimeException(e, function);
            if (context != this.scopeContext) {
                throw runtimeException.setTemplate(this.scopeContext.getTemplate());
            }
            throw runtimeException;
        }
    }
}
