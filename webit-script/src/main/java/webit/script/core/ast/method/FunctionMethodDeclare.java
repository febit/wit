// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.method;

import webit.script.Context;
import webit.script.Template;
import webit.script.core.ast.expressions.FunctionDeclare;
import webit.script.core.runtime.VariantContext;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.ExceptionUtil;

/**
 *
 * @author Zqq
 */
public final class FunctionMethodDeclare implements MethodDeclare {

    private final FunctionDeclare function;
    private final Template template;
    private final VariantContext[] parentVarContexts;

    public FunctionMethodDeclare(FunctionDeclare function, Template template, VariantContext[] parentVarContexts) {
        this.function = function;
        this.template = template;
        this.parentVarContexts = parentVarContexts;
    }

    public Object invoke(final Context context, final Object[] args) {
        try {
            return function.invoke(new Context(context, template, parentVarContexts), args);
        } catch (Throwable e) {
            throw template == context.template
                    ? ExceptionUtil.castToScriptRuntimeException(e, function)
                    : new ScriptRuntimeException(ExceptionUtil.castToScriptRuntimeException(e, function).setTemplate(template));
        }
    }
}
