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
package org.febit.wit.engine.nativex.function;

import org.febit.wit.engine.WitFunction;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.util.ClassUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;

public record NewArrayNativeFunction(
        Class<?> componentType
) implements WitFunction.Constable {

    @Override
    public Object apply(@Nullable Object @Nullable [] args) {
        var size = resolveSize(args);
        return Array.newInstance(componentType, size);
    }

    private int resolveSize(@Nullable Object @Nullable [] args) {
        if (args == null || args.length == 0) {
            return 0;
        }

        var arg0 = args[0];
        if (!(arg0 instanceof Number number)) {
            throw new ScriptEvaluateException(
                    "A number is expected as array's length, but got: " + ClassUtils.nameOf(arg0));
        }

        var size = number.intValue();
        if (size < 0) {
            throw new ScriptEvaluateException(
                    "A non-negative number is expected as array's length, but got: " + size);
        }
        return size;
    }
}
