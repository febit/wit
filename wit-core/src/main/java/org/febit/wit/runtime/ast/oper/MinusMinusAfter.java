// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class MinusMinusAfter implements Expression {

    private final AssignableExpression expr;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
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
