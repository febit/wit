// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.Context;
import webit.script.core.ast.BinaryOperator;
import webit.script.core.ast.Expression;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.iter.IntAscIter;
import webit.script.lang.iter.IntDescIter;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class IntStep extends BinaryOperator {

    public IntStep(Expression leftExp, Expression rightExp, int line, int column) {
        super(leftExp, rightExp, line, column);
    }

    @Override
    public Object execute(final Context context) {
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
