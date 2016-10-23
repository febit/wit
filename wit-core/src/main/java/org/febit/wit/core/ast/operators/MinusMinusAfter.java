// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.ResetableValueExpression;
import org.febit.wit.util.ALU;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class MinusMinusAfter extends Expression {

    private final ResetableValueExpression expr;

    public MinusMinusAfter(ResetableValueExpression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object execute(final InternalContext context) {
        final Object value;
        final ResetableValueExpression resetable = this.expr;
        try {
            resetable.setValue(context, ALU.minusOne(
                    value = resetable.execute(context)));
            return value;
        } catch (Exception e) {
            throw StatementUtil.castToScriptRuntimeException(e, this);
        }
    }
}
