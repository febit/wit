// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.expressions.DirectValue;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class BitNot extends AbstractExpression implements Optimizable {

    private final Expression expr;

    public BitNot(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    public Object execute(final Context context) {
        return ALU.bitNot(StatementUtil.execute(expr, context));
    }

    public Expression optimize() {
        return expr instanceof DirectValue
                ? new DirectValue(ALU.bitNot(((DirectValue) expr).value), line, column)
                : this;
    }
}
