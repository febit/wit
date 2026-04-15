/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.engine.nativex.support;

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.UncheckedException;
import org.febit.wit.util.Args;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.MethodHandleUtils;
import org.febit.wit.util.Modifiers;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@UtilityClass
public class MethodInvokerUtils {

    private static final MethodType MT_0 = MethodType.methodType(Object.class);
    private static final MethodType MT_1 = MethodType.methodType(Object.class, Object.class);
    private static final MethodType MT_2 = MethodType.methodType(
            Object.class, Object.class, Object.class);
    private static final MethodType MT_3 = MethodType.methodType(
            Object.class, Object.class, Object.class, Object.class);
    private static final MethodType MT_4 = MethodType.methodType(
            Object.class, Object.class, Object.class, Object.class, Object.class);
    private static final MethodType MT_5 = MethodType.methodType(
            Object.class, Object.class, Object.class, Object.class, Object.class,
            Object.class);
    private static final MethodType MT_6 = MethodType.methodType(
            Object.class, Object.class, Object.class, Object.class, Object.class,
            Object.class, Object.class);
    private static final MethodType MT_7 = MethodType.methodType(
            Object.class, Object.class, Object.class, Object.class, Object.class,
            Object.class, Object.class, Object.class);
    private static final MethodType MT_8 = MethodType.methodType(
            Object.class, Object.class, Object.class, Object.class, Object.class,
            Object.class, Object.class, Object.class, Object.class);
    private static final MethodType MT_9 = MethodType.methodType(
            Object.class, Object.class, Object.class, Object.class, Object.class,
            Object.class, Object.class, Object.class, Object.class, Object.class);

    public static MethodInvoker of(Method method) {
        var handle = MethodInvokerUtils.handleOf(method);
        var invoker = MethodInvokerUtils.invokerOf(handle);
        var parameterCount = method.getParameterCount();
        var varargsComponentType = method.isVarArgs()
                ? method.getParameterTypes()[parameterCount - 1].getComponentType()
                : null;

        var actualParameterCount = Modifiers.isStatic(method)
                ? parameterCount : parameterCount + 1;
        return new MethodInvoker(
                handle,
                invoker,
                actualParameterCount,
                ClassUtils.isVoidType(method.getReturnType()),
                varargsComponentType
        );
    }

    public static MethodInvoker of(Constructor<?> constructor) {
        var handle = MethodInvokerUtils.handleOf(constructor);
        var invoker = MethodInvokerUtils.invokerOf(handle);
        var parameterCount = constructor.getParameterCount();
        var varargsComponentType = constructor.isVarArgs()
                ? constructor.getParameterTypes()[parameterCount - 1].getComponentType()
                : null;
        return new MethodInvoker(
                handle,
                invoker,
                parameterCount,
                false,
                varargsComponentType
        );
    }

    static @Nullable Object[] fitArgs(
            @Nullable Object @Nullable [] args,
            int expectedSize,
            @Nullable Class<?> varargsComponentType
    ) {
        if (expectedSize == 0) {
            return Args.empty();
        }
        if (args == null) {
            var fit = new Object[expectedSize];
            if (varargsComponentType != null) {
                fit[expectedSize - 1] = Array.newInstance(varargsComponentType, 0);
            }
            return fit;
        }
        if (args.length < expectedSize) {
            var fit = new Object[expectedSize];
            System.arraycopy(args, 0, fit, 0, args.length);
            if (varargsComponentType != null) {
                fit[expectedSize - 1] = Array.newInstance(varargsComponentType, 0);
            }
            return fit;
        }
        // TODO collect varargs into array if args.length >= expectedSize
        return args;
    }

    private static MethodHandle handleOf(Method method) {
        try {
            var original = MethodHandleUtils.lookupOf(method.getDeclaringClass())
                    .unreflect(method);
            return asGeneric(original);
        } catch (IllegalAccessException e) {
            throw new UncheckedException(e);
        }
    }

    private static MethodHandle handleOf(Constructor<?> method) {
        try {
            var original = MethodHandleUtils.lookupOf(method.getDeclaringClass())
                    .unreflectConstructor(method);
            return asGeneric(original);
        } catch (IllegalAccessException e) {
            throw new UncheckedException(e);
        }
    }

    private static MethodHandle asGeneric(MethodHandle handle) {
        var type = handle.type();
        return switch (type.parameterCount()) {
            case 0 -> handle.withVarargs(false).asType(MT_0);
            case 1 -> handle.withVarargs(false).asType(MT_1);
            case 2 -> handle.withVarargs(false).asType(MT_2);
            case 3 -> handle.withVarargs(false).asType(MT_3);
            case 4 -> handle.withVarargs(false).asType(MT_4);
            case 5 -> handle.withVarargs(false).asType(MT_5);
            case 6 -> handle.withVarargs(false).asType(MT_6);
            case 7 -> handle.withVarargs(false).asType(MT_7);
            case 8 -> handle.withVarargs(false).asType(MT_8);
            case 9 -> handle.withVarargs(false).asType(MT_9);
            default -> handle;
        };
    }

    private static MethodInvoker.Handler invokerOf(MethodHandle handle) {
        var type = handle.type();
        return switch (type.parameterCount()) {
            case 0 -> MethodInvokerUtils::invoke0;
            case 1 -> MethodInvokerUtils::invoke1;
            case 2 -> MethodInvokerUtils::invoke2;
            case 3 -> MethodInvokerUtils::invoke3;
            case 4 -> MethodInvokerUtils::invoke4;
            case 5 -> MethodInvokerUtils::invoke5;
            case 6 -> MethodInvokerUtils::invoke6;
            case 7 -> MethodInvokerUtils::invoke7;
            case 8 -> MethodInvokerUtils::invoke8;
            case 9 -> MethodInvokerUtils::invoke9;
            default -> MethodInvokerUtils::invokeN;
        };
    }

    private static Object invoke0(MethodHandle handle, @Nullable Object[] args) throws Throwable {
        return handle.invokeExact();
    }

    private static Object invoke1(MethodHandle handle, @Nullable Object[] args) throws Throwable {
        return handle.invokeExact(args[0]);
    }

    private static Object invoke2(MethodHandle handle, @Nullable Object[] args) throws Throwable {
        return handle.invokeExact(
                args[0],
                args[1]
        );
    }

    private static Object invoke3(MethodHandle handle, @Nullable Object[] args) throws Throwable {
        return handle.invokeExact(
                args[0],
                args[1],
                args[2]
        );
    }

    private static Object invoke4(MethodHandle handle, @Nullable Object[] args) throws Throwable {
        return handle.invokeExact(
                args[0],
                args[1],
                args[2],
                args[3]
        );
    }

    private static Object invoke5(MethodHandle handle, @Nullable Object[] args) throws Throwable {
        return handle.invokeExact(
                args[0],
                args[1],
                args[2],
                args[3],
                args[4]
        );
    }

    private static Object invoke6(MethodHandle handle, @Nullable Object[] args) throws Throwable {
        return handle.invokeExact(
                args[0],
                args[1],
                args[2],
                args[3],
                args[4],
                args[5]
        );
    }

    private static Object invoke7(MethodHandle handle, @Nullable Object[] args) throws Throwable {
        return handle.invokeExact(
                args[0],
                args[1],
                args[2],
                args[3],
                args[4],
                args[5],
                args[6]
        );
    }

    private static Object invoke8(MethodHandle handle, @Nullable Object[] args) throws Throwable {
        return handle.invokeExact(
                args[0],
                args[1],
                args[2],
                args[3],
                args[4],
                args[5],
                args[6],
                args[7]
        );
    }

    private static Object invoke9(MethodHandle handle, @Nullable Object[] args) throws Throwable {
        return handle.invokeExact(
                args[0],
                args[1],
                args[2],
                args[3],
                args[4],
                args[5],
                args[6],
                args[7],
                args[8]
        );
    }

    private static Object invokeN(MethodHandle handle, @Nullable Object[] args) throws Throwable {
        return handle.invokeWithArguments(args);
    }
}
