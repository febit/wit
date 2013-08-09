package webit.script.core.ast.method;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import webit.script.Context;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public final class NativeMethodDeclare implements MethodDeclare {

    private final Method method;
    private final int argsCount;
    private final boolean isStatic;

    public NativeMethodDeclare(Method method, int argsCount, boolean isStatic) {
        this.method = method;
        this.argsCount = argsCount;
        this.isStatic = isStatic;
    }

    public Object execute(Context context, Object[] args) {
        Object obj;
        Object[] methodArgs;
        if (isStatic) {
            obj = null;
            if (args != null) {
                int copyLen = args.length;

                if (copyLen == argsCount) {
                    methodArgs = args;
                } else {
                    //TODO: Warning 参数个数不一致
                    if (copyLen > argsCount) {
                        copyLen = argsCount;
                    }
                    methodArgs = new Object[argsCount];
                    System.arraycopy(args, 0, methodArgs, 0, copyLen);
                }
            } else {
                methodArgs = new Object[argsCount];
            }
        } else {
            if (args == null || args.length < 1 || args[0] == null) {
                throw new ScriptRuntimeException("this method need one argument at least");
            }
            obj = args[0];
            int copyLen = args.length - 1;
            //TODO: Warning 参数个数不一致
            if (copyLen > argsCount) {
                copyLen = argsCount;
            }
            methodArgs = new Object[argsCount];
            System.arraycopy(args, 1, methodArgs, 0, copyLen);
        }
        try {
            return method.invoke(obj, methodArgs);
        } catch (IllegalAccessException ex) {
            throw new ScriptRuntimeException("this method is inaccessible", ex);
        } catch (IllegalArgumentException ex) {
            throw new ScriptRuntimeException("illegal argument", ex);
        } catch (InvocationTargetException ex) {
            throw new ScriptRuntimeException("this method throws an exception", ex);
        }
    }
}
