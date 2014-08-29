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
public final class PlusPlusBefore extends Expression {

    private final ResetableValueExpression expr;

    public PlusPlusBefore(ResetableValueExpression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    public Object execute(final Context context) {
        final ResetableValueExpression _expr;
        return StatementUtil.executeSetValue(_expr = this.expr, context, ALU.plusOne(
                StatementUtil.execute(_expr, context)));
    }
}
