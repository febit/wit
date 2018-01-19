// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.util.ClassUtil;
import org.febit.wit.util.CollectionUtil;

/**
 *
 * @author zqq90
 */
public class IntStep extends BiOperator {

    public IntStep(Expression leftExp, Expression rightExp, int line, int column) {
        super(leftExp, rightExp, line, column);
    }

    @Override
    public Object execute(final InternalContext context) {
        final int num1;
        Object tempResult;
        tempResult = leftExpr.execute(context);
        if (tempResult instanceof Number) {
            num1 = ((Number) tempResult).intValue();
        } else {
            throw new ScriptRuntimeException("left need a int, but found ".concat(ClassUtil.getClassName(tempResult)), this);
        }
        final int num2;
        tempResult = rightExpr.execute(context);
        if (tempResult instanceof Number) {
            num2 = ((Number) tempResult).intValue();
        } else {
            throw new ScriptRuntimeException("right need a int, but found ".concat(ClassUtil.getClassName(tempResult)), this);
        }
        if (num1 < num2) {
            return CollectionUtil.createIntAscIter(num1, num2);
        } else {
            return CollectionUtil.createIntDescIter(num1, num2);
        }
    }
}
