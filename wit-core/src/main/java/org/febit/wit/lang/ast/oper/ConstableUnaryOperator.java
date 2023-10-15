// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.oper;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.expr.DirectValue;
import org.febit.wit.util.ExceptionUtil;

import java.util.function.Function;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class ConstableUnaryOperator implements Expression {

    protected final Expression expr;
    protected final Function<Object, Object> op;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        try {
            return op.apply(expr.execute(context));
        } catch (Exception e) {
            throw ExceptionUtil.toScriptRuntimeException(e, this);
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
    public Object getConstValue() {
        return op.apply(AstUtils.calcConst(expr));
    }
}
