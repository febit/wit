// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class PlusPlusAfter extends AbstractExpression {

    private final ResetableValueExpression expr;

    public PlusPlusAfter(ResetableValueExpression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    public Object execute(final Context context) {
        final Object value;
        final ResetableValueExpression _expr;
        StatementUtil.executeSetValue(_expr = this.expr, context,ALU.plusOne(
                 value = StatementUtil.execute(_expr, context)));
        return value;
    }
}
