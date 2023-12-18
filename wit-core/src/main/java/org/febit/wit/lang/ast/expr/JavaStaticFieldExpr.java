// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;

import java.lang.reflect.Field;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class JavaStaticFieldExpr implements AssignableExpression {

    private final Field field;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        try {
            return field.get(null);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            return new ScriptRuntimeException("Cannot get value from static field: " + field, ex, this);
        }
    }

    @Override
    @Nullable
    public Object setValue(InternalContext context, @Nullable Object value) {
        try {
            field.set(null, value);
            return value;
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            return new ScriptRuntimeException("Cannot set value to static field: " + field, ex, this);
        }
    }
}
