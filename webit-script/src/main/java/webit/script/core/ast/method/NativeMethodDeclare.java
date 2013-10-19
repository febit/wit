// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.method;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import webit.script.Context;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.ClassUtil;

/**
 *
 * @author Zqq
 */
public final class NativeMethodDeclare implements MethodDeclare {

    private final Method method;
    private final int argsCount;
    private final boolean isStatic;
    private final boolean noVoid;

    public NativeMethodDeclare(Method method) {
        this.method = method;
        this.argsCount = method.getParameterTypes().length;
        this.isStatic = ClassUtil.isStatic(method);
        Class returnType;
        this.noVoid = ((returnType = method.getReturnType()) != void.class) && (returnType != Void.class);
    }

    public Object execute(final Context context, final Object[] args) {
        final Object obj;
        final Object[] methodArgs;
        if (isStatic) {
            obj = null;
            if (args != null) {
                int argsLen;
                if ((argsLen = args.length) == argsCount) {
                    methodArgs = args;
                } else {
                    //XXX: Warning 参数个数不一致
                    System.arraycopy(args, 0, methodArgs = new Object[argsCount], 0, argsLen <= argsCount ? argsLen : argsCount);
                }
            } else {
                methodArgs = new Object[argsCount];
            }
        } else {
            if (args != null && args.length != 0 && args[0] != null) {
                obj = args[0];
                int copyLen;
                //XXX: Warning 参数个数不一致
                System.arraycopy(args, 1, methodArgs = new Object[argsCount], 0, ((copyLen = args.length - 1) <= argsCount) ? copyLen : argsCount);
            } else {
                throw new ScriptRuntimeException("this method need one argument at least");
            }
        }
        try {
            return noVoid ? method.invoke(obj, methodArgs) : Context.VOID;
        } catch (IllegalAccessException ex) {
            throw new ScriptRuntimeException("this method is inaccessible: ".concat(ex.getLocalizedMessage()));
        } catch (IllegalArgumentException ex) {
            throw new ScriptRuntimeException("illegal argument: ".concat(ex.getLocalizedMessage()));
        } catch (InvocationTargetException ex) {
            throw new ScriptRuntimeException("this method throws an exception", ex.getTargetException());
        }
    }
}
