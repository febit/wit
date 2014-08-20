// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang.method;

import webit.script.Context;
import webit.script.Template;
import webit.script.core.VariantContext;
import webit.script.core.ast.expressions.FunctionDeclare;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.MethodDeclare;
import webit.script.util.ExceptionUtil;

/**
 *
 * @author Zqq
 */
public final class FunctionMethodDeclare implements MethodDeclare {

    private final FunctionDeclare function;
    private final Template template;
    private final VariantContext[] parentVarContexts;
    private final boolean containsRootContext;

    public FunctionMethodDeclare(FunctionDeclare function, Template template, VariantContext[] parentVarContexts, boolean containsRootContext) {
        this.function = function;
        this.template = template;
        this.parentVarContexts = parentVarContexts;
        this.containsRootContext = containsRootContext;
    }

    public Object invoke(final Context context, final Object[] args) {
        try {
            return function.invoke(new Context(context, template, parentVarContexts, containsRootContext), args);
        } catch (Exception e) {
            ScriptRuntimeException scriptRuntimeException = ExceptionUtil.castToScriptRuntimeException(e, function);
            throw template == context.template
                    ? scriptRuntimeException
                    : new ScriptRuntimeException(scriptRuntimeException.setTemplate(template));
        }
    }
}
