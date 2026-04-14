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

import org.febit.wit.engine.accessor.Getter;
import org.febit.wit.engine.accessor.Setter;
import org.febit.wit.exception.ScriptEvaluateException;
import org.jspecify.annotations.Nullable;

public class ObjectArrayAccessor implements Getter<Object[]>, Setter<Object[]> {

    @Nullable
    @Override
    public Object get(@Nullable Object[] array, @Nullable Object property) {
        if (property == null) {
            throw new ScriptEvaluateException("property/index should not be null for array access.");
        }
        if (property instanceof Number idx) {
            try {
                return array[idx.intValue()];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ScriptEvaluateException("index out of bounds: " + idx, e);
            }
        }
        return switch (property.toString()) {
            case "size", "length" -> array.length;
            case "isEmpty" -> array.length == 0;
            default -> throw new ScriptEvaluateException("Unsupported property for array access: " + property);
        };
    }

    @Override
    public void set(@Nullable Object[] array, @Nullable Object property, @Nullable Object value) {
        if (!(property instanceof Number idx)) {
            throw new ScriptEvaluateException("property/index should be a number for array access.");
        }
        try {
            array[idx.intValue()] = value;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ScriptEvaluateException("index out of bounds: " + idx, e);
        }
    }
}
