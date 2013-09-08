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

            ScriptRuntimeException old;
            if (e instanceof ScriptRuntimeException) {
                old = (ScriptRuntimeException) e;
            } else {
                old = new ScriptRuntimeException(e, function);
            }

            ScriptRuntimeException exception;
            if (template == context.template) {
                exception = old;
            } else {
                old.setTemplate(template);
                exception = new ScriptRuntimeException(e);
            }
            throw exception;
        }
    }
}
