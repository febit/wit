// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.asm.AsmMethodCaller;
import webit.script.asm.AsmMethodCallerManager;
import webit.script.exceptions.ParseException;
import webit.script.loggers.Logger;
import webit.script.method.MethodDeclare;
import webit.script.method.impl.AsmNativeMethodDeclare;
import webit.script.method.impl.NativeConstructorDeclare;
import webit.script.method.impl.NativeMethodDeclare;
import webit.script.method.impl.NativeNewArrayDeclare;
import webit.script.security.NativeSecurityManager;
import webit.script.util.ClassUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public class NativeFactory implements Initable {

    private boolean enableAsm = true;

    private Logger logger;
    private NativeSecurityManager nativeSecurityManager;

    public void init(Engine engine) {
        this.logger = engine.getLogger();
        this.nativeSecurityManager = engine.getNativeSecurityManager();
    }

    public void setEnableAsm(boolean enableAsm) {
        this.enableAsm = enableAsm;
    }

    public MethodDeclare createNativeNewArrayMethodDeclare(Class componentType) {
        return createNativeNewArrayMethodDeclare(componentType, -1, -1);
    }
    
    public MethodDeclare createNativeNewArrayMethodDeclare(Class componentType, int line, int column) {
        Class classWaitCheck = componentType;
        while (classWaitCheck.isArray()) {
            classWaitCheck = classWaitCheck.getComponentType();
        }

        if (classWaitCheck == Void.class || classWaitCheck == Void.TYPE) {
            throw new ParseException("ComponentType must not Void.class", line, column);
        }

        final String path;
        if (this.nativeSecurityManager.access(path = (classWaitCheck.getName().concat(".[]"))) == false) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        return new NativeNewArrayDeclare(componentType);
    }
    
    public MethodDeclare createNativeMethodDeclare(Class clazz, String methodName, Class[] paramClasses) {
        return createNativeMethodDeclare(clazz, methodName, paramClasses, -1, -1);
    }

    public MethodDeclare createNativeMethodDeclare(Class clazz, String methodName, Class[] paramClasses, int line, int column) {

        final String path;
        if (this.nativeSecurityManager.access(path = (StringUtil.concat(clazz.getName(), ".", methodName))) == false) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        try {
            final Method method = ClassUtil.searchMethod(clazz, methodName, paramClasses, false);
            AsmMethodCaller caller;
            if (this.enableAsm) {
                if (ClassUtil.isPublic(clazz)) {
                    if (ClassUtil.isPublic(method)) {
                        try {
                            if ((caller = AsmMethodCallerManager.getCaller(method)) == null) {
                                logger.error(StringUtil.concat("AsmMethodCaller for '", method.toString(), "' is null, and instead by NativeMethodDeclare"));
                            }
                        } catch (Exception ex) {
                            caller = null;
                            logger.error(StringUtil.concat("Generate AsmMethodCaller for '", method.toString(), "' failed, and instead by NativeMethodDeclare"), ex);
                        }
                    } else {
                        logger.warn(StringUtil.concat("'", method.toString(), "' will not use asm, since this method is not public, and instead by NativeMethodDeclare"));
                        caller = null;
                    }
                } else {
                    logger.warn(StringUtil.concat("'", method.toString(), "' will not use asm, since class is not public, and instead by NativeMethodDeclare"));
                    caller = null;
                }
            } else {
                caller = null;
            }

            return caller != null
                    ? new AsmNativeMethodDeclare(caller)
                    : new NativeMethodDeclare(method);

        } catch (NoSuchMethodException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        } catch (SecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        }
    }

    public MethodDeclare createNativeConstructorDeclare(Class clazz, Class[] paramClasses) {
        return createNativeConstructorDeclare(clazz, paramClasses, -1, -1);
    }

    @SuppressWarnings("unchecked")
    public MethodDeclare createNativeConstructorDeclare(Class clazz, Class[] paramClasses, int line, int column) {

        final String path;
        if (this.nativeSecurityManager.access(path = (clazz.getName() + ".<init>")) == false) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        try {
            final Constructor constructor = clazz.getConstructor(paramClasses);
            AsmMethodCaller caller;
            if (this.enableAsm) {
                if (ClassUtil.isPublic(clazz)) {
                    if (ClassUtil.isPublic(constructor)) {
                        try {
                            if ((caller = AsmMethodCallerManager.getCaller(constructor)) == null) {
                                logger.error(StringUtil.concat("AsmMethodCaller for '", constructor.toString(), "' is null, and instead by NativeConstructorDeclare"));
                            }
                        } catch (Exception ex) {
                            caller = null;
                            logger.error(StringUtil.concat("Generate AsmMethodCaller for '", constructor.toString(), "' failed, and instead by NativeConstructorDeclare"), ex);
                        }
                    } else {
                        logger.warn(StringUtil.concat("'", constructor.toString(), "' will not use asm, since this method is not public, and instead by NativeConstructorDeclare"));
                        caller = null;
                    }
                } else {
                    logger.warn(StringUtil.concat("'" + constructor.toString() + "' will not use asm, since class is not public, and instead by NativeConstructorDeclare"));
                    caller = null;
                }
            } else {
                caller = null;
            }

            return caller != null
                    ? new AsmNativeMethodDeclare(caller)
                    : new NativeConstructorDeclare(constructor);

        } catch (NoSuchMethodException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        } catch (SecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        }
    }
}
