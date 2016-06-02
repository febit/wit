// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.InternalContext;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.expressions.DirectValue;
import webit.script.util.ALU;

/**
 *
 * @author zqq90
 */
public final class Not extends Expression implements Optimizable {

    private final Expression expr;

    public Not(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object execute(final InternalContext context) {
        return !ALU.isTrue(expr.execute(context));
    }

    @Override
    public Expression optimize() {
        return expr instanceof DirectValue
                ? new DirectValue(ALU.not(((DirectValue) expr).value), line, column)
                : this;
    }
}
