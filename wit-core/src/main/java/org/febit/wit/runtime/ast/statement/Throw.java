// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

public record Throw(
        Expression exception,
        Position position
) implements Statement {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var ex = this.exception.execute(context);
        if (ex instanceof RuntimeException runtime) {
            throw runtime;
        }
        if (ex instanceof Throwable throwable) {
            throw new ScriptEvaluateException(throwable);
        }
        throw new ScriptEvaluateException(String.valueOf(ex));
    }
}
