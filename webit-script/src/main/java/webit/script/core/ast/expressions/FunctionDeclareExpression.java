// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.method.Function;
import webit.script.core.ast.method.FunctionMethodDeclare;
import webit.script.core.runtime.variant.VariantContext;

/**
 *
 * @author Zqq
 */
public final class FunctionDeclareExpression extends AbstractExpression {

    private final Function function;

    public FunctionDeclareExpression(Function function) {
        super(function.getLine(), function.getColumn());
        this.function = function;
    }

    public Object execute(final Context context) {
        final VariantContext[] variantContexts;
        final int[] overflowUpstairs;
        if ((overflowUpstairs = function.overflowUpstairs) != null) {
            final int len;
            final int max;
            variantContexts = new VariantContext[(max = overflowUpstairs[(len = overflowUpstairs.length) - 1]) + 1];
            for (int i = 0, j; i < len; i++) {
                variantContexts[max - (j = overflowUpstairs[i])] = context.vars.getContext(j);
            }
        } else {
            variantContexts = null;
        }
        return new FunctionMethodDeclare(function, context.template, variantContexts);
    }
}
