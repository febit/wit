// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.method;

import webit.script.Context;
import webit.script.Template;
import webit.script.core.runtime.variant.VariantContext;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public final class FunctionMethodDeclare implements MethodDeclare {

    private final Function function;
    private final Template template;
    private final VariantContext[] parentVarContexts;

    public FunctionMethodDeclare(Function function, Template template, VariantContext[] parentVarContexts) {
        this.function = function;
        this.template = template;
        this.parentVarContexts = parentVarContexts;
    }

    public Object execute(final Context context, final Object[] args) {
        try {
            return function.execute(new Context(context, template, parentVarContexts), args);
        } catch (Throwable e) {

            ScriptRuntimeException runtimeException = e instanceof ScriptRuntimeException
                    ? (ScriptRuntimeException) e
                    : new ScriptRuntimeException(e, function);

            if (template == context.template) {
                throw runtimeException;
            } else {
                runtimeException.setTemplate(template);
                throw new ScriptRuntimeException(e);
            }
        }
    }
}
