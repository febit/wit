// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.exceptions.ParseException;
import webit.script.lang.MethodDeclare;
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

    protected Logger logger;
    protected NativeSecurityManager nativeSecurityManager;

    public void init(Engine engine) {
        this.logger = engine.getLogger();
        this.nativeSecurityManager = engine.getNativeSecurityManager();
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
            Method method = ClassUtil.searchMethod(clazz, methodName, paramClasses, false);
            return createNativeMethodDeclare(clazz, method, line, column);
        } catch (NoSuchMethodException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        } catch (SecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        }
    }

    public MethodDeclare createNativeMethodDeclare(Class clazz, Method method, int line, int column) {
        return new NativeMethodDeclare(method);
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
            Constructor constructor = clazz.getConstructor(paramClasses);
            return createNativeConstructorDeclare(clazz, constructor, line, column);
        } catch (NoSuchMethodException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        } catch (SecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        }
    }

    public MethodDeclare createNativeConstructorDeclare(Class clazz, Constructor constructor, int line, int column) {
        return new NativeConstructorDeclare(constructor);
    }
}
