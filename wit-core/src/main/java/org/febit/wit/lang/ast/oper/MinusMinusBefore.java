// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.oper;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.util.ALU;
import org.febit.wit.util.ExceptionUtil;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class MinusMinusBefore implements Expression {

    private final AssignableExpression expr;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        try {
            final AssignableExpression assignable = this.expr;
            return assignable.setValue(context, ALU.minusOne(
                    assignable.execute(context)));
        } catch (Exception e) {
            throw ExceptionUtil.toScriptRuntimeException(e, this);
        }
    }
}
