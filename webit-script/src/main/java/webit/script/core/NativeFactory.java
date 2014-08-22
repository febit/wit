// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.asm.AsmMethodAccessor;
import webit.script.asm.AsmMethodAccessorManager;
import webit.script.exceptions.ParseException;
import webit.script.lang.MethodDeclare;
import webit.script.lang.method.AsmNativeMethodDeclare;
import webit.script.lang.method.NativeConstructorDeclare;
import webit.script.lang.method.NativeMethodDeclare;
import webit.script.lang.method.NativeNewArrayDeclare;
import webit.script.loggers.Logger;
import webit.script.security.NativeSecurityManager;
import webit.script.util.ClassUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90
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

    public MethodDeclare createNativeNewArrayMethodDeclare(Class componentType, boolean checkAccess) {
        return createNativeNewArrayMethodDeclare(componentType, -1, -1, checkAccess);
    }

    public MethodDeclare createNativeNewArrayMethodDeclare(Class componentType, int line, int column) {
        return createNativeNewArrayMethodDeclare(componentType, line, column, true);
    }

    public MethodDeclare createNativeNewArrayMethodDeclare(Class componentType, int line, int column, boolean checkAccess) {
        Class classForCheck = componentType;
        while (classForCheck.isArray()) {
            classForCheck = classForCheck.getComponentType();
        }

        if (classForCheck == Void.class || classForCheck == Void.TYPE) {
            throw new ParseException("ComponentType must not Void.class", line, column);
        }

        final String path = classForCheck.getName().concat(".[]");
        if (checkAccess && !this.nativeSecurityManager.access(path)) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        return new NativeNewArrayDeclare(componentType);
    }

    public MethodDeclare createNativeMethodDeclare(Class clazz, String methodName, Class[] paramClasses) {
        return createNativeMethodDeclare(clazz, methodName, paramClasses, -1, -1);
    }

    public MethodDeclare createNativeMethodDeclare(Class clazz, String methodName, Class[] paramClasses, boolean checkAccess) {
        return createNativeMethodDeclare(clazz, methodName, paramClasses, -1, -1, checkAccess);
    }

    public MethodDeclare createNativeMethodDeclare(Class clazz, String methodName, Class[] paramClasses, int line, int column) {
        return createNativeMethodDeclare(clazz, methodName, paramClasses, line, column, true);
    }

    public MethodDeclare createNativeMethodDeclare(Class clazz, String methodName, Class[] paramClasses, int line, int column, boolean checkAccess) {

        final String path = StringUtil.concat(clazz.getName(), ".", methodName);
        if (checkAccess && !this.nativeSecurityManager.access(path)) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        try {
            final Method method = ClassUtil.searchMethod(clazz, methodName, paramClasses, false);
            AsmMethodAccessor accessor;
            if (this.enableAsm) {
                if (ClassUtil.isPublic(clazz)) {
                    if (ClassUtil.isPublic(method)) {
                        try {
                            accessor = AsmMethodAccessorManager.getAccessor(method);
                            if (accessor == null) {
                                logger.error("AsmMethodCaller for '{}' is null, and instead by NativeMethodDeclare", method);
                            }
                        } catch (Exception ex) {
                            accessor = null;
                            logger.error("Generate AsmMethodCaller for '{}' failed, and instead by NativeMethodDeclare:{}", method, ex);
                        }
                    } else {
                        logger.warn("'{}' will not use asm, since this method is not public, and instead by NativeMethodDeclare", method);
                        accessor = null;
                    }
                } else {
                    logger.warn("'{}' will not use asm, since class is not public, and instead by NativeMethodDeclare", method);
                    accessor = null;
                }
            } else {
                accessor = null;
            }

            return accessor != null
                    ? new AsmNativeMethodDeclare(accessor)
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

    public MethodDeclare createNativeConstructorDeclare(Class clazz, Class[] paramClasses, boolean checkAccess) {
        return createNativeConstructorDeclare(clazz, paramClasses, -1, -1, checkAccess);
    }

    public MethodDeclare createNativeConstructorDeclare(Class clazz, Class[] paramClasses, int line, int column) {
        return createNativeConstructorDeclare(clazz, paramClasses, line, column, true);
    }

    @SuppressWarnings("unchecked")
    public MethodDeclare createNativeConstructorDeclare(Class clazz, Class[] paramClasses, int line, int column, boolean checkAccess) {

        final String path = clazz.getName().concat(".<init>");
        if (checkAccess && !this.nativeSecurityManager.access(path)) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        try {
            final Constructor constructor = clazz.getConstructor(paramClasses);
            AsmMethodAccessor accessor;
            if (this.enableAsm) {
                if (ClassUtil.isPublic(clazz)) {
                    if (ClassUtil.isPublic(constructor)) {
                        try {
                            accessor = AsmMethodAccessorManager.getAccessor(constructor);
                            if (accessor == null) {
                                logger.error("AsmMethodCaller for '{}' is null, and instead by NativeConstructorDeclare", constructor);
                            }
                        } catch (Exception ex) {
                            accessor = null;
                            logger.error("Generate AsmMethodCaller for '{}' failed, and instead by NativeConstructorDeclare: {}", constructor, ex);
                        }
                    } else {
                        logger.warn("'{}' will not use asm, since this method is not public, and instead by NativeConstructorDeclare", constructor);
                        accessor = null;
                    }
                } else {
                    logger.warn("'{}' will not use asm, since class is not public, and instead by NativeConstructorDeclare", constructor);
                    accessor = null;
                }
            } else {
                accessor = null;
            }

            return accessor != null
                    ? new AsmNativeMethodDeclare(accessor)
                    : new NativeConstructorDeclare(constructor);

        } catch (NoSuchMethodException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        } catch (SecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        }
    }
}
