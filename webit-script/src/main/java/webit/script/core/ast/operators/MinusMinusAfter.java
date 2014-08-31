// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class MinusMinusAfter extends Expression {

    private final ResetableValueExpression expr;

    public MinusMinusAfter(ResetableValueExpression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    public Object execute(final Context context) {
        final Object value;
        final ResetableValueExpression _expr;
        try {
            (_expr = this.expr).setValue(context, ALU.minusOne(
                    value = _expr.execute(context)));
            return value;
        } catch (Exception e) {
            throw StatementUtil.castToScriptRuntimeException(e, this);
        }
    }
}
