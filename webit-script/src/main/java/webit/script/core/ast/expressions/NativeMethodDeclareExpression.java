package webit.script.core.ast.expressions;

import java.lang.reflect.Method;
import webit.script.Context;
import webit.script.asm.AsmMethodCaller;
import webit.script.asm.AsmMethodCallerManager;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.method.AsmNativeMethodDeclare;
import webit.script.core.ast.method.NativeMethodDeclare;
import webit.script.exceptions.ScriptRuntimeException;

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
        if (context.enableAsmNative) {
            AsmMethodCaller caller;
            try {
                caller = AsmMethodCallerManager.generateCaller(method);
            } catch (Exception ex) {
                ScriptRuntimeException scriptRuntimeException;
                if (ex instanceof ScriptRuntimeException) {
                    scriptRuntimeException = (ScriptRuntimeException) ex;
                } else {
                    scriptRuntimeException = new ScriptRuntimeException(ex);
                }
                throw scriptRuntimeException;
            }
            return new AsmNativeMethodDeclare(caller);
        } else {
            return new NativeMethodDeclare(method, argsCount, isStatic);
        }
    }
}
