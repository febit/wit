// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.InternalContext;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.exceptions.ScriptRuntimeException;

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
