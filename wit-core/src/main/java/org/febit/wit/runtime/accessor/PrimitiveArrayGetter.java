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
package org.febit.wit.runtime.accessor;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.ALU;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface PrimitiveArrayGetter<T> extends Getter<T> {

    @Nullable
    Object getValue(T array, int idx);

    @Nullable
    @Override
    default Object get(T array, @Nullable Object property) {
        if (property == null) {
            throw new ScriptEvaluateException("Array index is null.");
        }
        if (property instanceof Number idx) {
            return getValue(array, idx.intValue());
        }
        return switch (property.toString()) {
            case "size", "length" -> ALU.size(array);
            case "isEmpty" -> ALU.size(array) == 0;
            default -> throw new ScriptEvaluateException("Invalid property: array#" + property);
        };
    }

}
