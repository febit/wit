// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import java.util.Objects;
import java.util.function.BiFunction;
import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Constable;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Optimizable;
import org.febit.wit.core.ast.expressions.DirectValue;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public class ConstableBiOperator extends BiOperator implements Optimizable, Constable {

    protected final BiFunction<Object, Object, Object> op;

    public ConstableBiOperator(Expression leftExpr, Expression rightExpr, BiFunction<Object, Object, Object> op, int line, int column) {
        super(leftExpr, rightExpr, line, column);
        Objects.requireNonNull(op);
        this.op = op;
    }

    @Override
    public Object execute(final InternalContext context) {
        try {
            return op.apply(leftExpr.execute(context), rightExpr.execute(context));
        } catch (Exception e) {
            throw StatementUtil.castToScriptRuntimeException(e, this);
        }
    }

    @Override
    public Expression optimize() {
        if (StatementUtil.isImmutableDirectValue(leftExpr)
                && StatementUtil.isImmutableDirectValue(rightExpr)) {
            return new DirectValue(
                    op.apply(((DirectValue) leftExpr).value, ((DirectValue) rightExpr).value),
                    line, column);
        }
        return this;
    }

    @Override
    public Object getConstValue() {
        return op.apply(
                StatementUtil.calcConst(leftExpr, true),
                StatementUtil.calcConst(rightExpr, true)
        );
    }
}
