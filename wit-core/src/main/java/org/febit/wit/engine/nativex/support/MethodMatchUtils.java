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
import org.febit.wit.exception.AmbiguousMethodException;
import org.febit.wit.util.ClassUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Executable;
import java.util.List;
import java.util.stream.Stream;

@UtilityClass
public class MethodMatchUtils {

    private static final Class<?>[] EMPTY_ARG_TYPES = new Class<?>[0];

    private static final int DISTANCE_NEVER = -1;
    private static final int DISTANCE_EXACT = 0;
    private static final int DISTANCE_ASSIGNABLE = 1;
    private static final int DISTANCE_OBJECT = 100;
    private static final int DISTANCE_PRIMITIVE = 10;
    // XXX DISTANCE_CONVERT = 1000
    private static final int DISTANCE_NULL = 1000000;

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

    /**
     * Choose the most suitable method from the candidates.
     *
     * @param executables executables
     * @param args        args
     * @param argsOffset  args offset used to match method parameters
     * @return null if not found
     */
    @Nullable
    public static <T extends ExecutableAware<?>> T findBest(
            List<T> executables, @Nullable Object @Nullable [] args, int argsOffset) {
        return findBest(executables, MethodMatchUtils::distance, argTypes(args), argsOffset);
    }

    /**
     * Choose the most suitable method from the candidates.
     *
     * @param executables executables
     * @param args        if mixed, first arg is the host of member methods.
     * @return null if not found
     */
    @Nullable
    public static <T extends ExecutableAware<?>> T findMixedBest(
            List<T> executables, @Nullable Object @Nullable [] args) {
        return findBest(executables, MethodMatchUtils::distanceMix, argTypes(args), 0);
    }

    /**
     * Choose the most suitable method from the candidates.
     *
     * @param executables executables
     * @param argTypes    argTypes
     * @param argsOffset  args offset used to match method parameters
     * @return null if not found
     */
    @SuppressWarnings({
            "unchecked",
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    @Nullable
    public static <T extends ExecutableAware<?>> T findBest(
            List<T> executables,
            DistanceCalculator<T> distanceCalculator,
            @Nullable Class<?>[] argTypes, int argsOffset) {
        if (executables.isEmpty()) {
            return null;
        }
        var candidate = new ExecutableAware[executables.size()];
        int candidateCount = 0;
        int leastDistance = Integer.MAX_VALUE;

        for (var exec : executables) {
            int distance = distanceCalculator.distance(exec, argTypes, argsOffset);
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
            var result = tryResolveAmbiguous(candidate, candidateCount);
            if (result != null) {
                return (T) result;
            }
            throw AmbiguousMethodException.of(
                    Stream.of(candidate)
                            .limit(candidateCount)
                            .map(ExecutableAware::executable)
                            .toArray(Executable[]::new),
                    argTypes);
        }
        return (T) candidate[0];
    }

    public static int distanceMix(ExecutableAware<?> exec, @Nullable Class<?>[] actuals, int actualOffset) {
        if (exec.isStatic()) {
            return distance(exec.parameterTypes(), actuals, actualOffset);
        }
        if (actuals.length <= actualOffset) {
            return DISTANCE_NEVER;
        }
        var hostType = actuals[actualOffset];
        if (hostType == null
                || !exec.declaringClass().isAssignableFrom(hostType)) {
            return DISTANCE_NEVER;
        }
        return distance(exec.parameterTypes(), actuals, actualOffset + 1);
    }

    public static int distance(ExecutableAware<?> exec, @Nullable Class<?>[] actuals, int actualOffset) {
        return distance(exec.parameterTypes(), actuals, actualOffset);
    }

    /**
     * @param executables executables
     * @param count       count
     * @return null if can't resolve
     */
    @Nullable
    private static <T extends ExecutableAware<?>> T tryResolveAmbiguous(T[] executables, int count) {
        var candidate = executables[0];
        var candidateArgs = candidate.parameterTypes();
        for (int i = 1; i < count; i++) {
            var next = executables[i];
            if (candidate.isStatic() != next.isStatic()) {
                // current not support
                return null;
            }
            Class<?>[] nextArgs = next.parameterTypes();
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
