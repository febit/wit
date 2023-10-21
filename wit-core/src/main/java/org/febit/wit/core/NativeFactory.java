// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core;

import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.TextPosition;
import org.febit.wit.lang.method.MixedMultiNativeFunctionDeclare;
import org.febit.wit.lang.method.MultiNativeConstructorDeclare;
import org.febit.wit.lang.method.MultiNativeFunctionDeclare;
import org.febit.wit.lang.method.NativeConstructorDeclare;
import org.febit.wit.lang.method.NativeFunctionDeclare;
import org.febit.wit.lang.method.NativeNewArrayDeclare;
import org.febit.wit.loggers.Logger;
import org.febit.wit.security.NativeSecurityManager;
import org.febit.wit.util.ClassUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zqq90
 */
public class NativeFactory {

    protected final ConcurrentMap<Object, FunctionDeclare> methodCaching = new ConcurrentHashMap<>();

    protected Logger logger;
    protected NativeSecurityManager nativeSecurityManager;

    public FunctionDeclare getNativeNewArrayMethodDeclare(Class<?> componentType) {
        return getNativeNewArrayMethodDeclare(componentType, TextPosition.UNKNOWN, true);
    }

    public FunctionDeclare getNativeNewArrayMethodDeclare(Class<?> componentType, boolean checkAccess) {
        return getNativeNewArrayMethodDeclare(componentType, TextPosition.UNKNOWN, checkAccess);
    }

    public FunctionDeclare getNativeNewArrayMethodDeclare(Class<?> componentType, Position pos, boolean checkAccess) {
        Class<?> classForCheck = componentType;
        while (classForCheck.isArray()) {
            classForCheck = classForCheck.getComponentType();
        }
        if (ClassUtil.isVoidType(classForCheck)) {
            throw new ParseException("ComponentType must not void", pos);
        }
        if (checkAccess) {
            final String path = classForCheck.getName().concat(".[]");
            if (!this.nativeSecurityManager.access(path)) {
                throw createNotAccessiblePathException(path, pos);
            }
        }
        return new NativeNewArrayDeclare(componentType);
    }

    public FunctionDeclare getNativeMethodDeclare(Class<?> clazz, String methodName) {
        return getNativeMethodDeclare(clazz, methodName, TextPosition.UNKNOWN, true);
    }

    public FunctionDeclare getNativeMethodDeclare(Class<?> clazz, String methodName, boolean checkAccess) {
        return getNativeMethodDeclare(clazz, methodName, TextPosition.UNKNOWN, checkAccess);
    }

    public FunctionDeclare getNativeMethodDeclare(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        return getNativeMethodDeclare(clazz, methodName, paramTypes, TextPosition.UNKNOWN, true);
    }

    public FunctionDeclare getNativeMethodDeclare(Class<?> clazz, String methodName,
                                                  Class<?>[] paramTypes, boolean checkAccess) {
        return getNativeMethodDeclare(clazz, methodName, paramTypes, TextPosition.UNKNOWN, checkAccess);
    }

    public FunctionDeclare getNativeMethodDeclare(Class<?> clazz, String methodName, Class<?>[] paramTypes,
                                                  Position position, boolean checkAccess) {
        if (checkAccess) {
            final String path = clazz.getName() + '.' + methodName;
            if (!this.nativeSecurityManager.access(path)) {
                throw createNotAccessiblePathException(path, position);
            }
        }
        try {
            return getNativeMethodDeclare(clazz.getMethod(methodName, paramTypes));
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new ParseException(ex.getMessage(), ex, position);
        }
    }

    public FunctionDeclare getNativeMethodDeclare(Class<?> clazz, String methodName, Position pos, boolean checkAccess) {
        if (checkAccess) {
            final String path = clazz.getName() + '.' + methodName;
            if (!this.nativeSecurityManager.access(path)) {
                throw createNotAccessiblePathException(path, pos);
            }
        }
        return createNativeMethodDeclare(clazz, methodName);
    }

    public FunctionDeclare getNativeConstructorDeclare(Class<?> clazz) {
        return getNativeConstructorDeclare(clazz, TextPosition.UNKNOWN, true);
    }

    public FunctionDeclare getNativeConstructorDeclare(Class<?> clazz, boolean checkAccess) {
        return getNativeConstructorDeclare(clazz, TextPosition.UNKNOWN, checkAccess);
    }

    public FunctionDeclare getNativeConstructorDeclare(Class<?> clazz, Class<?>[] paramTypes) {
        return getNativeConstructorDeclare(clazz, paramTypes, TextPosition.UNKNOWN, true);
    }

    public FunctionDeclare getNativeConstructorDeclare(Class<?> clazz, Class<?>[] paramTypes, boolean checkAccess) {
        return getNativeConstructorDeclare(clazz, paramTypes, TextPosition.UNKNOWN, checkAccess);
    }

    public FunctionDeclare getNativeConstructorDeclare(Class<?> clazz, Class<?>[] paramTypes,
                                                       Position position, boolean checkAccess) {
        if (checkAccess) {
            final String path = clazz.getName().concat(".<init>");
            if (!this.nativeSecurityManager.access(path)) {
                throw createNotAccessiblePathException(path, position);
            }
        }
        try {
            return getNativeConstructorDeclare(clazz.getConstructor(paramTypes));
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new ParseException(ex.getMessage(), ex, position);
        }
    }

    public FunctionDeclare getNativeConstructorDeclare(Class<?> clazz, Position pos, boolean checkAccess) {
        if (checkAccess) {
            final String path = clazz.getName().concat(".<init>");
            if (!this.nativeSecurityManager.access(path)) {
                throw createNotAccessiblePathException(path, pos);
            }
        }
        return createNativeConstructorDeclare(clazz);
    }

    public FunctionDeclare getNativeMethodDeclare(Method method) {
        FunctionDeclare declare = methodCaching.get(method);
        if (declare == null) {
            declare = createNativeMethodDeclare(method);
            FunctionDeclare old = methodCaching.putIfAbsent(method, declare);
            if (old != null) {
                return old;
            }
        }
        return declare;
    }

    public FunctionDeclare getNativeConstructorDeclare(Constructor constructor) {
        FunctionDeclare declare = methodCaching.get(constructor);
        if (declare == null) {
            declare = createNativeConstructorDeclare(constructor);
            FunctionDeclare old = methodCaching.putIfAbsent(constructor, declare);
            if (old != null) {
                return old;
            }
        }
        return declare;
    }

    public FunctionDeclare createNativeConstructorDeclare(Class<?> clazz) {
        Constructor[] constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            throw new ScriptRuntimeException("Not found public constructor for class： " + clazz.getName());
        }
        if (constructors.length == 1) {
            return new NativeConstructorDeclare(constructors[0]);
        }
        ClassUtil.setAccessible(constructors);
        return new MultiNativeConstructorDeclare(constructors);
    }

    public FunctionDeclare createNativeMethodDeclare(Class<?> clazz, String methodName) {
        Method[] methods = ClassUtil.getPublicMethods(clazz, methodName);
        if (methods.length == 0) {
            throw new ScriptRuntimeException("Method not found： " + clazz.getName() + '#' + methodName);
        }
        if (methods.length == 1) {
            return createNativeMethodDeclare(methods[0]);
        }
        return createMultiNativeMethodDeclare(methods);
    }

    public FunctionDeclare createNativeMethodDeclare(Method method) {
        ClassUtil.setAccessible(method);
        return new NativeFunctionDeclare(method);
    }

    public FunctionDeclare createNativeMethodDeclare(List<Method> methods) {
        if (methods == null || methods.isEmpty()) {
            throw new IllegalArgumentException("methods must mot empty");
        }
        int size = methods.size();
        if (size == 1) {
            return createNativeMethodDeclare(methods.get(0));
        }
        return createMultiNativeMethodDeclare(methods.toArray(new Method[size]));
    }

    public FunctionDeclare createMultiNativeMethodDeclare(Method[] methods) {
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
        return mix ? new MixedMultiNativeFunctionDeclare(methods)
                : new MultiNativeFunctionDeclare(methods, isStatic);
    }

    protected FunctionDeclare createNativeConstructorDeclare(Constructor<?> constructor) {
        ClassUtil.setAccessible(constructor);
        return new NativeConstructorDeclare(constructor);
    }

    protected static ParseException createNotAccessiblePathException(String path, Position position) {
        return new ParseException("Not accessible of native path: ".concat(path), position);
    }
}
