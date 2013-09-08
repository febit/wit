// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.ResetableValue;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class PlusPlusOperator extends AbstractExpression {

    private final ResetableValueExpression expr;
    private final boolean executeAtFirst;

    public PlusPlusOperator(ResetableValueExpression expr, boolean executeAtFirst, int line, int column) {
        super(line, column);
        this.expr = expr;
        this.executeAtFirst = executeAtFirst;
    }

    public Object execute(final Context context, final boolean needReturn) {
        final ResetableValue resetableValue = StatmentUtil.getResetableValue(expr, context);
        final Object value = resetableValue.get();

        final Object result = ALU.plusOne(value);
        resetableValue.set(result);
        return executeAtFirst ? result : value;
    }
}
