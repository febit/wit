// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.oper;

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
public final class PlusPlusBefore implements Expression {

    private final AssignableExpression expr;

    @Getter
    private final Position position;

    @Override
    public Object execute(final InternalContext context) {
        try {
            var assignable = this.expr;
            return assignable.setValue(context, ALU.plusOne(
                    assignable.execute(context))
            );
        } catch (Exception e) {
            throw ScriptRuntimeException.from(e, this);
        }
    }
}
