package webit.script.core.ast.expressions;

import java.util.ArrayList;
import java.util.List;
import webit.script.core.ast.Expression;
import webit.script.core.ast.StatmentPart;

/**
 *
 * @author Zqq
 */
public final class FunctionExecuteExpressionPart extends StatmentPart {

    private Expression funcExpr;
    private List<Expression> paramExprList;

    public FunctionExecuteExpressionPart() {
        paramExprList = new ArrayList<Expression>();
    }

    @Override
    public FunctionExecuteExpressionPart setPosition(int line, int column) {
        super.setPosition(line, column);
        return this;
    }

    public FunctionExecuteExpressionPart setFuncExpr(Expression funcExpr) {
        this.funcExpr = funcExpr;
        return this;
    }

    public FunctionExecuteExpressionPart append(Expression paramExpr) {
        this.paramExprList.add(paramExpr);
        return this;
    }

    @Override
    public FunctionExecuteExpression pop() {

        Expression[] paramExprs = new Expression[paramExprList.size()];
        paramExprList.toArray(paramExprs);

        return new FunctionExecuteExpression(funcExpr, paramExprs, line, column);
    }
}
