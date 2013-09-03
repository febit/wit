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

    public Object execute(Context context, boolean needReturn) {
        Object object1 = StatmentUtil.execute(leftExpr, context);
        int num1;
        if (object1 instanceof Number) {
            num1 = ((Number) object1).intValue();
        } else {
            throw new ScriptRuntimeException("left need a int, but found " + (object1 != null ? object1.getClass().getName() : "[null]"));
        }
        Object object2 = StatmentUtil.execute(rightExpr, context);
        int num2;
        if (object2 instanceof Number) {
            num2 = ((Number) object2).intValue();
        } else {
            throw new ScriptRuntimeException("left need a int, but found " + (object2 != null ? object2.getClass().getName() : "[null]"));
        }
        if (num1 < num2) {
            return new IntegerAscStepIter(num1, num2);
        } else {
            return new IntegerDescStepIter(num1, num2);
        }
    }
}
