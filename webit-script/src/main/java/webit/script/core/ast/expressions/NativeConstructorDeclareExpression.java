package webit.script.core.ast.expressions;

import java.lang.reflect.Constructor;
import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.method.NativeConstructorDeclare;

/**
 *
 * @author Zqq
 */
public class NativeConstructorDeclareExpression extends AbstractExpression {

    private final Constructor constructor;
    private final int argsCount;

    public NativeConstructorDeclareExpression(Constructor constructor, int argsCount, int line, int column) {
        super(line, column);
        this.constructor = constructor;
        this.argsCount = argsCount;
    }

    @Override
    public Object execute(Context context, boolean needReturn) {
        return new NativeConstructorDeclare(constructor, argsCount);
    }
}
