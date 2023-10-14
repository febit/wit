// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import lombok.RequiredArgsConstructor;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class TemplateStringExpressionPart {

    private final Position position;
    private final List<Expression> exprs = new ArrayList<>();

    public TemplateStringExpressionPart add(final Expression expr) {
        if (expr instanceof DirectValue) {
            Object innerValue = ((DirectValue) expr).value;
            if (innerValue == null) {
                return this;
            }
            if (!(innerValue instanceof String)) {
                return add(new DirectValue(String.valueOf(innerValue), expr.getPosition()));
            }
        }
        exprs.add(expr);
        return this;
    }

    public TemplateStringExpression pop() {
        return new TemplateStringExpression(exprs.toArray(new Expression[0]), position);
    }
}
