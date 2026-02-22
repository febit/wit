// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.febit.wit.exception.ParseException;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.parser.security.NativeSecurity;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.TextPosition;
import org.febit.wit.runtime.function.FunctionDeclare;
import org.febit.wit.runtime.function.MixedMultiNativeFunctionDeclare;
import org.febit.wit.runtime.function.MultiNativeConstructorDeclare;
import org.febit.wit.runtime.function.MultiNativeFunctionDeclare;
import org.febit.wit.runtime.function.NativeConstructorDeclare;
import org.febit.wit.runtime.function.NativeFunctionDeclare;
import org.febit.wit.runtime.function.NativeNewArrayDeclare;
import org.febit.wit.util.ClassUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class NativeFactory {

    protected final ConcurrentMap<Object, FunctionDeclare> cache = new ConcurrentHashMap<>();

    @Getter
    private final NativeSecurity security;

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
        if (ClassUtils.isVoidType(classForCheck)) {
            throw new ParseException("ComponentType must not void", pos);
        }
        if (checkAccess) {
            var path = classForCheck.getName().concat(".[]");
            if (!this.security.allowed(path)) {
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

    public FunctionDeclare getNativeMethodDeclare(Class<?> clazz, String methodName, Class<?> @Nullable [] paramTypes) {
        return getNativeMethodDeclare(clazz, methodName, paramTypes, TextPosition.UNKNOWN, true);
    }

    public FunctionDeclare getNativeMethodDeclare(Class<?> clazz, String methodName,
                                                  Class<?>[] paramTypes, boolean checkAccess) {
        return getNativeMethodDeclare(clazz, methodName, paramTypes, TextPosition.UNKNOWN, checkAccess);
    }

    public FunctionDeclare getNativeMethodDeclare(Class<?> clazz, String methodName, Class<?> @Nullable [] paramTypes,
                                                  Position position, boolean checkAccess) {
        if (checkAccess) {
            var path = clazz.getName() + '.' + methodName;
            if (!this.security.allowed(path)) {
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
            var path = clazz.getName() + '.' + methodName;
            if (!this.security.allowed(path)) {
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

    public FunctionDeclare getNativeConstructorDeclare(Class<?> clazz, Class<?> @Nullable [] paramTypes) {
        return getNativeConstructorDeclare(clazz, paramTypes, TextPosition.UNKNOWN, true);
    }

    public FunctionDeclare getNativeConstructorDeclare(Class<?> clazz, Class<?> @Nullable [] paramTypes, boolean checkAccess) {
        return getNativeConstructorDeclare(clazz, paramTypes, TextPosition.UNKNOWN, checkAccess);
    }

    public FunctionDeclare getNativeConstructorDeclare(
            Class<?> clazz,
            Class<?> @Nullable [] paramTypes,
            Position position,
            boolean checkAccess
    ) {
        if (checkAccess) {
            var path = clazz.getName().concat(".<init>");
            if (!this.security.allowed(path)) {
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
            var path = clazz.getName().concat(".<init>");
            if (!this.security.allowed(path)) {
                throw createNotAccessiblePathException(path, pos);
            }
        }
        return createNativeConstructorDeclare(clazz);
    }

    public FunctionDeclare getNativeMethodDeclare(Method method) {
        FunctionDeclare declare = cache.get(method);
        if (declare == null) {
            declare = createNativeMethodDeclare(method);
            FunctionDeclare old = cache.putIfAbsent(method, declare);
            if (old != null) {
                return old;
            }
        }
        return declare;
    }

    public FunctionDeclare getNativeConstructorDeclare(Constructor<?> constructor) {
        var declare = cache.get(constructor);
        if (declare == null) {
            declare = createNativeConstructorDeclare(constructor);
            FunctionDeclare old = cache.putIfAbsent(constructor, declare);
            if (old != null) {
                return old;
            }
        }
        return declare;
    }

    public FunctionDeclare createNativeConstructorDeclare(Class<?> clazz) {
        var constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            throw new ScriptEvaluateException("No such public constructor for class: " + clazz.getName());
        }
        if (constructors.length == 1) {
            return new NativeConstructorDeclare(constructors[0]);
        }
        ClassUtils.setAccessible(constructors);
        return new MultiNativeConstructorDeclare(constructors);
    }

    public FunctionDeclare createNativeMethodDeclare(Class<?> clazz, String methodName) {
        var methods = ClassUtils.getPublicMethods(clazz, methodName);
        if (methods.length == 0) {
            throw new ScriptEvaluateException("No such method: " + clazz.getName() + '#' + methodName);
        }
        if (methods.length == 1) {
            return createNativeMethodDeclare(methods[0]);
        }
        return createMultiNativeMethodDeclare(methods);
    }

    public FunctionDeclare createNativeMethodDeclare(Method method) {
        method.trySetAccessible();
        return new NativeFunctionDeclare(method);
    }

    public FunctionDeclare createNativeMethodDeclare(@Nullable List<Method> methods) {
        if (methods == null || methods.isEmpty()) {
            throw new IllegalArgumentException("methods must mot empty");
        }
        int size = methods.size();
        if (size == 1) {
            return createNativeMethodDeclare(methods.get(0));
        }
        return createMultiNativeMethodDeclare(methods.toArray(new Method[size]));
    }

    public FunctionDeclare createMultiNativeMethodDeclare(Method @Nullable [] methods) {
        if (methods == null || methods.length == 0) {
            throw new IllegalArgumentException("methods must mot empty");
        }
        ClassUtils.setAccessible(methods);
        var isStatic = ClassUtils.isStatic(methods[0]);
        boolean mix = false;
        for (int i = 1; i < methods.length; i++) {
            if (isStatic != ClassUtils.isStatic(methods[0])) {
                mix = true;
                break;
            }
        }
        return mix ? new MixedMultiNativeFunctionDeclare(methods)
                : new MultiNativeFunctionDeclare(methods, isStatic);
    }

    protected FunctionDeclare createNativeConstructorDeclare(Constructor<?> constructor) {
        constructor.trySetAccessible();
        return new NativeConstructorDeclare(constructor);
    }

    protected static ParseException createNotAccessiblePathException(String path, Position position) {
        return new ParseException("Not accessible of native path: " + path, position);
    }
}
