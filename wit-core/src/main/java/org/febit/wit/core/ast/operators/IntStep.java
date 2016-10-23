// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.operators;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.BinaryOperator;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.iter.IntAscIter;
import org.febit.wit.lang.iter.IntDescIter;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class IntStep extends BinaryOperator {

    public IntStep(Expression leftExp, Expression rightExp, int line, int column) {
        super(leftExp, rightExp, line, column);
    }

    @Override
    public Object execute(final InternalContext context) {
        Object result;
        final int num1;
        if ((result = leftExpr.execute(context)) instanceof Number) {
            num1 = ((Number) result).intValue();
        } else {
            throw new ScriptRuntimeException(StringUtil.concatObjectClass("left need a int, but found ", result), this);
        }
        final int num2;
        if ((result = rightExpr.execute(context)) instanceof Number) {
            num2 = ((Number) result).intValue();
        } else {
            throw new ScriptRuntimeException(StringUtil.concatObjectClass("right need a int, but found ", result), this);
        }
        if (num1 < num2) {
            return new IntAscIter(num1, num2);
        } else {
            return new IntDescIter(num1, num2);
        }
    }
}
