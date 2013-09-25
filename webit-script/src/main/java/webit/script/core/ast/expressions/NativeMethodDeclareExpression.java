// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.lang.reflect.Method;
import webit.script.Context;
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

    public NativeMethodDeclareExpression(Method method, int line, int column) {
        super(line, column);
        this.method = method;
    }

    public Object execute(final Context context) {
        if (context.enableAsmNative) {
            try {
                return new AsmNativeMethodDeclare(AsmMethodCallerManager.getCaller(method));
            } catch (Throwable ex) {
                ScriptRuntimeException scriptRuntimeException;
                if (ex instanceof ScriptRuntimeException) {
                    scriptRuntimeException = (ScriptRuntimeException) ex;
                } else {
                    scriptRuntimeException = new ScriptRuntimeException(ex);
                }
                throw scriptRuntimeException;
            }
        } else {
            return new NativeMethodDeclare(method);
        }
    }
}
