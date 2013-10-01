// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.BinaryOperator;
import webit.script.core.ast.Expression;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.StatmentUtil;
import webit.script.util.collection.IntegerAscStepIter;
import webit.script.util.collection.IntegerDescStepIter;

/**
 *
 * @author Zqq
 */
public class IntStepOperator extends BinaryOperator {

    public IntStepOperator(Expression leftExp, Expression rightExp, int line, int column) {
        super(leftExp, rightExp, line, column);
    }

    public Object execute(final Context context) {
        Object result;
        final int num1;
        if ((result = StatmentUtil.execute(leftExpr, context)) instanceof Number) {
            num1 = ((Number) result).intValue();
        } else {
            throw new ScriptRuntimeException("left need a int, but found " + (result != null ? result.getClass().getName() : "[null]"));
        }
        final int num2;
        if ((result = StatmentUtil.execute(rightExpr, context)) instanceof Number) {
            num2 = ((Number) result).intValue();
        } else {
            throw new ScriptRuntimeException("right need a int, but found " + (result != null ? result.getClass().getName() : "[null]"));
        }
        if (num1 < num2) {
            return new IntegerAscStepIter(num1, num2);
        } else {
            return new IntegerDescStepIter(num1, num2);
        }
    }
}
