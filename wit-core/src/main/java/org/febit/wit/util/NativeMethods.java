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
package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.AmbiguousMethodException;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.Undefined;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings({
        "java:S135", // Loops should not contain more than a single "break" or "continue" statement
        "java:S1452", // Generic wildcard types should not be used in return types
})
@UtilityClass
public class NativeMethods {

    private static final int DISTANCE_NEVER = -1;
    private static final int DISTANCE_EXACT = 0;
    private static final int DISTANCE_ASSIGNABLE = 1;
    private static final int DISTANCE_OBJECT = 100;
    private static final int DISTANCE_PRIMITIVE = 10;
    // XXX DISTANCE_CONVERT = 1000
    private static final int DISTANCE_NULL = 1000000;

    private static final Class<?>[] EMPTY_ARG_TYPES = new Class<?>[0];

    public static Stream<Method> find(Class<?> target, String name) {
        var methods = new HashMap<MethodKey, Method>();
        for (var method : target.getMethods()) {
            if (!method.getName().equals(name)) {
                continue;
            }
            var key = new MethodKey(method.getParameterTypes());
            methods.merge(key, method, NativeMethods::preferOverrideWinner);
        }
        return methods.values().stream();
    }

    private record MethodKey(Class<?>[] parameterTypes) {

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof MethodKey omk)) {
                return false;
            }
            return Arrays.equals(parameterTypes, omk.parameterTypes);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(parameterTypes);
        }
    }

    private static Method preferOverrideWinner(Method left, Method right) {
        if (left.isBridge() && !right.isBridge()) {
            return right;
        }
        if (right.isBridge() && !left.isBridge()) {
            return left;
        }
        var leftDeclaring = left.getDeclaringClass();
        var rightDeclaring = right.getDeclaringClass();
        if (leftDeclaring.isAssignableFrom(rightDeclaring)) {
            return right;
        }
        return left;
    }

    @Nullable
    public static Object invoke(Method method, @Nullable Object @Nullable [] args) {
        if (Modifiers.isStatic(method)) {
            return invoke(method, null, args, 0);
        }
        if (args == null || args.length == 0 || args[0] == null) {
            throw new ScriptEvaluateException("this method need one argument at least");
        }
        return invoke(method, args[0], args, 1);
    }

    @Nullable
    public static Object invoke(
            final Method method, @Nullable Object self, @Nullable Object @Nullable [] args, int from
    ) {
        var methodArgs = fitArgs(args, method.getParameterCount(), from);
        try {
            Object result = method.invoke(self, methodArgs);
            return ClassUtils.isVoidType(method.getReturnType())
                    ? Undefined.UNDEFINED
                    : result;
        } catch (IllegalAccessException ex) {
            throw new ScriptEvaluateException("this method is inaccessible: " + ex.getLocalizedMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ScriptEvaluateException("illegal argument: " + ex.getLocalizedMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new ScriptEvaluateException("this method throws an exception", ex);
        }
    }

    public static Object invoke(
            Constructor<?> constructor, @Nullable Object @Nullable [] args
    ) {
        var methodArgs = fitArgs(args, constructor.getParameterCount(), 0);
        try {
            return constructor.newInstance(methodArgs);
        } catch (InstantiationException ex) {
            throw new ScriptEvaluateException("Can't create new instance: " + ex.getLocalizedMessage(), ex);
        } catch (IllegalAccessException ex) {
            throw new ScriptEvaluateException("Inaccessible method: " + ex.getLocalizedMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ScriptEvaluateException("Illegal arguments: " + ex.getLocalizedMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new ScriptEvaluateException("this method throws an exception", ex);
        }
    }

    private static @Nullable Class<?>[] argTypes(@Nullable Object @Nullable [] args) {
        if (args == null || args.length == 0) {
            return EMPTY_ARG_TYPES;
        }

        @Nullable
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < argTypes.length; i++) {
            var arg = args[i];
            argTypes[i] = arg != null ? arg.getClass() : null;
        }
        return argTypes;
    }

    private static @Nullable Object[] fitArgs(
            @Nullable Object @Nullable [] args, int expectedSize, final int from) {
        if (expectedSize == 0) {
            return Args.empty();
        }
        if (args == null) {
            return new Object[expectedSize];
        }
        if (from == 0 && args.length == expectedSize) {
            return args;
        }
        if (from >= args.length) {
            return new Object[expectedSize];
        }
        var fit = new Object[expectedSize];
        System.arraycopy(args, from, fit, 0, Math.min(args.length - from, expectedSize));
        return fit;
    }

    /**
     * Choose the most suitable method from the candidates.
     *
     * @param executables executables
     * @param args        args
     * @param from        arg offset used to match method parameters
     * @return null if not found
     */
    @Nullable
    public static <T extends Executable> T choose(
            List<T> executables, @Nullable Object @Nullable [] args, int from) {
        return choose(executables, NativeMethods::distance, argTypes(args), from);
    }

    /**
     * Choose the most suitable method from the candidates.
     *
     * @param executables executables
     * @param args        if mixed, first arg is the host of member methods.
     * @param mix         if mix, static methods with member methods
     * @return null if not found
     */
    @Nullable
    public static <T extends Executable> T choose(
            List<T> executables, @Nullable Object @Nullable [] args, boolean mix) {
        return choose(executables,
                mix ? NativeMethods::distanceMix : NativeMethods::distance,
                argTypes(args),
                0
        );
    }

    /**
     * Choose the most suitable method from the candidates.
     *
     * @param executables executables
     * @param argTypes    argTypes
     * @param from        arg offset used to match method parameters
     * @return null if not found
     */
    @SuppressWarnings({
            "unchecked",
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    @Nullable
    static <T extends Executable> T choose(
            List<T> executables,
            DistanceCalculator<T> distanceCalculator,
            @Nullable Class<?>[] argTypes, int from) {
        if (executables.isEmpty()) {
            return null;
        }
        var candidate = new Executable[executables.size()];
        int candidateCount = 0;
        int leastDistance = Integer.MAX_VALUE;

        for (var exec : executables) {
            int distance = distanceCalculator.distance(exec, argTypes, from);
            if (distance < 0) {
                continue;
            }
            if (distance == leastDistance) {
                candidate[candidateCount++] = exec;
            } else if (distance < leastDistance) {
                leastDistance = distance;
                candidate[0] = exec;
                candidateCount = 1;
            }
        }
        if (candidateCount > 1) {
            if (argTypes.length == 0) {
                return null;
            }
            var method = tryResolveAmbiguous(candidate, candidateCount);
            if (method != null) {
                return (T) method;
            }
            throw AmbiguousMethodException.of(
                    Arrays.copyOf(candidate, candidateCount),
                    argTypes);
        }
        return (T) candidate[0];
    }

    @FunctionalInterface
    interface DistanceCalculator<T> {
        int distance(T exec, @Nullable Class<?>[] actuals, int actualOffset);
    }

    static int distanceMix(Executable exec, @Nullable Class<?>[] actuals, int actualOffset) {
        if (Modifiers.isStatic(exec)) {
            return distance(exec.getParameterTypes(), actuals, actualOffset);
        }
        if (actuals.length <= actualOffset) {
            return DISTANCE_NEVER;
        }
        var hostType = actuals[actualOffset];
        if (hostType == null
                || !exec.getDeclaringClass().isAssignableFrom(hostType)) {
            return DISTANCE_NEVER;
        }
        return distance(exec.getParameterTypes(), actuals, actualOffset + 1);
    }

    static int distance(Executable exec, @Nullable Class<?>[] actuals, int actualOffset) {
        return distance(exec.getParameterTypes(), actuals, actualOffset);
    }

    /**
     * @param executables executables
     * @param count       count
     * @return null if can't resolve
     */
    @Nullable
    private static <T extends Executable> T tryResolveAmbiguous(T[] executables, int count) {
        var candidate = executables[0];
        var candidateArgs = candidate.getParameterTypes();
        for (int i = 1; i < count; i++) {
            var next = executables[i];
            if (Modifiers.isStatic(candidate) != Modifiers.isStatic(next)) {
                // current not support
                return null;
            }
            Class<?>[] nextArgs = next.getParameterTypes();
            int distance = distance(candidateArgs, nextArgs, 0);
            if (distance == 0) {
                return null;
            }
            if (distance > 0) {
                candidate = next;
                candidateArgs = nextArgs;
            } else if (distance(nextArgs, candidateArgs, 0) <= 0) {
                // ambiguous
                return null;
            }
        }
        return candidate;
    }

    private static int distance(Class<?>[] expects, @Nullable Class<?>[] actuals, int actualOffset) {
        int actualSize = actuals.length - actualOffset;
        if (actualSize > expects.length) {
            return DISTANCE_NEVER;
        }
        int total = (expects.length - actualSize) * DISTANCE_NULL;
        for (int i = 0; i < actualSize; i++) {
            int distance = distance(expects[i], actuals[actualOffset + i]);
            if (distance < 0) {
                return DISTANCE_NEVER;
            }
            total += distance;
        }
        return total;
    }

    private static int distance(Class<?> expect, @Nullable Class<?> actual) {
        if (actual == null) {
            return expect.isPrimitive() ? DISTANCE_NEVER : DISTANCE_NULL;
        }
        if (actual.equals(expect)) {
            return DISTANCE_EXACT;
        }
        if (expect.isPrimitive()) {
            return actual == ClassUtils.boxedType(expect)
                    ? DISTANCE_PRIMITIVE
                    : DISTANCE_NEVER;
        }
        if (actual.isPrimitive()) {
            return expect == ClassUtils.boxedType(actual)
                    ? DISTANCE_PRIMITIVE
                    : DISTANCE_NEVER;
        }
        if (expect.isAssignableFrom(actual)) {
            return expect == Object.class ? DISTANCE_OBJECT : DISTANCE_ASSIGNABLE;
        }
        //TODO: support auto convert
        return DISTANCE_NEVER;
    }

}
