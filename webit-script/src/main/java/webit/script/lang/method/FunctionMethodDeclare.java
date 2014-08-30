// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang.method;

import webit.script.Context;
import webit.script.Template;
import webit.script.core.VariantIndexer;
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
    private final Object[] vars;
    private final VariantIndexer[] indexers;

    public FunctionMethodDeclare(FunctionDeclare function, Template template, Object[] vars, VariantIndexer[] indexers) {
        this.function = function;
        this.template = template;
        this.vars = vars;
        this.indexers = indexers;
    }

    public Object invoke(final Context context, final Object[] args) {
        if (context.vars == this.vars) {
            try {
                return function.invoke(context, args);
            } catch (Exception e) {
                throw ExceptionUtil.castToScriptRuntimeException(e, function);
            }
        } else {
            try {
                final Object[] bakVars = context.vars;
                final VariantIndexer[] bakIndexers = context.indexers;
                final Object result;
                context.vars = this.vars;
                context.indexers = this.indexers;
                result = function.invoke(context, args);
                context.vars = bakVars;
                context.indexers = bakIndexers;
                return result;
            } catch (Exception e) {
                throw new ScriptRuntimeException(ExceptionUtil.castToScriptRuntimeException(e, function).setTemplate(template));
            }
        }
    }
}
