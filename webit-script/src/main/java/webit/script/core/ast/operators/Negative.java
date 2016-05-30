// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.expressions.DirectValue;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class Negative extends Expression implements Optimizable {

    private final Expression expr;

    public Negative(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object execute(final Context context) {
        try {
            return ALU.negative(expr.execute(context));
        } catch (Exception e) {
            throw StatementUtil.castToScriptRuntimeException(e, this);
        }
    }

    @Override
    public Expression optimize() {
        return expr instanceof DirectValue
                ? new DirectValue(ALU.negative(((DirectValue) expr).value), line, column)
                : this;
    }
}
