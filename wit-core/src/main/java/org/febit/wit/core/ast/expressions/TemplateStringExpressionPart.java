package org.febit.wit.core.ast.expressions;

import java.util.ArrayList;
import java.util.List;
import org.febit.wit.core.ast.Expression;

/**
 *
 * @author zqq90
 */
public class TemplateStringExpressionPart {

    private final int line;
    private final int column;

    private final List<Expression> exprs = new ArrayList<>();

    public TemplateStringExpressionPart(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public TemplateStringExpressionPart add(final Expression expr) {
        if (expr instanceof DirectValue) {
            if (((DirectValue) expr).value == null) {
                return this;
            }
            if (!(((DirectValue) expr).value instanceof String)) {
                return add(new DirectValue(String.valueOf(((DirectValue) expr).value), expr.line, expr.column));
            }
        }
        exprs.add(expr);
        return this;
    }

    public TemplateStringExpression pop() {
        return new TemplateStringExpression(exprs.toArray(new Expression[exprs.size()]), line, column);
    }
}
