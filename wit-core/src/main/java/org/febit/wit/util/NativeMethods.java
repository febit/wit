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
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

@SuppressWarnings({
        "java:S135", // Loops should not contain more than a single "break" or "continue" statement
        "java:S1452", // Generic wildcard types should not be used in return types
})
@UtilityClass
public class NativeMethods {

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

}
