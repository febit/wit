// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;

/**
 * @author zqq90
 */
public class TemplateStringExpression extends Expression {

    private final Expression[] exprs;

    public TemplateStringExpression(Expression[] exprs, int line, int column) {
        super(line, column);
        this.exprs = exprs;
    }

    @Override
    public Object execute(InternalContext context) {
        StringBuilder buf = new StringBuilder();
        for (Expression expr : exprs) {
            Object val = expr.execute(context);
            if (val != null && val != InternalContext.VOID) {
                // XXX: let template string append with OutResolver
                buf.append(val);
            }
        }
        return buf.toString();
    }
}
