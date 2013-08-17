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

    public Object execute(Context context, Object[] args) {
        Context functionContext = new Context(context, template, parentVarContexts);
        try {
            return function.execute(functionContext, args);
        } catch (Exception e) {

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
