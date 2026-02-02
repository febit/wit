// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.oper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.expr.DirectValue;
import org.jspecify.annotations.Nullable;

import java.util.function.UnaryOperator;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class ConstableUnaryOperator implements Expression {

    private final Expression expr;
    private final UnaryOperator<@Nullable Object> op;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        try {
            return op.apply(expr.execute(context));
        } catch (Exception e) {
            throw ScriptRuntimeException.from(e, this);
        }
    }

    @Override
    public Expression optimize() {
        if (AstUtils.isImmutableDirectValue(expr)) {
            return new DirectValue(op.apply(((DirectValue) expr).value), position);
        }
        return this;
    }

    @Override
    @Nullable
    public Object calcAsConst() {
        return op.apply(AstUtils.calcConst(expr));
    }
}
