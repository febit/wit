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

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class IndexOperator implements AssignableExpression {

    private final Expression leftExpr;
    private final Expression rightExpr;

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        try {
            return context.getBeanProperty(leftExpr.execute(context), rightExpr.execute(context));
        } catch (Exception e) {
            throw ExceptionUtil.toScriptRuntimeException(e, this);
        }
    }

    @Override
    @Nullable
    public Object setValue(final InternalContext context, @Nullable final Object value) {
        try {
            context.setBeanProperty(
                    leftExpr.execute(context),
                    rightExpr.execute(context),
                    value);
            return value;
        } catch (Exception e) {
            throw ExceptionUtil.toScriptRuntimeException(e, this);
        }
    }
}
