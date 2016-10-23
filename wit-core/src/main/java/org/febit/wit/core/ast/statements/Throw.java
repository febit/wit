// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.exceptions.ScriptRuntimeException;

/**
 *
 * @author zqq90
 */
public class Throw extends Statement {

    protected final Expression expr;

    public Throw(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object execute(InternalContext context) {
        Object exception = this.expr.execute(context);
        if (exception instanceof RuntimeException) {
            throw (RuntimeException) exception;
        }
        if (exception instanceof Exception) {
            throw new ScriptRuntimeException((Exception) exception);
        }
        throw new ScriptRuntimeException(String.valueOf(exception));
    }
}
