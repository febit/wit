// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.AssignableExpression;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.util.ALU;
import org.febit.wit.util.ExceptionUtil;

/**
 * @author zqq90
 */
public final class PlusPlusBefore extends Expression {

    private final AssignableExpression expr;

    public PlusPlusBefore(AssignableExpression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object execute(final InternalContext context) {
        try {
            final AssignableExpression resetable = this.expr;
            return resetable.setValue(context, ALU.plusOne(
                    resetable.execute(context)));
        } catch (Exception e) {
            throw ExceptionUtil.toScriptRuntimeException(e, this);
        }
    }
}
