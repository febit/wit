package org.febit.wit.core.ast.expressions;

import java.util.ArrayList;
import java.util.List;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class TemplateStringExpressionPart {

    private final int line;
    private final int column;

    private final List<Expression> exprs = new ArrayList<>();
    private int minLength = 0;

    public TemplateStringExpressionPart(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public TemplateStringExpressionPart add(Expression expr) {
        if (expr instanceof DirectValue) {
            if (((DirectValue) expr).value == null) {
                return this;
            }
            if (!(((DirectValue) expr).value instanceof String)) {
                return add(new DirectValue(String.valueOf(((DirectValue) expr).value), expr.line, expr.column));
            }
            String value = (String) ((DirectValue) expr).value;
            if (value == null) {
                return this;
            }
            minLength += value.length();
        }
        exprs.add(expr);
        return this;
    }

    public TemplateStringExpression pop() {
        return new TemplateStringExpression(exprs.toArray(new Expression[exprs.size()]), line, column);
    }
}
