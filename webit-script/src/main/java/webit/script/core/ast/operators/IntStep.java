// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.Context;
import webit.script.core.ast.BinaryOperator;
import webit.script.core.ast.Expression;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.StatementUtil;
import webit.script.util.StringUtil;
import webit.script.util.collection.IntegerAscStepIter;
import webit.script.util.collection.IntegerDescStepIter;

/**
 *
 * @author Zqq
 */
public class IntStep extends BinaryOperator {

    public IntStep(Expression leftExp, Expression rightExp, int line, int column) {
        super(leftExp, rightExp, line, column);
    }

    public Object execute(final Context context) {
        Object result;
        final int num1;
        if ((result = StatementUtil.execute(leftExpr, context)) instanceof Number) {
            num1 = ((Number) result).intValue();
        } else {
            throw new ScriptRuntimeException(StringUtil.concatObjectClass("left need a int, but found ", result));
        }
        final int num2;
        if ((result = StatementUtil.execute(rightExpr, context)) instanceof Number) {
            num2 = ((Number) result).intValue();
        } else {
            throw new ScriptRuntimeException(StringUtil.concatObjectClass("right need a int, but found ", result));
        }
        if (num1 < num2) {
            return new IntegerAscStepIter(num1, num2);
        } else {
            return new IntegerDescStepIter(num1, num2);
        }
    }
}
