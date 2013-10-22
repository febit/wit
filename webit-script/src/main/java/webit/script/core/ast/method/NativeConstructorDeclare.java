// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.method;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import webit.script.Context;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class NativeConstructorDeclare implements MethodDeclare {

    private final Constructor constructor;
    private final int argsCount;

    public NativeConstructorDeclare(Constructor constructor) {
        this.constructor = constructor;
        this.argsCount = constructor.getParameterTypes().length;
    }

    public Object invoke(final Context context, final Object[] args) {

        final Object[] methodArgs;
        final int argsLen;
        if (args != null && (argsLen = args.length) != 0) {
            if (argsLen == argsCount) {
                methodArgs = args;
            } else {
                //XXX: Warning 参数个数不一致
                System.arraycopy(args, 0, methodArgs = new Object[argsCount], 0, argsLen <= argsCount ? argsLen : argsCount);
            }
        } else {
            methodArgs = new Object[argsCount];
        }
        try {
            return constructor.newInstance(methodArgs);
        } catch (InstantiationException ex) {
            throw new ScriptRuntimeException("this a abstract class, can't create new instance: ".concat(ex.getLocalizedMessage()));
        } catch (IllegalAccessException ex) {
            throw new ScriptRuntimeException("this method is inaccessible: ".concat(ex.getLocalizedMessage()));
        } catch (IllegalArgumentException ex) {
            throw new ScriptRuntimeException("illegal argument: ".concat(ex.getLocalizedMessage()));
        } catch (InvocationTargetException ex) {
            throw new ScriptRuntimeException("this method throws an exception", ex.getTargetException());
        }
    }
}
