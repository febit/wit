// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.lang.method.MixedMultiNativeMethodDeclare;
import org.febit.wit.lang.method.MultiNativeConstructorDeclare;
import org.febit.wit.lang.method.MultiNativeMethodDeclare;
import org.febit.wit.lang.method.NativeConstructorDeclare;
import org.febit.wit.lang.method.NativeMethodDeclare;
import org.febit.wit.lang.method.NativeNewArrayDeclare;
import org.febit.wit.loggers.Logger;
import org.febit.wit.security.NativeSecurityManager;
import org.febit.wit.util.ClassUtil;

/**
 *
 * @author zqq90
 */
public class NativeFactory {

    protected final ConcurrentMap<Object, MethodDeclare> methodCaching = new ConcurrentHashMap<>();

    protected Logger logger;
    protected NativeSecurityManager nativeSecurityManager;

    public MethodDeclare getNativeNewArrayMethodDeclare(Class componentType) {
        return getNativeNewArrayMethodDeclare(componentType, -1, -1, true);
    }

    public MethodDeclare getNativeNewArrayMethodDeclare(Class componentType, boolean checkAccess) {
        return getNativeNewArrayMethodDeclare(componentType, -1, -1, checkAccess);
    }

    public MethodDeclare getNativeNewArrayMethodDeclare(Class componentType, int line, int column, boolean checkAccess) {
        Class classForCheck = componentType;
        while (classForCheck.isArray()) {
            classForCheck = classForCheck.getComponentType();
        }
        if (ClassUtil.isVoidType(classForCheck)) {
            throw new ParseException("ComponentType must not void", line, column);
        }
        if (checkAccess) {
            final String path = classForCheck.getName().concat(".[]");
            if (!this.nativeSecurityManager.access(path)) {
                throw createNotAccessablePathException(path, line, column);
            }
        }
        return new NativeNewArrayDeclare(componentType);
    }

    public MethodDeclare getNativeMethodDeclare(Class clazz, String methodName) {
        return getNativeMethodDeclare(clazz, methodName, -1, -1, true);
    }

    public MethodDeclare getNativeMethodDeclare(Class clazz, String methodName, boolean checkAccess) {
        return getNativeMethodDeclare(clazz, methodName, -1, -1, checkAccess);
    }

    public MethodDeclare getNativeMethodDeclare(Class clazz, String methodName, Class[] paramTypes) {
        return getNativeMethodDeclare(clazz, methodName, paramTypes, -1, -1, true);
    }

    public MethodDeclare getNativeMethodDeclare(Class clazz, String methodName, Class[] paramTypes, boolean checkAccess) {
        return getNativeMethodDeclare(clazz, methodName, paramTypes, -1, -1, checkAccess);
    }

    @SuppressWarnings("unchecked")
    public MethodDeclare getNativeMethodDeclare(Class clazz, String methodName, Class[] paramTypes, int line, int column, boolean checkAccess) {
        if (checkAccess) {
            final String path = clazz.getName() + '.' + methodName;
            if (!this.nativeSecurityManager.access(path)) {
                throw createNotAccessablePathException(path, line, column);
            }
        }
        try {
            return getNativeMethodDeclare(clazz.getMethod(methodName, paramTypes));
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new ParseException(ex.getMessage(), ex, line, column);
        }
    }

    public MethodDeclare getNativeMethodDeclare(Class clazz, String methodName, int line, int column, boolean checkAccess) {
        if (checkAccess) {
            final String path = clazz.getName() + '.' + methodName;
            if (!this.nativeSecurityManager.access(path)) {
                throw createNotAccessablePathException(path, line, column);
            }
        }
        return createNativeMethodDeclare(clazz, methodName);
    }

    public MethodDeclare getNativeConstructorDeclare(Class clazz) {
        return getNativeConstructorDeclare(clazz, -1, -1, true);
    }

    public MethodDeclare getNativeConstructorDeclare(Class clazz, boolean checkAccess) {
        return getNativeConstructorDeclare(clazz, -1, -1, checkAccess);
    }

    public MethodDeclare getNativeConstructorDeclare(Class clazz, Class[] paramTypes) {
        return getNativeConstructorDeclare(clazz, paramTypes, -1, -1, true);
    }

    public MethodDeclare getNativeConstructorDeclare(Class clazz, Class[] paramTypes, boolean checkAccess) {
        return getNativeConstructorDeclare(clazz, paramTypes, -1, -1, checkAccess);
    }

    @SuppressWarnings("unchecked")
    public MethodDeclare getNativeConstructorDeclare(Class clazz, Class[] paramTypes, int line, int column, boolean checkAccess) {
        if (checkAccess) {
            final String path = clazz.getName().concat(".<init>");
            if (!this.nativeSecurityManager.access(path)) {
                throw createNotAccessablePathException(path, line, column);
            }
        }
        try {
            return getNativeConstructorDeclare(clazz.getConstructor(paramTypes));
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new ParseException(ex.getMessage(), ex, line, column);
        }
    }

    public MethodDeclare getNativeConstructorDeclare(Class clazz, int line, int column, boolean checkAccess) {
        if (checkAccess) {
            final String path = clazz.getName().concat(".<init>");
            if (!this.nativeSecurityManager.access(path)) {
                throw createNotAccessablePathException(path, line, column);
            }
        }
        return createNativeConstructorDeclare(clazz);
    }

    public MethodDeclare getNativeMethodDeclare(Method method) {
        MethodDeclare declare = methodCaching.get(method);
        if (declare == null) {
            declare = createNativeMethodDeclare(method);
            MethodDeclare old = methodCaching.putIfAbsent(method, declare);
            if (old != null) {
                return old;
            }
        }
        return declare;
    }

    public MethodDeclare getNativeConstructorDeclare(Constructor constructor) {
        MethodDeclare declare = methodCaching.get(constructor);
        if (declare == null) {
            declare = createNativeConstructorDeclare(constructor);
            MethodDeclare old = methodCaching.putIfAbsent(constructor, declare);
            if (old != null) {
                return old;
            }
        }
        return declare;
    }

    public MethodDeclare createNativeConstructorDeclare(Class clazz) {
        Constructor[] constructors = clazz.getConstructors();
        if (constructors == null || constructors.length == 0) {
            throw new ScriptRuntimeException("Not found public constructor for class： " + clazz.getName());
        }
        if (constructors.length == 1) {
            return new NativeConstructorDeclare(constructors[0]);
        }
        ClassUtil.setAccessible(constructors);
        return new MultiNativeConstructorDeclare(constructors);
    }

    public MethodDeclare createNativeMethodDeclare(Class clazz, String methodName) {
        Method[] methods = ClassUtil.getPublicMethods(clazz, methodName);
        if (methods.length == 0) {
            throw new ScriptRuntimeException("Method not found： " + clazz.getName() + '#' + methodName);
        }
        if (methods.length == 1) {
            return createNativeMethodDeclare(methods[0]);
        }
        return createMultiNativeMethodDeclare(methods);
    }

    public MethodDeclare createNativeMethodDeclare(Method method) {
        ClassUtil.setAccessible(method);
        return new NativeMethodDeclare(method);
    }

    public MethodDeclare createMultiNativeMethodDeclare(Method[] methods) {
        if (methods == null || methods.length == 0) {
            throw new IllegalArgumentException("methods must mot empty");
        }
        ClassUtil.setAccessible(methods);
        final boolean isStatic = ClassUtil.isStatic(methods[0]);
        boolean mix = false;
        for (int i = 1; i < methods.length; i++) {
            if (isStatic != ClassUtil.isStatic(methods[0])) {
                mix = true;
                break;
            }
        }
        return mix ? new MixedMultiNativeMethodDeclare(methods)
                : new MultiNativeMethodDeclare(methods, isStatic);
    }

    protected MethodDeclare createNativeConstructorDeclare(Constructor constructor) {
        ClassUtil.setAccessible(constructor);
        return new NativeConstructorDeclare(constructor);
    }

    protected static ParseException createNotAccessablePathException(String path, int line, int column) {
        return new ParseException("Not accessable of native path: ".concat(path), line, column);
    }
}
