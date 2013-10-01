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

    public FunctionDeclareExpression(Function function, int line, int column) {
        super(line, column);
        this.function = function;
    }

    public Object execute(final Context context) {

        final VariantContext[] variantContexts;
        final int range;
        if ((range = function.overflowUpstairsRange) >= 0) {
            final int[] overflowUpstairs = function.overflowUpstairs;
            variantContexts = new VariantContext[range + 1];
            for (int i = 0, j, len = overflowUpstairs.length; i < len; i++) {
                j = overflowUpstairs[i];
                variantContexts[range - j] = context.vars.getContext(j);
            }
        } else {
            variantContexts = null;
        }

        return new FunctionMethodDeclare(function, context.template, variantContexts);
    }
}
