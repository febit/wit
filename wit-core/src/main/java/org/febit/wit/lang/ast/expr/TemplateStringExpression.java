// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class TemplateStringExpression implements Expression {

    private final Expression[] exprs;
    @Getter
    private final Position position;

    @Override
    public Object execute(InternalContext context) {
        var buf = new StringBuilder();
        for (var expr : exprs) {
            var piece = expr.execute(context);
            if (piece != null && piece != InternalContext.VOID) {
                // TODO: let template string append with OutResolver
                buf.append(piece);
            }
        }
        return buf.toString();
    }
}
