// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;

@Accessors(fluent = true)
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
            return new ScriptEvaluateException("Cannot get value from static field: " + field, ex, this);
        }
    }

    @Override
    @Nullable
    public Object set(InternalContext context, @Nullable Object value) {
        try {
            field.set(null, value);
            return value;
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            return new ScriptEvaluateException("Cannot set value to static field: " + field, ex, this);
        }
    }
}
