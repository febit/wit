package webit.script.core.ast.expressions;

import java.lang.reflect.Method;
import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.method.NativeMethodDeclare;

/**
 *
 * @author Zqq
 */
public class NativeMethodDeclareExpression extends AbstractExpression {

    private final Method method;
    private final int argsCount;
    private final boolean isStatic;

    public NativeMethodDeclareExpression(Method method, int argsCount, boolean isStatic, int line, int column) {
        super(line, column);
        this.method = method;
        this.argsCount = argsCount;
        this.isStatic = isStatic;
    }

    @Override
    public Object execute(Context context, boolean needReturn) {
        return new NativeMethodDeclare(method, argsCount, isStatic);
    }
}
