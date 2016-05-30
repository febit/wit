// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.lang.method;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import webit.script.Context;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.MethodDeclare;
import webit.script.util.ClassUtil;

/**
 *
 * @author zqq90
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

    @Override
    public Object invoke(final Context context, final Object[] args) {
        final Object obj;
        final Object[] methodArgs;
        final int myArgsCount = this.argsCount;
        if (isStatic) {
            obj = null;
            if (args != null) {
                int argsLen;
                if ((argsLen = args.length) == myArgsCount) {
                    methodArgs = args;
                } else {
                    //Note: Warning 参数个数不一致
                    System.arraycopy(args, 0, methodArgs = new Object[myArgsCount], 0, argsLen <= myArgsCount ? argsLen : myArgsCount);
                }
            } else {
                methodArgs = new Object[myArgsCount];
            }
        } else {
            if (args != null && args.length != 0 && args[0] != null) {
                obj = args[0];
                int copyLen;
                //Note: Warning 参数个数不一致
                System.arraycopy(args, 1, methodArgs = new Object[myArgsCount], 0, ((copyLen = args.length - 1) <= myArgsCount) ? copyLen : myArgsCount);
            } else {
                throw new ScriptRuntimeException("this method need one argument at least");
            }
        }
        try {
            Object result = method.invoke(obj, methodArgs);
            return noVoid ? result : Context.VOID;
        } catch (IllegalAccessException ex) {
            throw new ScriptRuntimeException("this method is inaccessible: ".concat(ex.getLocalizedMessage()));
        } catch (IllegalArgumentException ex) {
            throw new ScriptRuntimeException("illegal argument: ".concat(ex.getLocalizedMessage()));
        } catch (InvocationTargetException ex) {
            throw new ScriptRuntimeException("this method throws an exception", ex.getTargetException());
        }
    }
}
