// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.lang.reflect.Constructor;
import webit.script.Context;
import webit.script.asm.AsmMethodCallerManager;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.method.AsmNativeMethodDeclare;
import webit.script.core.ast.method.NativeConstructorDeclare;
import webit.script.util.ExceptionUtil;

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

    public Object execute(final Context context) {

        if (context.enableAsmNative) {
            try {
                return new AsmNativeMethodDeclare(AsmMethodCallerManager.getCaller(constructor));
            } catch (Throwable ex) {
                throw ExceptionUtil.castToScriptRuntimeException(ex);
            }
        } else {
            return new NativeConstructorDeclare(constructor);
        }
    }
}
