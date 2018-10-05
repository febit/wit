// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import org.febit.wit.core.ast.Expression;

import java.util.ArrayList;
import java.util.List;

/**
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
            Object innerValue = ((DirectValue) expr).value;
            if (innerValue == null) {
                return this;
            }
            if (!(innerValue instanceof String)) {
                return add(new DirectValue(String.valueOf(innerValue), expr.line, expr.column));
            }
        }
        exprs.add(expr);
        return this;
    }

    public TemplateStringExpression pop() {
        return new TemplateStringExpression(exprs.toArray(new Expression[exprs.size()]), line, column);
    }
}
