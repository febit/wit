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
import org.febit.wit.runtime.Function;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.TextPosition;
import org.febit.wit.runtime.function.ConstructorNativeFunction;
import org.febit.wit.runtime.function.MethodNativeFunction;
import org.febit.wit.runtime.function.MultiConstructorNativeFunction;
import org.febit.wit.runtime.function.MultiMethodMixedNativeFunction;
import org.febit.wit.runtime.function.MultiMethodNativeFunction;
import org.febit.wit.runtime.function.NewArrayNativeFunction;
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

    protected final ConcurrentMap<Object, Function> cache = new ConcurrentHashMap<>();

    @Getter
    private final NativeSecurity security;

    public Function newArrayFunction(Class<?> componentType) {
        return newArrayFunction(componentType, TextPosition.UNKNOWN, true);
    }

    public Function newArrayFunction(Class<?> componentType, boolean checkAccess) {
        return newArrayFunction(componentType, TextPosition.UNKNOWN, checkAccess);
    }

    public Function newArrayFunction(Class<?> componentType, Position pos, boolean checkAccess) {
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
                throw createPathNotAccessibleException(path, pos);
            }
        }
        return new NewArrayNativeFunction(componentType);
    }

    public Function methodFunction(Class<?> clazz, String methodName) {
        return methodFunction(clazz, methodName, TextPosition.UNKNOWN, true);
    }

    public Function methodFunction(Class<?> clazz, String methodName, boolean checkAccess) {
        return methodFunction(clazz, methodName, TextPosition.UNKNOWN, checkAccess);
    }

    public Function methodFunction(Class<?> clazz, String methodName, Class<?> @Nullable [] paramTypes) {
        return methodFunction(clazz, methodName, paramTypes, TextPosition.UNKNOWN, true);
    }

    public Function methodFunction(Class<?> clazz, String methodName,
                                   Class<?>[] paramTypes, boolean checkAccess) {
        return methodFunction(clazz, methodName, paramTypes, TextPosition.UNKNOWN, checkAccess);
    }

    public Function methodFunction(Class<?> clazz, String methodName, Class<?> @Nullable [] paramTypes,
                                   Position position, boolean checkAccess) {
        if (checkAccess) {
            var path = clazz.getName() + '.' + methodName;
            if (!this.security.allowed(path)) {
                throw createPathNotAccessibleException(path, position);
            }
        }
        try {
            return methodFunction(clazz.getMethod(methodName, paramTypes));
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new ParseException(ex.getMessage(), ex, position);
        }
    }

    public Function methodFunction(Class<?> clazz, String methodName, Position pos, boolean checkAccess) {
        if (checkAccess) {
            var path = clazz.getName() + '.' + methodName;
            if (!this.security.allowed(path)) {
                throw createPathNotAccessibleException(path, pos);
            }
        }
        return createMethodFunction(clazz, methodName);
    }

    public Function constructorFunction(Class<?> clazz) {
        return constructorFunction(clazz, TextPosition.UNKNOWN, true);
    }

    public Function constructorFunction(Class<?> clazz, boolean checkAccess) {
        return constructorFunction(clazz, TextPosition.UNKNOWN, checkAccess);
    }

    public Function constructorFunction(Class<?> clazz, Class<?> @Nullable [] paramTypes) {
        return constructorFunction(clazz, paramTypes, TextPosition.UNKNOWN, true);
    }

    public Function constructorFunction(Class<?> clazz, Class<?> @Nullable [] paramTypes, boolean checkAccess) {
        return constructorFunction(clazz, paramTypes, TextPosition.UNKNOWN, checkAccess);
    }

    public Function constructorFunction(
            Class<?> clazz,
            Class<?> @Nullable [] paramTypes,
            Position position,
            boolean checkAccess
    ) {
        if (checkAccess) {
            var path = clazz.getName().concat(".<init>");
            if (!this.security.allowed(path)) {
                throw createPathNotAccessibleException(path, position);
            }
        }
        try {
            return constructorFunction(clazz.getConstructor(paramTypes));
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new ParseException(ex.getMessage(), ex, position);
        }
    }

    public Function constructorFunction(Class<?> clazz, Position pos, boolean checkAccess) {
        if (checkAccess) {
            var path = clazz.getName().concat(".<init>");
            if (!this.security.allowed(path)) {
                throw createPathNotAccessibleException(path, pos);
            }
        }
        return createConstructorFunction(clazz);
    }

    public Function methodFunction(Method method) {
        return cache.computeIfAbsent(method,
                m -> createMethodFunction((Method) m));
    }

    public Function constructorFunction(Constructor<?> constructor) {
        return cache.computeIfAbsent(constructor,
                c -> createConstructorFunction((Constructor<?>) c));
    }

    protected Function createConstructorFunction(Class<?> clazz) {
        var constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            throw new ScriptEvaluateException("No such public constructor for class: " + clazz.getName());
        }
        if (constructors.length == 1) {
            return new ConstructorNativeFunction(constructors[0]);
        }
        ClassUtils.setAccessible(constructors);
        return new MultiConstructorNativeFunction(constructors);
    }

    protected Function createMethodFunction(Class<?> clazz, String methodName) {
        var methods = ClassUtils.getPublicMethods(clazz, methodName);
        if (methods.length == 0) {
            throw new ScriptEvaluateException("No such method: " + clazz.getName() + '#' + methodName);
        }
        if (methods.length == 1) {
            return createMethodFunction(methods[0]);
        }
        return createMultiMethodFunction(methods);
    }

    public Function createMethodFunction(Method method) {
        method.trySetAccessible();
        return new MethodNativeFunction(method);
    }

    public Function createMethodsFunction(@Nullable List<Method> methods) {
        if (methods == null || methods.isEmpty()) {
            throw new IllegalArgumentException("methods must mot empty");
        }
        int size = methods.size();
        if (size == 1) {
            return createMethodFunction(methods.get(0));
        }
        return createMultiMethodFunction(methods.toArray(new Method[size]));
    }

    protected Function createMultiMethodFunction(Method @Nullable [] methods) {
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
        return mix ? new MultiMethodMixedNativeFunction(methods)
                : new MultiMethodNativeFunction(methods, isStatic);
    }

    protected Function createConstructorFunction(Constructor<?> constructor) {
        constructor.trySetAccessible();
        return new ConstructorNativeFunction(constructor);
    }

    protected static ParseException createPathNotAccessibleException(String path, Position position) {
        return new ParseException("Not accessible of native path: " + path, position);
    }
}
