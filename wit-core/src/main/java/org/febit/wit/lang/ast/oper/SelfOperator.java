// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.oper;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.util.ExceptionUtil;

import java.util.function.BiFunction;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class SelfOperator implements Expression {

    protected final AssignableExpression leftExpr;
    protected final Expression rightExpr;
    protected final BiFunction<Object, Object, Object> op;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public final Object execute(final InternalContext context) {
        try {
            var assignable = this.leftExpr;
            // Must execute right expr first!
            var rightResult = rightExpr.execute(context);
            return assignable.setValue(context,
                    op.apply(assignable.execute(context), rightResult)
            );
        } catch (Exception e) {
            throw ExceptionUtil.toScriptRuntimeException(e, this);
        }
    }

}
