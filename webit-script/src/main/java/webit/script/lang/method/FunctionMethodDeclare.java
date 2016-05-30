// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.lang.method;

import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.expressions.FunctionDeclare;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.MethodDeclare;
import webit.script.lang.UnConstableMethodDeclare;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class FunctionMethodDeclare implements MethodDeclare, UnConstableMethodDeclare {

    private final FunctionDeclare function;
    private final Context scopeContext;
    private final VariantIndexer[] indexers;
    private final int varSize;

    public FunctionMethodDeclare(FunctionDeclare function, Context scopeContext, VariantIndexer[] indexers, int varSize) {
        this.function = function;
        this.scopeContext = scopeContext;
        this.indexers = indexers;
        this.varSize = varSize;
    }

    @Override
    public Object invoke(final Context context, final Object[] args) {
        try {
            return function.invoke(this.scopeContext.createSubContext(this.indexers, context, this.varSize), args);
        } catch (Exception e) {
            ScriptRuntimeException runtimeException = StatementUtil.castToScriptRuntimeException(e, function);
            if (context != this.scopeContext) {
                runtimeException.setTemplate(this.scopeContext.template);
            }
            throw runtimeException;
        }
    }
}
