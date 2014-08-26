// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang.method;

import webit.script.Context;
import webit.script.Template;
import webit.script.core.Variants;
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
    private final Variants[] parentVarses;
    private final boolean withRootVars;

    public FunctionMethodDeclare(FunctionDeclare function, Template template, Variants[] parentVarses, boolean withRootVars) {
        this.function = function;
        this.template = template;
        this.parentVarses = parentVarses;
        this.withRootVars = withRootVars;
    }

    public Object invoke(final Context context, final Object[] args) {
        try {
            return function.invoke(new Context(context, template, parentVarses, withRootVars), args);
        } catch (Exception e) {
            ScriptRuntimeException exception = ExceptionUtil.castToScriptRuntimeException(e, function);
            throw template == context.template
                    ? exception
                    : new ScriptRuntimeException(exception.setTemplate(template));
        }
    }
}
