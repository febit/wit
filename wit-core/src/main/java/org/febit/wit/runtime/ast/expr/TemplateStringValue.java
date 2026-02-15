// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.ast.Expression;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class TemplateStringValue implements Expression {

    private final Expression[] exprs;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var buf = new StringBuilder();
        for (var expr : exprs) {
            var piece = expr.execute(context);
            if (piece != null && piece != Undefined.UNDEFINED) {
                buf.append(piece);
            }
        }
        return buf.toString();
    }
}
