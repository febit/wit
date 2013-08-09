package webit.script.core.ast.expressions;

import java.util.ArrayList;
import java.util.List;
import webit.script.core.ast.Expression;
import webit.script.core.ast.StatmentPart;

/**
 *
 * @author Zqq
 */
public final class ArrayValuePart extends StatmentPart{
    
    protected final List<Expression> valueExprs;

    public ArrayValuePart(int line, int column) {
        super(line, column);
        this.valueExprs = new ArrayList();
    }
    
    public ArrayValuePart append(Expression expr){
        valueExprs.add(expr);
        return this;
    }

    @Override
    public ArrayValue pop() {
        Expression[] exprs = new Expression[valueExprs.size()];
        valueExprs.toArray(exprs);
        return new ArrayValue(exprs, line, column);
    }
}
