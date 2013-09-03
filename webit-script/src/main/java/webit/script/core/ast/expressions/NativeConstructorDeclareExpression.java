// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.lang.reflect.Constructor;
import webit.script.Context;
import webit.script.asm.AsmMethodCaller;
import webit.script.asm.AsmMethodCallerManager;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.method.AsmNativeMethodDeclare;
import webit.script.core.ast.method.NativeConstructorDeclare;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class NativeConstructorDeclareExpression extends AbstractExpression {

    private final Constructor constructor;

    public NativeConstructorDeclareExpression(Constructor constructor, int line, int column) {
        super(line, column);
        this.constructor = constructor;
    }

    @Override
    public Object execute(Context context, boolean needReturn) {

        if (context.enableAsmNative) {
            AsmMethodCaller caller;
            try {
                caller = AsmMethodCallerManager.generateCaller(constructor);
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
            return new NativeConstructorDeclare(constructor);
        }
    }
}
