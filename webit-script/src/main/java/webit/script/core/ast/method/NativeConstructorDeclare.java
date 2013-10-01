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

    public Object execute(final Context context, final Object[] args) {

        final Object[] methodArgs;
        if (args != null) {
            int copyLen;

            if ((copyLen = args.length) == argsCount) {
                methodArgs = args;
            } else {
                //TODO: Warning 参数个数不一致
                if (copyLen > argsCount) {
                    copyLen = argsCount;
                }
                System.arraycopy(args, 0, methodArgs = new Object[argsCount], 0, copyLen);
            }
        } else {
            methodArgs = new Object[argsCount];
        }
        try {
            return constructor.newInstance(methodArgs);
        } catch (InstantiationException ex) {
            throw new ScriptRuntimeException("this a abstract class, can't create new instance: "+ ex.getLocalizedMessage());
        } catch (IllegalAccessException ex) {
            throw new ScriptRuntimeException("this method is inaccessible: "+ ex.getLocalizedMessage());
        } catch (IllegalArgumentException ex) {
            throw new ScriptRuntimeException("illegal argument: "+ ex.getLocalizedMessage());
        } catch (InvocationTargetException ex) {
            throw new ScriptRuntimeException("this method throws an exception", ex);
        }
    }
}
