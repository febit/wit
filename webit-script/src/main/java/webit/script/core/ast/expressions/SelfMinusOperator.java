
package webit.script.core.ast.expressions;

import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.core.ast.SelfOperator;
import webit.script.util.ALU;

/**
 *
 * @author Zqq
 */
public final class SelfMinusOperator extends SelfOperator{

    public SelfMinusOperator(ResetableValueExpression leftExp, Expression rightExp, int line, int column) {
        super(leftExp, rightExp, line, column);
    }

    @Override
    protected Object doOperate(Object left, Object right) {
        return ALU.minus(left, right);
    }
}
