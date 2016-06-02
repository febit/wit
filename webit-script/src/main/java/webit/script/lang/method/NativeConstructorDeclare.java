// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.lang.method;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import webit.script.InternalContext;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.MethodDeclare;

/**
 *
 * @author zqq90
 */
public class NativeConstructorDeclare implements MethodDeclare {

    private final Constructor constructor;
    private final int argsCount;

    public NativeConstructorDeclare(Constructor constructor) {
        this.constructor = constructor;
        this.argsCount = constructor.getParameterTypes().length;
    }

    @Override
    public Object invoke(final InternalContext context, final Object[] args) {

        final Object[] methodArgs;
        final int argsLen;
        final int myArgsCount = this.argsCount;
        if (args != null && (argsLen = args.length) != 0) {
            if (argsLen == myArgsCount) {
                methodArgs = args;
            } else {
                //Note: Warning 参数个数不一致
                System.arraycopy(args, 0, methodArgs = new Object[myArgsCount], 0, argsLen <= myArgsCount ? argsLen : myArgsCount);
            }
        } else {
            methodArgs = new Object[myArgsCount];
        }
        try {
            return constructor.newInstance(methodArgs);
        } catch (InstantiationException ex) {
            throw new ScriptRuntimeException("Can't create new instance: ".concat(ex.getLocalizedMessage()));
        } catch (IllegalAccessException ex) {
            throw new ScriptRuntimeException("Unaccessible method: ".concat(ex.getLocalizedMessage()));
        } catch (IllegalArgumentException ex) {
            throw new ScriptRuntimeException("Illegal arguments: ".concat(ex.getLocalizedMessage()));
        } catch (InvocationTargetException ex) {
            throw new ScriptRuntimeException("this method throws an exception", ex.getTargetException());
        }
    }
}
