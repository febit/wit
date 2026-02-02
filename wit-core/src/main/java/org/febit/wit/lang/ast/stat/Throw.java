// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class Throw implements Statement {

    protected final Expression expr;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        Object exception = this.expr.execute(context);
        if (exception instanceof RuntimeException ex) {
            throw ex;
        }
        if (exception instanceof Exception ex) {
            throw new ScriptRuntimeException(ex);
        }
        throw new ScriptRuntimeException(String.valueOf(exception));
    }
}
