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
import org.febit.wit.exception.UncheckedException;
import org.febit.wit.parser.NativeFunctionFactory;
import org.febit.wit.runtime.heap.Heap;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@UtilityClass
public class HeapNativeUtils {

    @SuppressWarnings("UnusedReturnValue")
    public static int collectStaticMethods(
            Heap target, NativeFunctionFactory functionFactory, Class<?> type) {
        return collectStaticMethods(target, functionFactory, type, false);
    }

    public static int collectStaticMethods(
            Heap target,
            NativeFunctionFactory functionFactory,
            Class<?> type,
            boolean ignoreIfPresent
    ) {
        var methodMap = Arrays.stream(type.getMethods())
                .filter(ClassUtils::isStatic)
                .filter(m -> !(ignoreIfPresent && target.has(m.getName())))
                .collect(Collectors.groupingBy(Method::getName));

        methodMap.forEach((name, methods) ->
                target.set(name, functionFactory.method(methods))
        );
        return methodMap.size();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static int collectConstFields(Heap target, Class<?> type) {
        return collectConstFields(target, type, false);
    }

    public static int collectConstFields(
            Heap target,
            Class<?> type,
            boolean ignoreIfPresent
    ) {
        var fields = Arrays.stream(type.getFields())
                .filter(ClassUtils::isStatic)
                .filter(ClassUtils::isFinal)
                .filter(f -> !(ignoreIfPresent && target.has(f.getName())))
                .toList();

        fields.forEach(f -> {
            f.trySetAccessible();
            try {
                target.set(f.getName(), f.get(null));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new UncheckedException(e);
            }
        });
        return fields.size();
    }
}
