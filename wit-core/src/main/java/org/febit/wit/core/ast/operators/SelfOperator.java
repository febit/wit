// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import java.util.function.BiFunction;
import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.AssignableExpression;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public class SelfOperator extends Expression {

    protected final AssignableExpression leftExpr;
    protected final Expression rightExpr;
    protected final BiFunction<Object, Object, Object> op;

    public SelfOperator(AssignableExpression leftExpr, Expression rightExpr, BiFunction<Object, Object, Object> op, int line, int column) {
        super(line, column);
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
        this.op = op;
    }

    @Override
    public final Object execute(final InternalContext context) {
        try {
            AssignableExpression left = this.leftExpr;
            // Must execute right expr first!
            Object rightResult = rightExpr.execute(context);
            return left.setValue(context,
                    op.apply(left.execute(context), rightResult)
            );
        } catch (Exception e) {
            throw StatementUtil.castToScriptRuntimeException(e, this);
        }
    }

}
