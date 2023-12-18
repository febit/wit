// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.oper;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.ALU;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;
import org.febit.wit.lang.ast.Expression;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class MinusMinusAfter implements Expression {

    private final AssignableExpression expr;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        var assignable = this.expr;
        try {
            var value = assignable.execute(context);
            assignable.setValue(context, ALU.minusOne(value));
            return value;
        } catch (Exception e) {
            throw ScriptRuntimeException.from(e, this);
        }
    }
}
