package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author zhuqingqing_iwm
 */
public class Throw extends Statement {

    protected final Expression expr;

    public Throw(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object execute(Context context) {
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
